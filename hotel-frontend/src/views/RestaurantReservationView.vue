<script setup>
import { computed, onMounted, ref } from "vue";
import { useAuthStore } from "@/stores/auth";
import { useRoute } from "vue-router";

const authStore = useAuthStore();
const route = useRoute();

const restaurants = ref([]);
const timeOptions = ref([]);
const message = ref("");
const messageType = ref("");
const saving = ref(false);
const memberLoaded = ref(false);
const memberProfile = ref(null);

const form = ref(createEmptyForm());

function createEmptyForm() {
    return {
        memberId: null,
        contactName: "",
        contactPhone: "",
        restaurantId: "",
        timeId: "",
        reservationDate: "",
        peopleCount: 2,
        status: "已訂位",
    };
}

const isMember = computed(() => authStore.isLoggedIn);

const selectedRestaurant = computed(() => {
    return restaurants.value.find(
        (item) => Number(item.restaurantId) === Number(form.value.restaurantId),
    );
});

const restaurantImages = {
    雲饗中式自助餐廳:
        "https://images.unsplash.com/photo-1559339352-11d035aa65de?auto=format&fit=crop&w=1200&q=85",

    晨光西式餐廳:
        "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=1200&q=85",

    托斯卡尼義式餐廳:
        "https://images.unsplash.com/photo-1579751626657-72bc17010498?auto=format&fit=crop&w=1200&q=85",

    星夜駐唱酒吧:
        "https://images.unsplash.com/photo-1514933651103-005eec06c04b?auto=format&fit=crop&w=1200&q=85",
};

const selectedRestaurantImage = computed(() => {
    return restaurantImages[selectedRestaurant.value?.restaurantName] ?? "";
});

const today = computed(() => {
    const date = new Date();
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
});

function getAuthHeaders() {
    const token = localStorage.getItem("token");

    return {
        "Content-Type": "application/json",
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
    };
}

function showMessage(text, type) {
    message.value = text;
    messageType.value = type;
}

function formatTime(time) {
    return time ? time.slice(0, 5) : "";
}

async function preselectRestaurantFromQuery() {
    const restaurantName = String(route.query.restaurant ?? "");

    if (!restaurantName) {
        return;
    }

    const matchedRestaurant = restaurants.value.find(
        (item) => item.restaurantName === restaurantName,
    );

    if (!matchedRestaurant) {
        return;
    }

    form.value.restaurantId = String(matchedRestaurant.restaurantId);

    await loadTimeOptions();
}

async function loadRestaurants() {
    try {
        const response = await fetch("/api/public/restaurants");

        if (!response.ok) {
            throw new Error("讀取餐廳失敗");
        }

        restaurants.value = await response.json();
    } catch (error) {
        console.error(error);
        showMessage("無法讀取餐廳資料，請稍後再試。", "error");
    }
}

async function loadTimeOptions() {
    form.value.timeId = "";
    timeOptions.value = [];

    if (!form.value.restaurantId) {
        return;
    }

    try {
        const response = await fetch(
            `/api/public/restaurants/${form.value.restaurantId}/times`,
        );

        if (!response.ok) {
            throw new Error("讀取時段失敗");
        }

        timeOptions.value = await response.json();
    } catch (error) {
        console.error(error);
        showMessage("無法讀取餐廳時段，請稍後再試。", "error");
    }
}

async function loadMemberProfile() {
    if (!isMember.value) {
        return;
    }

    try {
        const response = await fetch("/api/members/me", {
            headers: getAuthHeaders(),
        });

        if (!response.ok) {
            showMessage("會員資料讀取失敗，請重新登入。", "error");
            return;
        }

        const data = await response.json();

        memberProfile.value = data;
        memberLoaded.value = true;

        form.value.memberId = data.memberId;
        form.value.contactName = data.name ?? "";
        form.value.contactPhone = data.phone ?? "";
    } catch (error) {
        console.error(error);
        showMessage("無法讀取會員資料。", "error");
    }
}

function resetForm() {
    form.value = createEmptyForm();
    timeOptions.value = [];

    if (memberLoaded.value && memberProfile.value) {
        form.value.memberId = memberProfile.value.memberId;
        form.value.contactName = memberProfile.value.name ?? "";
        form.value.contactPhone = memberProfile.value.phone ?? "";
    }
}

async function saveReservation() {
    message.value = "";

    if (
        !form.value.restaurantId ||
        !form.value.timeId ||
        !form.value.reservationDate ||
        !form.value.contactName.trim() ||
        !form.value.contactPhone.trim()
    ) {
        showMessage("請完整填寫訂位資料。", "error");
        return;
    }

    if (!/^09\d{8}$/.test(form.value.contactPhone.trim())) {
        showMessage("電話請填寫 09 開頭的 10 碼手機號碼。", "error");
        return;
    }

    saving.value = true;

    const payload = {
        memberId: memberLoaded.value ? form.value.memberId : null,
        contactName: form.value.contactName.trim(),
        contactPhone: form.value.contactPhone.trim(),
        restaurantId: Number(form.value.restaurantId),
        timeId: Number(form.value.timeId),
        reservationDate: form.value.reservationDate,
        peopleCount: Number(form.value.peopleCount),
        status: "已訂位",
    };

    const apiUrl = memberLoaded.value
        ? "/api/reservations"
        : "/api/public/reservations";

    try {
        const response = await fetch(apiUrl, {
            method: "POST",
            headers: memberLoaded.value
                ? getAuthHeaders()
                : { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
        });

        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
            showMessage(data.message || "訂位失敗，請確認資料。", "error");
            return;
        }

        showMessage("訂位成功！我們已為您保留座位。", "success");
        resetForm();
    } catch (error) {
        console.error(error);
        showMessage("系統連線失敗，請稍後再試。", "error");
    } finally {
        saving.value = false;
    }
}

onMounted(async () => {
    await loadRestaurants();
    // 從餐廳介紹頁點「立即訂位」時，自動選取該餐廳並載入時段
    await preselectRestaurantFromQuery();

    await loadMemberProfile();
});
</script>

<template>
    <div class="reservation-page">
        <section class="hero">
            <p>ONLINE RESERVATION</p>
            <h1>餐廳線上訂位</h1>
            <span>選擇您喜愛的餐廳與時段，預約美好的用餐時光。</span>
            <div class="reservation-steps">
                <div class="step-item active">
                    <span>1</span>
                    <p>選擇餐廳</p>
                </div>

                <div class="step-line"></div>

                <div class="step-item">
                    <span>2</span>
                    <p>填寫資料</p>
                </div>

                <div class="step-line"></div>

                <div class="step-item">
                    <span>3</span>
                    <p>完成訂位</p>
                </div>
            </div>
        </section>

        <main class="reservation-container">
            <section class="reservation-card">
                <div class="heading">
                    <div>
                        <p class="small-title">RESERVATION FORM</p>
                        <h2>填寫訂位資料</h2>
                    </div>

                    <span v-if="memberLoaded" class="member-badge">
                        會員資料已帶入
                    </span>
                </div>

                <div v-if="!isMember" class="guest-notice">
                    <strong>訪客訂位</strong>
                    <span>
                        已經是會員？
                        <RouterLink to="/login">登入後可自動帶入姓名與電話</RouterLink>
                    </span>
                </div>

                <div class="reservation-tips">
                    <div class="tip-item">
                        <span>🍽️</span>
                        <div>
                            <strong>多元餐飲選擇</strong>
                            <p>可依喜好選擇不同風格餐廳。</p>
                        </div>
                    </div>

                    <div class="tip-item">
                        <span>🕒</span>
                        <div>
                            <strong>即時時段連動</strong>
                            <p>選擇餐廳後自動顯示可用時段。</p>
                        </div>
                    </div>

                    <div class="tip-item">
                        <span>✓</span>
                        <div>
                            <strong>快速完成訂位</strong>
                            <p>送出資料後即可建立訂位紀錄。</p>
                        </div>
                    </div>
                </div>

                <form @submit.prevent="saveReservation">
                    <div class="form-grid">
                        <label>
                            選擇餐廳
                            <select v-model="form.restaurantId" required @change="loadTimeOptions">
                                <option value="">請選擇餐廳</option>
                                <option v-for="restaurant in restaurants" :key="restaurant.restaurantId"
                                    :value="String(restaurant.restaurantId)">
                                    {{ restaurant.restaurantName }}
                                </option>
                            </select>
                        </label>

                        <label>
                            用餐時段
                            <select v-model="form.timeId" required :disabled="!form.restaurantId">
                                <option value="">
                                    {{ form.restaurantId ? "請選擇時段" : "請先選擇餐廳" }}
                                </option>

                                <option v-for="time in timeOptions" :key="time.timeId" :value="String(time.timeId)">
                                    {{ time.mealType }}
                                    （{{ formatTime(time.openTime) }} - {{ formatTime(time.closeTime) }}）
                                </option>
                            </select>
                        </label>

                        <label>
                            用餐日期
                            <input v-model="form.reservationDate" type="date" :min="today" required />
                        </label>

                        <label>
                            用餐人數
                            <input v-model.number="form.peopleCount" type="number" min="1"
                                :max="selectedRestaurant?.capacity || 20" required />
                        </label>

                        <label>
                            訂位人姓名
                            <input v-model="form.contactName" type="text" placeholder="請輸入姓名" :readonly="memberLoaded"
                                required />
                        </label>

                        <label>
                            聯絡電話
                            <input v-model="form.contactPhone" type="tel" placeholder="例如：0912345678"
                                :readonly="memberLoaded" required />
                        </label>
                    </div>

                    <div v-if="selectedRestaurant" class="restaurant-preview">
                        <img :src="selectedRestaurantImage" :alt="selectedRestaurant.restaurantName" />

                        <div class="restaurant-info">
                            <strong>{{ selectedRestaurant.restaurantName }}</strong>

                            <span v-if="selectedRestaurant.description">
                                {{ selectedRestaurant.description }}
                            </span>

                            <span v-if="selectedRestaurant.phone">
                                餐廳電話：{{ selectedRestaurant.phone }}
                            </span>
                        </div>
                    </div>

                    <button type="submit" :disabled="saving">
                        {{ saving ? "訂位資料送出中…" : "確認訂位" }}
                    </button>

                    <p class="submit-note">
                        送出後將立即建立訂位紀錄；如需異動，請洽餐廳服務人員。
                    </p>

                    <p v-if="message" class="message" :class="messageType">
                        {{ message }}
                    </p>
                </form>
            </section>
        </main>
    </div>
</template>

<style scoped>
.reservation-page {
    min-height: 100vh;
    background: #f8f4ee;
    color: #3f3227;
}

.hero {
    padding: 74px 20px;
    text-align: center;
    color: #fff;
    background:
        linear-gradient(135deg,
            rgba(57, 40, 27, 0.9),
            rgba(143, 103, 57, 0.8)),
        url("https://images.unsplash.com/photo-1414235077428-338989a2e8c0?auto=format&fit=crop&w=1800&q=90") center / cover;
}

.hero>p {
    margin: 0 0 12px;
    color: #efd6a8;
    font-size: 13px;
    font-weight: bold;
    letter-spacing: 2px;
}

.hero h1 {
    margin: 0;
    font-size: clamp(34px, 5vw, 48px);
}

.hero>span {
    display: block;
    margin-top: 16px;
    color: #f5ece0;
}

.reservation-container {
    width: min(900px, 92%);
    margin: -32px auto 70px;
}

.reservation-card {
    padding: clamp(24px, 5vw, 46px);
    border-radius: 20px;
    background: #fff;
    box-shadow: 0 16px 45px rgba(68, 48, 27, 0.15);
}

.heading {
    display: flex;
    justify-content: space-between;
    gap: 16px;
    align-items: center;
    margin-bottom: 24px;
}

.small-title {
    margin: 0 0 7px;
    color: #a57736;
    font-size: 12px;
    font-weight: bold;
    letter-spacing: 1.5px;
}

.heading h2 {
    margin: 0;
    color: #4d3825;
}

.member-badge {
    padding: 8px 12px;
    border-radius: 20px;
    color: #26703d;
    background: #e5f5e9;
    font-size: 13px;
    font-weight: bold;
}

.guest-notice {
    display: grid;
    gap: 5px;
    margin-bottom: 24px;
    padding: 14px 16px;
    border-left: 4px solid #b88949;
    background: #fcf7ee;
    color: #6e5945;
    font-size: 14px;
}

.guest-notice a {
    color: #9b6e30;
    font-weight: bold;
    text-decoration: none;
}

.form-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
}

label {
    display: grid;
    gap: 8px;
    color: #53412e;
    font-size: 14px;
    font-weight: bold;
}

input,
select {
    box-sizing: border-box;
    width: 100%;
    padding: 13px;
    border: 1px solid #ded2c2;
    border-radius: 9px;
    color: #3f3227;
    background: #fffdfa;
    font: inherit;
}

input:focus,
select:focus {
    border-color: #ab7c3a;
    outline: none;
    box-shadow: 0 0 0 3px rgba(171, 124, 58, 0.15);
}

input:read-only {
    background: #f1eee8;
    cursor: not-allowed;
}

.restaurant-preview {
    display: grid;
    grid-template-columns: 190px 1fr;
    overflow: hidden;
    margin-top: 24px;
    border: 1px solid #eadcc9;
    border-radius: 12px;
    background: #fcf7ef;
}

.restaurant-preview img {
    width: 100%;
    height: 150px;
    object-fit: cover;
}

.restaurant-info {
    display: grid;
    gap: 7px;
    padding: 20px;
    color: #6a5642;
    font-size: 14px;
    line-height: 1.7;
}

.restaurant-info strong {
    color: #573d25;
    font-size: 17px;
}

button {
    width: 100%;
    margin-top: 26px;
    padding: 15px;
    border: 0;
    border-radius: 9px;
    color: #fff;
    background: #9b6e30;
    cursor: pointer;
    font: inherit;
    font-weight: bold;
}

button:hover:not(:disabled) {
    background: #7e531e;
}

button:disabled {
    cursor: not-allowed;
    opacity: 0.6;
}

.submit-note {
    margin: 11px 0 0;
    color: #8a7b69;
    font-size: 12px;
    line-height: 1.6;
    text-align: center;
}

.message {
    margin: 16px 0 0;
    text-align: center;
    font-weight: bold;
}

.success {
    color: #287244;
}

.error {
    color: #b34038;
}

.reservation-steps {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 10px;
    margin-top: 34px;
}

.step-item {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #eadfce;
    font-size: 13px;
}

.step-item span {
    display: grid;
    width: 27px;
    height: 27px;
    place-items: center;
    border: 1px solid #d7b476;
    border-radius: 50%;
    font-size: 12px;
    font-weight: bold;
}

.step-item.active span {
    color: #4a321d;
    background: #e9c98d;
}

.step-item p {
    margin: 0;
    white-space: nowrap;
}

.step-line {
    width: 38px;
    height: 1px;
    background: rgba(255, 255, 255, 0.45);
}

.reservation-tips {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
    margin: 0 0 26px;
}

.tip-item {
    display: flex;
    gap: 10px;
    padding: 14px;
    border: 1px solid #eee2d2;
    border-radius: 10px;
    background: #fdfaf5;
}

.tip-item>span {
    display: grid;
    width: 32px;
    height: 32px;
    flex-shrink: 0;
    place-items: center;
    border-radius: 50%;
    background: #f1dfc2;
    font-size: 15px;
}

.tip-item strong {
    display: block;
    color: #62462b;
    font-size: 13px;
}

.tip-item p {
    margin: 4px 0 0;
    color: #887968;
    font-size: 12px;
    line-height: 1.5;
}

@media (max-width: 650px) {
    .reservation-steps {
        gap: 6px;
    }

    .step-line {
        width: 14px;
    }

    .step-item p {
        display: none;
    }

    .reservation-tips {
        grid-template-columns: 1fr;
    }

    .form-grid {
        grid-template-columns: 1fr;
    }

    .heading {
        align-items: flex-start;
        flex-direction: column;
    }

    .restaurant-preview {
        grid-template-columns: 1fr;
    }

    .restaurant-preview img {
        height: 170px;
    }
}
</style>
