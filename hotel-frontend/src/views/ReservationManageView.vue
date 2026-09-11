<script setup>
import { computed, onMounted, ref, watch } from "vue";

const RESTAURANT_API_URL = "/api/restaurant";
const TIME_API_URL = "/api/restaurant_times";
const RESERVATION_API_URL = "/api/reservations";
const MEMBER_API_URL = "/api/members";
const BACKUP_API_URL = "/api/restaurant-backup";

const importInput = ref(null);
const importing = ref(false);
const filterRestaurantId = ref("");
const filterTimeId = ref("");
const filterDate = ref("");
const currentPage = ref(1);
const pageSize = 10;

const restaurants = ref([]);
const allTimes = ref([]);
const timeOptions = ref([]);
const reservations = ref([]);

const message = ref("");
const messageType = ref("");
const formTitle = ref("新增訂位");

const loading = ref(false);
const saving = ref(false);
const memberLoaded = ref(false);
const testingSms = ref(null);
const modalOpen = ref(false);
const filterModalOpen = ref(false);

const form = ref(createEmptyForm());

function createEmptyForm() {
    return {
        reservationId: null,
        memberId: "",
        contactName: "",
        contactPhone: "",
        restaurantId: "",
        reservationDate: "",
        timeId: "",
        peopleCount: "",
        status: "已訂位",
    };
}

// 呼叫後端時帶入登入 Token。
function getAuthHeaders() {
    const token = localStorage.getItem("token");

    const headers = {
        "Content-Type": "application/json",
    };

    if (token) {
        headers.Authorization = "Bearer " + token;
    }

    return headers;
}

const hasMember = computed(() => {
    return String(form.value.memberId ?? "").trim() !== "";
});

const filterTimeOptions = computed(() => {
    if (!filterRestaurantId.value) {
        return allTimes.value;
    }

    return allTimes.value.filter(
        (time) => Number(time.restaurantId) === Number(filterRestaurantId.value),
    );
});

const filteredReservations = computed(() => {
    return reservations.value
        .filter((reservation) => {
            const matchesRestaurant =
                !filterRestaurantId.value
                || Number(reservation.restaurantId) === Number(filterRestaurantId.value);

            const matchesTime =
                !filterTimeId.value
                || Number(reservation.timeId) === Number(filterTimeId.value);

            const matchesDate =
                !filterDate.value || reservation.reservationDate === filterDate.value;

            return matchesRestaurant && matchesTime && matchesDate;
        })
        .sort((first, second) => {
            const lastStatuses = ["已逾期", "已取消"];
            const firstShouldBeLast = lastStatuses.includes(getDisplayStatus(first));
            const secondShouldBeLast = lastStatuses.includes(getDisplayStatus(second));

            return Number(firstShouldBeLast) - Number(secondShouldBeLast);
        });
});

const hasQuery = computed(() => {
    return Boolean(
        filterRestaurantId.value
        || filterTimeId.value
        || filterDate.value,
    );
});

const queriedReservationCount = computed(() => {
    return hasQuery.value ? filteredReservations.value.length : 0;
});

const totalPeople = computed(() => {
    if (!hasQuery.value) {
        return 0;
    }

    return filteredReservations.value
        .filter((reservation) => reservation.status !== "已取消")
        .reduce((total, reservation) => total + Number(reservation.peopleCount || 0), 0);
});

const totalPages = computed(() => {
    return Math.max(1, Math.ceil(filteredReservations.value.length / pageSize));
});

const pagedReservations = computed(() => {
    const start = (currentPage.value - 1) * pageSize;
    return filteredReservations.value.slice(start, start + pageSize);
});

function getDisplayStatus(reservation) {
    if (reservation.status !== "已訂位") {
        return reservation.status;
    }

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const reservationDate = new Date(
        `${reservation.reservationDate}T00:00:00`,
    );

    return reservationDate < today ? "已逾期" : "已訂位";
}

function goToPage(page) {
    if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page;
    }
}

watch([filterRestaurantId, filterTimeId, filterDate], () => {
    currentPage.value = 1;
});

watch(
    () => filteredReservations.value.length,
    () => {
        if (currentPage.value > totalPages.value) {
            currentPage.value = totalPages.value;
        }
    },
);

function showMessage(text, type) {
    message.value = text;
    messageType.value = type;
}

function handleFilterRestaurantChange() {
    filterTimeId.value = "";
}

function clearFilters() {
    filterRestaurantId.value = "";
    filterTimeId.value = "";
    filterDate.value = "";
}

function openFilterModal() {
    filterModalOpen.value = true;
}

function closeFilterModal() {
    filterModalOpen.value = false;
}

function exportFilteredReservations() {
    if (filteredReservations.value.length === 0) {
        showMessage("目前沒有可匯出的查詢結果", "error");
        return;
    }

    const exportData = {
        exportedAt: new Date().toISOString(),
        filters: {
            restaurant: filterRestaurantId.value
                ? getRestaurantName(filterRestaurantId.value)
                : "全部餐廳",
            time: filterTimeId.value ? getTimeName(filterTimeId.value) : "全部時段",
            reservationDate: filterDate.value || "全部日期",
        },
        totalReservations: filteredReservations.value.length,
        totalPeople: totalPeople.value,
        reservations: filteredReservations.value,
    };

    const blob = new Blob([JSON.stringify(exportData, null, 2)], {
        type: "application/json;charset=utf-8",
    });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");

    link.href = url;
    link.download = `訂位查詢結果_${new Date().toISOString().slice(0, 10)}.json`;
    link.click();

    URL.revokeObjectURL(url);
    showMessage("查詢結果已匯出 JSON", "success");
}

function formatTime(time) {
    return time ? time.slice(0, 5) : "";
}

function getRestaurantName(restaurantId) {
    const restaurant = restaurants.value.find(
        (item) => Number(item.restaurantId) === Number(restaurantId),
    );

    return restaurant ? restaurant.restaurantName : `餐廳 ID：${restaurantId}`;
}

function getTimeName(timeId) {
    const time = allTimes.value.find(
        (item) => Number(item.timeId) === Number(timeId),
    );

    if (!time) {
        return `時段 ID：${timeId}`;
    }

    return `${time.mealType}（${formatTime(time.openTime)} - ${formatTime(time.closeTime)}）`;
}

// 輸入會員 ID 後，自動帶入姓名與電話。
function handleMemberIdInput() {
    memberLoaded.value = false;
    form.value.contactName = "";
    form.value.contactPhone = "";
}

async function loadMemberInfo() {
    const memberId = String(form.value.memberId ?? "").trim();

    memberLoaded.value = false;

    if (!memberId) {
        return;
    }

    if (!/^\d+$/.test(memberId)) {
        showMessage("會員 ID 必須為數字", "error");
        return;
    }

    try {
        const response = await fetch(`${MEMBER_API_URL}/${memberId}`, {
            method: "GET",
            headers: getAuthHeaders(),
        });

        if (response.status === 404) {
            showMessage("查無此會員 ID", "error");
            return;
        }

        if (response.status === 401 || response.status === 403) {
            showMessage("登入狀態失效或沒有會員資料權限", "error");
            return;
        }

        if (!response.ok) {
            showMessage("讀取會員資料失敗", "error");
            return;
        }

        const member = await response.json();

        form.value.memberId = String(member.memberId);
        form.value.contactName = member.name ?? "";
        form.value.contactPhone = member.phone ?? "";
        memberLoaded.value = true;
    } catch (error) {
        console.error(error);
        showMessage("無法連線至會員 API", "error");
    }
}

async function loadRestaurants() {
    try {
        const response = await fetch(RESTAURANT_API_URL, {
            method: "GET",
            headers: getAuthHeaders(),
        });

        if (response.status === 401 || response.status === 403) {
            showMessage("登入狀態失效或沒有餐廳資料權限", "error");
            return;
        }

        if (!response.ok) {
            showMessage("讀取餐廳資料失敗", "error");
            return;
        }

        restaurants.value = await response.json();
    } catch (error) {
        console.error(error);

        showMessage("無法連線至餐廳 API", "error");
    }
}

async function loadAllTimes() {
    try {
        const response = await fetch(TIME_API_URL, {
            method: "GET",
            headers: getAuthHeaders(),
        });

        if (!response.ok) {
            return;
        }

        allTimes.value = await response.json();
    } catch (error) {
        console.error(error);
    }
}

async function loadTimeOptions(selectedTimeId = "") {
    if (!form.value.restaurantId) {
        timeOptions.value = [];
        form.value.timeId = "";
        return;
    }

    try {
        const response = await fetch(
            `${TIME_API_URL}/restaurant/${form.value.restaurantId}`,
            {
                method: "GET",
                headers: getAuthHeaders(),
            },
        );

        if (response.status === 401 || response.status === 403) {
            showMessage("登入狀態失效或沒有時段資料權限", "error");
            return;
        }

        if (!response.ok) {
            showMessage("讀取餐廳時段失敗", "error");
            return;
        }

        timeOptions.value = await response.json();

        form.value.timeId = selectedTimeId ? String(selectedTimeId) : "";
    } catch (error) {
        console.error(error);

        showMessage("無法連線至餐廳時段 API", "error");
    }
}

async function loadReservations() {
    loading.value = true;

    try {
        const response = await fetch(RESERVATION_API_URL, {
            method: "GET",
            headers: getAuthHeaders(),
        });

        if (response.status === 401 || response.status === 403) {
            showMessage("登入狀態失效或沒有訂位管理權限", "error");
            return;
        }

        if (!response.ok) {
            showMessage("讀取訂位資料失敗", "error");
            return;
        }

        reservations.value = await response.json();

    } catch (error) {
        console.error(error);

        showMessage("無法連線至訂位 API", "error");
    } finally {
        loading.value = false;
    }
}

function clearForm() {
    form.value = createEmptyForm();
    timeOptions.value = [];
    memberLoaded.value = false;
    formTitle.value = "新增訂位";
}

function openCreateModal() {
    clearForm();
    modalOpen.value = true;
}

function closeModal() {
    modalOpen.value = false;
    clearForm();
}

async function saveReservation() {
    if (hasMember.value && !memberLoaded.value) {
        showMessage("請先輸入有效的會員 ID", "error");
        return;
    }

    if (!hasMember.value) {
        if (!form.value.contactName.trim() || !form.value.contactPhone.trim()) {
            showMessage("非會員訂位必須填寫姓名與電話", "error");
            return;
        }
    }

    const payload = {
        memberId: hasMember.value ? Number(form.value.memberId) : null,

        contactName: form.value.contactName.trim() || null,

        contactPhone: form.value.contactPhone.trim() || null,

        restaurantId: Number(form.value.restaurantId),

        reservationDate: form.value.reservationDate,

        timeId: Number(form.value.timeId),

        peopleCount: Number(form.value.peopleCount),

        status: form.value.status,
    };

    const isEdit = form.value.reservationId !== null;

    const url = isEdit
        ? `${RESERVATION_API_URL}/${form.value.reservationId}`
        : RESERVATION_API_URL;

    saving.value = true;

    try {
        const response = await fetch(url, {
            method: isEdit ? "PUT" : "POST",

            headers: getAuthHeaders(),

            body: JSON.stringify(payload),
        });

        if (response.status === 401 || response.status === 403) {
            showMessage("登入狀態失效或沒有操作權限", "error");
            return;
        }

        if (!response.ok) {
            showMessage("儲存失敗", "error");
            return;
        }

        modalOpen.value = false;
        clearForm();
        showMessage(isEdit ? "修改成功" : "新增成功", "success");

        await loadReservations();
    } catch (error) {
        console.error(error);

        showMessage("無法連線至訂位 API", "error");
    } finally {
        saving.value = false;
    }
}

async function editReservation(reservation) {
    form.value = {
        reservationId: reservation.reservationId,

        memberId: reservation.memberId ?? "",

        contactName: reservation.contactName ?? "",

        contactPhone: reservation.contactPhone ?? "",

        restaurantId: String(reservation.restaurantId),

        reservationDate: reservation.reservationDate,

        timeId: "",

        peopleCount: reservation.peopleCount,

        status: reservation.status,
    };

    memberLoaded.value = false;

    if (hasMember.value) {
        await loadMemberInfo();
    }

    await loadTimeOptions(reservation.timeId);

    formTitle.value = `修改訂位 ID：${reservation.reservationId}`;
    modalOpen.value = true;
}

async function deleteReservation(id) {
    if (!confirm("確定要刪除這筆訂位嗎？")) {
        return;
    }

    try {
        const response = await fetch(`${RESERVATION_API_URL}/${id}`, {
            method: "DELETE",
            headers: getAuthHeaders(),
        });

        if (response.status === 401 || response.status === 403) {
            showMessage("登入狀態失效或沒有刪除權限", "error");
            return;
        }

        if (!response.ok) {
            showMessage("刪除失敗", "error");
            return;
        }

        showMessage("訂位已刪除", "success");

        await loadReservations();
    } catch (error) {
        console.error(error);

        showMessage("無法連線至訂位 API", "error");
    }
}

async function sendTestSms(reservation) {
    if (!reservation.contactPhone) {
        showMessage("此訂位沒有聯絡電話，無法測試簡訊", "error");
        return;
    }

    testingSms.value = reservation.reservationId;

    try {
        const response = await fetch(
            `${RESERVATION_API_URL}/${reservation.reservationId}/sms`,
            {
                method: "POST",
                headers: getAuthHeaders(),
            },
        );
        const result = await response.json().catch(() => ({}));

        if (!response.ok) {
            showMessage(result.message || "測試簡訊發送失敗", "error");
            return;
        }

        showMessage("測試簡訊已發送，請查看 Spring Boot Console", "success");
    } catch (error) {
        console.error(error);
        showMessage("無法連線至測試簡訊 API", "error");
    } finally {
        testingSms.value = null;
    }
}

// 匯入與匯出餐廳、時段、訂位資料。
function openImportDialog() {
    importInput.value?.click();
}

async function importBackup(event) {
    const file = event.target.files?.[0];

    if (!file) {
        return;
    }

    try {
        const backupData = JSON.parse(await file.text());

        if (!confirm("匯入只會新增不存在的資料，確定要繼續嗎？")) {
            return;
        }

        importing.value = true;

        const response = await fetch(`${BACKUP_API_URL}/import`, {
            method: "POST",
            headers: getAuthHeaders(),
            body: JSON.stringify(backupData),
        });

        const result = await response.json().catch(() => ({}));

        if (!response.ok) {
            showMessage(result.message || "匯入失敗，請確認 JSON 格式", "error");
            return;
        }

        const skippedCount =
            result.skippedRestaurants + result.skippedTimes + result.skippedReservations;
        const convertedCount = result.memberConvertedToGuest ?? 0;

        showMessage(
            `匯入完成：新增 ${result.addedRestaurants} 間餐廳、${result.addedTimes} 個時段、${result.addedReservations} 筆訂位；略過 ${skippedCount} 筆重複或格式異常資料${convertedCount ? `；${convertedCount} 筆不存在的會員已改為訪客訂位。` : "。"}`,
            "success",
        );

        await loadRestaurants();
        await loadAllTimes();
        await loadReservations();
    } catch (error) {
        console.error(error);
        showMessage("JSON 檔案格式錯誤或無法讀取", "error");
    } finally {
        importing.value = false;
        event.target.value = "";
    }
}

onMounted(async () => {
    await loadRestaurants();
    await loadAllTimes();
    await loadReservations();
});
</script>

<template>
    <div class="reservation-page">
        <div class="admin-page-header">
            <div>
                <h1>餐廳訂位管理</h1>

                <p>管理飯店餐廳訂位、會員與非會員聯絡資訊及訂位狀態</p>
            </div>
        </div>

        <div v-if="message" class="admin-message page-message" :class="messageType">
            {{ message }}
        </div>

        <section class="admin-card">
            <div class="reservation-list-header">
                <h2>訂位列表</h2>

                <div class="list-header-actions">
                    <input ref="importInput" type="file" accept="application/json,.json" hidden
                        @change="importBackup" />

                    <button type="button" class="admin-btn admin-btn-primary" @click="openFilterModal">
                        查詢訂位
                    </button>

                    <button type="button" class="admin-btn admin-btn-secondary" :disabled="importing"
                        @click="openImportDialog">
                        {{ importing ? "匯入中..." : "匯入 JSON" }}
                    </button>

                    <button type="button" class="admin-btn admin-btn-primary" @click="openCreateModal">
                        ＋ 新增訂位
                    </button>

                    <button type="button" class="admin-btn admin-btn-secondary" @click="loadReservations">
                        重新整理
                    </button>
                </div>
            </div>

            <div v-if="loading" class="loading-message">訂位資料讀取中...</div>

            <div v-else class="admin-table-wrapper">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>訂位 ID</th>
                            <th>訂位人姓名</th>
                            <th>訂位人電話</th>
                            <th>餐廳</th>
                            <th>訂位日期</th>
                            <th>時段</th>
                            <th>人數</th>
                            <th>狀態</th>
                            <th>操作</th>
                        </tr>
                    </thead>

                    <tbody>
                        <tr v-if="filteredReservations.length === 0">
                            <td colspan="9" class="empty-row">查無符合條件的訂位資料</td>
                        </tr>

                        <tr v-for="reservation in pagedReservations" :key="reservation.reservationId">
                            <td>
                                {{ reservation.reservationId }}
                            </td>

                            <td>
                                {{ reservation.contactName ?? "" }}
                            </td>

                            <td>
                                {{ reservation.contactPhone ?? "" }}
                            </td>

                            <td>
                                {{ getRestaurantName(reservation.restaurantId) }}
                            </td>

                            <td>
                                {{ reservation.reservationDate }}
                            </td>

                            <td>
                                {{ getTimeName(reservation.timeId) }}
                            </td>

                            <td>
                                {{ reservation.peopleCount }}
                            </td>

                            <td>
                                <span class="reservation-status" :class="{
                                    'status-booked': getDisplayStatus(reservation) === '已訂位',
                                    'status-cancelled': getDisplayStatus(reservation) === '已取消',
                                    'status-completed': getDisplayStatus(reservation) === '已完成',
                                    'status-expired': getDisplayStatus(reservation) === '已逾期',
                                }">
                                    {{ getDisplayStatus(reservation) }}
                                </span>
                            </td>

                            <td>
                                <div class="reservation-actions">
                                    <button type="button" class="admin-btn admin-btn-edit"
                                        @click="editReservation(reservation)">
                                        修改
                                    </button>

                                    <button type="button" class="admin-btn admin-btn-delete"
                                        @click="deleteReservation(reservation.reservationId)">
                                        刪除
                                    </button>

                                    <button type="button" class="admin-btn admin-btn-secondary"
                                        :disabled="testingSms === reservation.reservationId"
                                        @click="sendTestSms(reservation)">
                                        {{ testingSms === reservation.reservationId ? "發送中..." : "測試簡訊" }}
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div v-if="filteredReservations.length > pageSize" class="pagination">
                <button type="button" class="admin-btn admin-btn-secondary" :disabled="currentPage === 1"
                    @click="goToPage(currentPage - 1)">
                    上一頁
                </button>

                <button v-for="page in totalPages" :key="page" type="button" class="page-button"
                    :class="{ active: currentPage === page }" @click="goToPage(page)">
                    {{ page }}
                </button>

                <button type="button" class="admin-btn admin-btn-secondary" :disabled="currentPage === totalPages"
                    @click="goToPage(currentPage + 1)">
                    下一頁
                </button>
            </div>
        </section>

        <Teleport to="body">
            <div v-if="modalOpen" class="modal-overlay" @click.self="closeModal">
                <section class="reservation-modal" role="dialog" aria-modal="true">
                    <header class="modal-header">
                        <h2>{{ formTitle }}</h2>

                        <button type="button" class="modal-close" @click="closeModal">
                            ×
                        </button>
                    </header>

                    <form class="modal-body" @submit.prevent="saveReservation">
                        <div class="admin-form-grid">
                            <div class="admin-form-group">
                                <label>會員 ID（選填）</label>
                                <input v-model="form.memberId" type="number" min="1" placeholder="會員訂位可輸入會員 ID"
                                    @input="handleMemberIdInput" @blur="loadMemberInfo" />
                            </div>

                            <div class="admin-form-group">
                                <label>訂位人姓名（非會員必填）</label>
                                <input v-model="form.contactName" type="text" placeholder="請輸入訂位人姓名"
                                    :disabled="memberLoaded" />
                            </div>

                            <div class="admin-form-group">
                                <label>訂位人電話（非會員必填）</label>
                                <input v-model="form.contactPhone" type="text" placeholder="請輸入聯絡電話"
                                    :disabled="memberLoaded" />
                            </div>

                            <div class="admin-form-group">
                                <label>餐廳 *</label>
                                <select v-model="form.restaurantId" required @change="loadTimeOptions()">
                                    <option value="">請選擇餐廳</option>
                                    <option v-for="restaurant in restaurants" :key="restaurant.restaurantId"
                                        :value="String(restaurant.restaurantId)">
                                        {{ restaurant.restaurantName }}
                                    </option>
                                </select>
                            </div>

                            <div class="admin-form-group">
                                <label>訂位日期 *</label>
                                <input v-model="form.reservationDate" type="date" required />
                            </div>

                            <div class="admin-form-group">
                                <label>訂位時段 *</label>
                                <select v-model="form.timeId" required :disabled="!form.restaurantId">
                                    <option value="">
                                        {{ form.restaurantId ? "請選擇時段" : "請先選擇餐廳" }}
                                    </option>
                                    <option v-for="time in timeOptions" :key="time.timeId" :value="String(time.timeId)">
                                        {{ time.mealType }}（{{ formatTime(time.openTime) }} - {{
                                            formatTime(time.closeTime) }}）
                                    </option>
                                </select>
                            </div>

                            <div class="admin-form-group">
                                <label>訂位人數 *</label>
                                <input v-model="form.peopleCount" type="number" min="1" required />
                            </div>

                            <div class="admin-form-group">
                                <label>訂位狀態 *</label>
                                <select v-model="form.status" required>
                                    <option value="已訂位">訂位</option>
                                    <option value="已取消">取消</option>
                                    <option value="已完成">已完成</option>
                                </select>
                            </div>
                        </div>

                        <div class="admin-form-actions modal-actions">
                            <button type="submit" class="admin-btn admin-btn-primary" :disabled="saving">
                                {{ saving ? "儲存中..." : "儲存" }}
                            </button>

                            <button type="button" class="admin-btn admin-btn-secondary" @click="closeModal">
                                取消
                            </button>
                        </div>
                    </form>
                </section>
            </div>
        </Teleport>

        <Teleport to="body">
            <div v-if="filterModalOpen" class="modal-overlay" @click.self="closeFilterModal">
                <section class="filter-modal" role="dialog" aria-modal="true">
                    <header class="modal-header">
                        <h2>查詢訂位與人數統計</h2>

                        <button type="button" class="modal-close" @click="closeFilterModal">
                            ×
                        </button>
                    </header>

                    <div class="modal-body">
                        <div class="filter-fields">
                            <label class="filter-field">
                                餐廳查詢
                                <select v-model="filterRestaurantId" @change="handleFilterRestaurantChange">
                                    <option value="">全部餐廳</option>
                                    <option v-for="restaurant in restaurants" :key="restaurant.restaurantId"
                                        :value="String(restaurant.restaurantId)">
                                        {{ restaurant.restaurantName }}
                                    </option>
                                </select>
                            </label>

                            <label class="filter-field">
                                時段查詢
                                <select v-model="filterTimeId">
                                    <option value="">全部時段</option>
                                    <option v-for="time in filterTimeOptions" :key="time.timeId"
                                        :value="String(time.timeId)">
                                        {{ getRestaurantName(time.restaurantId) }}｜{{ time.mealType }}
                                        （{{ formatTime(time.openTime) }} - {{ formatTime(time.closeTime) }}）
                                    </option>
                                </select>
                            </label>

                            <label class="filter-field">
                                訂位日期
                                <input v-model="filterDate" type="date" />
                            </label>

                            <div class="reservation-summary">
                                <div>
                                    <span>符合訂位</span>
                                    <strong>{{ queriedReservationCount }}</strong>
                                    <small>筆</small>
                                </div>

                                <div>
                                    <span>總用餐人數</span>
                                    <strong>{{ totalPeople }}</strong>
                                    <small>人</small>
                                </div>
                            </div>
                        </div>

                        <div class="admin-form-actions modal-actions">
                            <button type="button" class="admin-btn admin-btn-secondary" @click="clearFilters">
                                清除查詢
                            </button>

                            <button type="button" class="admin-btn admin-btn-secondary"
                                @click="exportFilteredReservations">
                                匯出查詢結果
                            </button>

                            <button type="button" class="admin-btn admin-btn-primary" @click="closeFilterModal">
                                套用查詢
                            </button>
                        </div>
                    </div>
                </section>
            </div>
        </Teleport>
    </div>
</template>

<style scoped>
.reservation-page {
    width: 100%;
}

.reservation-list-header {
    display: flex;

    justify-content: space-between;

    align-items: center;

    gap: 15px;

    margin-bottom: 22px;
}

.page-message {
    margin-bottom: 20px;
}

.reservation-list-header h2 {
    margin: 0;

    color: #6f5328;
}

.filter-modal {
    width: min(760px, 100%);
    background: #fff;
    border-radius: 14px;
    box-shadow: 0 22px 55px rgba(0, 0, 0, 0.26);
}

.filter-fields {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
}

.filter-field {
    display: grid;
    gap: 5px;
    min-width: 0;
    color: #6f5328;
    font-size: 12px;
    font-weight: bold;
}

.filter-field select,
.filter-field input {
    min-height: 38px;
    padding: 7px 9px;
    border: 1px solid #d8cbb9;
    border-radius: 6px;
    background: #fff;
    font: inherit;
    font-weight: normal;
}

.clear-filter-button {
    min-height: 38px;
}

.reservation-summary {
    display: flex;
    width: 100%;
    gap: 12px;
    margin-top: 0;
}

.reservation-summary>div {
    display: grid;
    flex: 1;
    min-width: 0;
    padding: 10px 14px;
    background: #fff;
    border: 1px solid #eadfce;
    border-radius: 8px;
}

.reservation-summary span {
    color: #7a6955;
    font-size: 12px;
}

.reservation-summary strong {
    margin-top: 3px;
    color: #9a6f32;
    font-size: 24px;
    line-height: 1;
}

.reservation-summary small {
    margin-top: 2px;
    color: #7a6955;
    font-size: 12px;
}

.reservation-actions {
    display: flex;

    gap: 7px;

    white-space: nowrap;
}

.empty-row {
    padding: 38px !important;

    text-align: center !important;

    color: #888 !important;
}

.loading-message {
    padding: 40px;

    text-align: center;

    color: #888;
}

.reservation-status {
    display: inline-block;

    padding: 5px 10px;

    border-radius: 20px;

    font-size: 12px;

    font-weight: bold;

    white-space: nowrap;
}

.status-booked {
    background-color: #e5f6eb;

    color: #257641;
}

.status-cancelled {
    background-color: #fde9e7;

    color: #b3443c;
}

.status-completed {
    background-color: #eee9e1;

    color: #5c4d3d;
}

.status-expired {
    background-color: #f7eddc;
    color: #8a6332;
}

input:disabled,
select:disabled {
    background-color: #f1eee8;

    cursor: not-allowed;
}

.admin-btn:disabled {
    opacity: 0.6;

    cursor: not-allowed;

    transform: none;
}

.list-header-actions {
    display: flex;
    flex-wrap: wrap;
    align-items: flex-end;
    gap: 10px;
}

.modal-overlay {
    position: fixed;
    z-index: 2000;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 24px;
    background: rgba(37, 30, 22, 0.55);
}

.reservation-modal {
    width: min(980px, 100%);
    max-height: calc(100vh - 48px);
    overflow: auto;
    background: #fff;
    border-radius: 14px;
    box-shadow: 0 22px 55px rgba(0, 0, 0, 0.26);
}

.modal-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px 28px;
    color: #fff;
    background: #4d3b28;
    border-radius: 14px 14px 0 0;
}

.modal-header h2 {
    margin: 0;
    font-size: 22px;
}

.modal-close {
    padding: 0 5px;
    border: 0;
    color: #fff;
    background: transparent;
    font-size: 32px;
    line-height: 1;
    cursor: pointer;
}

.modal-body {
    padding: 28px;
}

.modal-actions {
    margin-top: 24px;
}

.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
    margin-top: 24px;
}

.page-button {
    min-width: 36px;
    min-height: 36px;
    border: 1px solid #dfd2c0;
    border-radius: 7px;
    color: #6f5328;
    background: #fff;
    cursor: pointer;
}

.page-button:hover,
.page-button.active {
    border-color: #bd8d43;
    color: #fff;
    background: #bd8d43;
}

@media (max-width: 700px) {
    .reservation-list-header {
        align-items: stretch;

        flex-direction: column;
    }

    .list-header-actions {
        width: 100%;
    }

    .filter-fields {
        grid-template-columns: 1fr;
    }

    .filter-field {
        min-width: 0;
    }

    .reservation-summary>div {
        flex: 1;
    }

    .modal-overlay {
        align-items: flex-start;
        padding: 14px;
    }

    .reservation-modal,
    .filter-modal {
        max-height: calc(100vh - 28px);
        overflow: auto;
    }

    .modal-header,
    .modal-body {
        padding: 20px;
    }
}
</style>
