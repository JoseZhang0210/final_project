<script setup>
import { computed, onMounted, onUnmounted, ref, watch } from "vue";
import { bookingApi } from "@/api/bookingApi";
import { roomTypeApi } from "@/api/roomTypeApi";
import { roomApi } from "@/api/roomApi";
import { bookingPaymentApi } from "@/api/bookingPaymentApi";
import { fetchClient } from "@/api/apiClient"; // for BOOKING_ORDER_API_URL
import { useRouter } from "vue-router";

const BOOKING_API_URL = "/api/bookings";
const BOOKING_ORDER_API_URL = "/api/orders";

// 選單資料（初始化為空陣列）
const bookingOrders = ref([]);
const roomTypes = ref([]);
const rooms = ref([]);
const payments = ref([]);
const router = useRouter();

// 核心資料
const bookings = ref([]);
const message = ref("");
const messageType = ref("");
const formTitle = ref("新增/編輯訂房明細");

// 查詢條件
const searchCriteria = ref({
  memberId: "",
  checkInDate: "",
  checkOutDate: "",
  bookingStatus: "",
});

watch(() => searchCriteria.value.checkInDate, (newVal) => {
  if (newVal) {
    const nextDay = new Date(newVal);
    nextDay.setDate(nextDay.getDate() + 1);
    searchCriteria.value.checkOutDate = nextDay.toISOString().split('T')[0];
  }
});

const bookingStatuses = ["待入住", "已入住", "已完成", "已取消"];

const form = ref(createEmptyForm());
const showPaymentModal = ref(false);
const currentNewBooking = ref(null);
const isSubmittingPayment = ref(false); // 防止重複提交
const paymentForm = ref({
  amount: 0,
  paymentMethod: '現金',
  paymentStatus: '已付款',
  transactionId: ''
});

watch(() => form.value.checkInDate, (newVal) => {
  if (newVal) {
    const nextDay = new Date(newVal);
    nextDay.setDate(nextDay.getDate() + 1);
    form.value.checkOutDate = nextDay.toISOString().split('T')[0];
    calculatePrice();
  }
});

function createEmptyForm() {
  return {
    bookingId: "",
    memberId: "",
    bookingOrderId: "",
    roomTypeId: "",
    roomId: "",
    checkInDate: "",
    checkOutDate: "",
    guestNum: 1,
    bookingPrice: 0,
    bookingStatus: "待入住",
  };
}

// 支援駝峰與底線命名格式
const availableRooms = computed(() => {
  if (!form.value.roomTypeId) {
    return [];
  }

  const selectedRoomTypeId = Number(form.value.roomTypeId);

  // 1. 過濾出該房型的所有房間，並排除停用與維修中
  let matchedRooms = rooms.value.filter((room) => {
    const roomTypeId =
      room.roomTypeId ??
      room.room_type_id ??
      room.roomType?.roomTypeId;
    
    const status = room.roomStatus ?? room.room_status;
    if (status === '停用' || status === '維修中') return false;

    return Number(roomTypeId) === selectedRoomTypeId;
  });

  // 2. 根據目前選擇的入住與退房日期，過濾掉在該時段已被預訂的房間
  if (form.value.checkInDate && form.value.checkOutDate) {
    const checkIn = new Date(form.value.checkInDate);
    const checkOut = new Date(form.value.checkOutDate);

    const overlappingBookings = bookings.value.filter(b => {
      // 排除自己這筆訂單 (修改時)
      if (form.value.bookingId && (b.bookingId ?? b.booking_id) === form.value.bookingId) return false;
      
      const status = b.bookingStatus ?? b.booking_status;
      if (status === '已取消' || status === '已完成' || status === '已退房') return false;

      const bCheckIn = new Date(b.checkInDate ?? b.check_in_date);
      const bCheckOut = new Date(b.checkOutDate ?? b.check_out_date);

      // 日期重疊判斷 (新訂單入住時間 < 舊訂單退房時間 AND 新訂單退房時間 > 舊訂單入住時間)
      return checkIn < bCheckOut && checkOut > bCheckIn;
    });

    const occupiedRoomIds = overlappingBookings.map(b => b.roomId ?? b.room_id).filter(id => id);
    matchedRooms = matchedRooms.filter(room => !occupiedRoomIds.includes(room.roomId ?? room.room_id));
  }

  return matchedRooms;
});

const stayNights = computed(() => {
  if (!form.value.checkInDate || !form.value.checkOutDate) return 0;
  const checkIn = new Date(form.value.checkInDate);
  const checkOut = new Date(form.value.checkOutDate);
  const difference = checkOut - checkIn;
  return Math.max(0, Math.ceil(difference / (1000 * 60 * 60 * 24)));
});

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
}

function clearForm() {
  form.value = createEmptyForm();
  formTitle.value = "新增訂房明細";
  message.value = "";
}

function fillDummyData() {
  const today = new Date();
  const tzOffset = today.getTimezoneOffset() * 60000;
  const todayStr = new Date(today.getTime() - tzOffset).toISOString().split('T')[0];
  
  const tomorrow = new Date(today.getTime() + 86400000);
  const tomorrowStr = new Date(tomorrow.getTime() - tzOffset).toISOString().split('T')[0];
  
  let firstMemberId = 1;
  if (bookings.value && bookings.value.length > 0) {
    firstMemberId = bookings.value[0].memberId ?? bookings.value[0].member_id ?? 1;
  }
  
  form.value = {
    ...createEmptyForm(),
    memberId: firstMemberId,
    checkInDate: todayStr,
    checkOutDate: tomorrowStr,
    guestNum: 1,
    roomTypeId: 2, 
    bookingStatus: "已入住"
  };
  
  setTimeout(() => {
    if (availableRooms.value.length > 0) {
      form.value.roomId = availableRooms.value[0].roomId;
    }
    calculatePrice();
    formTitle.value = "新增訂房明細 (一鍵填入)";
  }, 50);
}

async function loadSelectOptions() {
  try {
    const [orderData, roomTypeData, roomData, paymentData] = await Promise.all([
      fetchClient(BOOKING_ORDER_API_URL, { method: "GET" }).catch(() => []),
      roomTypeApi.getAllRoomTypes().catch(() => []),
      roomApi.getAllRooms().catch(() => []),
      bookingPaymentApi.getAllPayments().catch(() => []),
    ]);

    bookingOrders.value = orderData;
    roomTypes.value = roomTypeData;
    rooms.value = roomData;
    payments.value = Array.isArray(paymentData) ? paymentData : paymentData.content || [];

    console.log("訂單選項：", bookingOrders.value);
    console.log("房型選項：", roomTypes.value);
    console.log("房間選項：", rooms.value);
    console.log("付款紀錄：", payments.value);
  } catch (error) {
    console.error("載入下拉選單錯誤：", error);
    showMessage(error.message || "無法載入下拉選單", "error");
  }
}

// 1. 載入與條件查詢 (對應 @GetMapping)
async function loadBookings() {
  currentPage.value = 1;
  try {
    // 依據條件切換 API Endpoint
    let data;
    if (
      searchCriteria.value.memberId ||
      searchCriteria.value.roomTypeId ||
      searchCriteria.value.roomId ||
      searchCriteria.value.checkInDate ||
      searchCriteria.value.checkOutDate ||
      searchCriteria.value.bookingStatus
    ) {
      data = await bookingApi.searchBookings({
        memberId: searchCriteria.value.memberId || null,
        roomTypeId: searchCriteria.value.roomTypeId || null,
        roomId: searchCriteria.value.roomId || null,
        checkInDate: searchCriteria.value.checkInDate || null,
        checkOutDate: searchCriteria.value.checkOutDate || null,
        bookingStatus: searchCriteria.value.bookingStatus || null
      });
    } else {
      data = await bookingApi.getAllBookings();
    }

    let rawBookings = Array.isArray(data) ? data : data ? [data] : [];
    
    // 自動將已過退房日期的訂單改為「已完成」
    const today = new Date();
    const tzOffset = today.getTimezoneOffset() * 60000;
    const todayStr = new Date(today.getTime() - tzOffset).toISOString().split('T')[0];
    
    rawBookings.forEach(b => {
      const cout = b.checkOutDate ?? b.check_out_date;
      const status = b.bookingStatus ?? b.booking_status;
      
      if (cout < todayStr && status !== '已取消' && status !== '已完成') {
        b.bookingStatus = '已完成';
        if (b.booking_status !== undefined) b.booking_status = '已完成';
        
        // 只傳送需要的欄位，避免後端反序列化錯誤
        const updatePayload = {
          bookingId: b.bookingId ?? b.booking_id,
          bookingStatus: '已完成'
        };
        
        // 同步更新至資料庫
        bookingApi.updateBooking(updatePayload.bookingId, updatePayload).catch(e => console.error("自動更新已完成狀態失敗", e));
      }
    });

    bookings.value = rawBookings;
  } catch (error) {
    console.error("loadBookings Error:", error);
    showMessage(error.message || "無法連線至預訂 API", "error");
  }
}

function clearSearch() {
  currentPage.value = 1;
  searchCriteria.value = { memberId: "", checkInDate: "", checkOutDate: "", bookingStatus: "" };
  loadBookings();
}

function getOrderLabel(orderId) {
  if (!orderId) {
    return "未指定訂單";
  }

  const order = bookingOrders.value.find(
    (item) => Number(item.bookingOrderId) === Number(orderId),
  );

  if (!order) {
    return `訂單 ${orderId}`;
  }

  return `訂單 ${order.bookingOrderId}`;
}

function getRoomTypeName(roomTypeId) {
  if (!roomTypeId) return "未指定房型";
  const found = roomTypes.value.find((item) => item.roomTypeId === Number(roomTypeId));
  return found ? (found.typeName ?? found.type_name) : `編號 ${roomTypeId}`;
}

function getRoomNumber(roomId) {
  if (!roomId) return "尚未分配";
  const found = rooms.value.find((item) => item.roomId === Number(roomId));
  return found ? (found.roomNumber ?? found.room_number) : `編號 ${roomId}`;
}

function getPaymentForBooking(bookingId) {
  return payments.value.find(p => p.bookingId === bookingId || p.booking_id === bookingId);
}

function getPaymentStatus(bookingId) {
  const payment = getPaymentForBooking(bookingId);
  return payment ? (payment.paymentStatus ?? payment.payment_status) : "無付款紀錄";
}

function goToPayment(bookingId) {
  router.push({ name: 'admin-booking-payments' });
}

function changeRoomType() {
  // 1. 清空已選擇的房號
  form.value.roomId = "";
  const selectedRoomType = roomTypes.value.find(
    (item) =>
      Number(item.roomTypeId ?? item.room_type_id) ===
      Number(form.value.roomTypeId),
  );
  if (selectedRoomType && form.value.guestNum > selectedRoomType.capacity) {
    form.value.guestNum = selectedRoomType.capacity;
  }
  calculatePrice();
}

function calculatePrice() {
  // 1. 防呆：若未選擇房型或住宿天數小於等於 0，價格歸零
  if (!form.value.roomTypeId || stayNights.value <= 0) {
    form.value.bookingPrice = 0;
    return;
  }

  // 2. 尋找匹配的房型物件（相容駝峰與底線命名）
  const selectedRoomType = roomTypes.value.find(
    (item) =>
      Number(item.roomTypeId ?? item.room_type_id) ===
      Number(form.value.roomTypeId),
  );
  if (!selectedRoomType || stayNights.value === 0) {
    form.value.bookingPrice = 0;
    return;
  }
  form.value.bookingPrice = selectedRoomType.pricePerNight * stayNights.value;

  // 3. 若為新增訂單，根據入住日期自動判斷狀態
  if (!form.value.bookingId && form.value.checkInDate) {
    const today = new Date();
    const tzOffset = today.getTimezoneOffset() * 60000;
    const todayStr = new Date(today.getTime() - tzOffset).toISOString().split('T')[0];
    
    if (form.value.checkInDate === todayStr) {
      form.value.bookingStatus = '已入住';
    } else if (form.value.checkInDate > todayStr) {
      form.value.bookingStatus = '待入住';
    }
  }
}

// 2. 儲存/更新預訂 (對應 PUT /api/bookings/{id})
async function saveBooking() {
  



  if (!form.value.memberId) {
    showMessage("請填寫會員 ID", "error");
    return;
  }

  if (!form.value.roomTypeId) {
    showMessage("請選擇房型", "error");
    return;
  }

  if (!form.value.checkInDate || !form.value.checkOutDate) {
    showMessage("請選擇入住及退房日期", "error");
    return;
  }

  if (stayNights.value < 1) {
    showMessage("退房日期必須晚於入住日期", "error");
    return;
  }

  const payload = {
    bookingId: form.value.bookingId,
    memberId: Number(form.value.memberId),
    roomTypeId: Number(form.value.roomTypeId),
    roomId: form.value.roomId ? Number(form.value.roomId) : null,
    checkInDate: form.value.checkInDate,
    checkOutDate: form.value.checkOutDate,
    guestNum: Number(form.value.guestNum),
    bookingPrice: Number(form.value.bookingPrice),
    bookingStatus: form.value.bookingStatus,
  };

  try {
    if (form.value.bookingId) {
      await bookingApi.updateBooking(form.value.bookingId, payload);
      showMessage("訂房明細修改成功", "success");
      clearForm();
      await loadBookings();
    } else {
      const createdBooking = await bookingApi.createBooking(payload);
      showMessage("訂房明細新增成功", "success");
      
      // 開啟付款視窗
      currentNewBooking.value = createdBooking.data || createdBooking; 
      paymentForm.value = {
        amount: payload.bookingPrice,
        paymentMethod: '信用卡',
        paymentStatus: '已付款',
        transactionId: ''
      };
      generateTxnId(); // 自動產生一個模擬的綠界序號
      showPaymentModal.value = true;
      
      clearForm();
      await loadBookings();
    }
  } catch (error) {
    showMessage(error.message || "無法連線至預訂 API", "error");
  }
}

function editBooking(booking) {
  form.value = {
    bookingId: booking.bookingId,
    memberId: booking.memberId ?? booking.member_id ?? "",
    bookingOrderId: booking.bookingOrderId ?? booking.booking_order_id ?? "",
    roomTypeId: booking.roomTypeId ?? booking.room_type_id ?? "",
    roomId: booking.roomId ?? booking.room_id ?? "",
    checkInDate: booking.checkInDate ?? booking.check_in_date ?? "",
    checkOutDate: booking.checkOutDate ?? booking.check_out_date ?? "",
    guestNum: booking.guestNum ?? booking.guest_num ?? 1,
    bookingPrice: booking.bookingPrice ?? booking.booking_price ?? 0,
    bookingStatus: booking.bookingStatus ?? booking.booking_status ?? "待確認",
  };
  formTitle.value = `修改訂房明細`;

  const payment = getPaymentForBooking(booking.bookingId);
  if (payment) {
    form.value.paymentMethod = payment.paymentMethod ?? payment.payment_method;
    form.value.transactionId = payment.transactionId ?? payment.transaction_id;
    form.value.paidAt = payment.paidAt ?? payment.paid_at;
  }

  window.scrollTo({ top: 0, behavior: "smooth" });
}

// 3. 刪除預訂 (對應 DELETE /api/bookings/{id})
async function deleteBooking(id) {
  if (!window.confirm("確定刪除這筆訂房明細嗎？")) {
    return;
  }

  try {
    await bookingApi.deleteBooking(id);
    showMessage("訂房明細已刪除", "success");
    if (form.value.bookingId === id) {
      clearForm();
    }
    await loadBookings();
  } catch (error) {
    showMessage(error.message || "刪除失敗", "error");
  }
}

function generateTxnId() {
  // 為了保證不與資料庫重複，使用時間戳 (YYYYMMDDHHMMSS) 加上 4 碼隨機數
  const now = new Date();
  const timestamp = now.getFullYear().toString() +
    String(now.getMonth() + 1).padStart(2, '0') +
    String(now.getDate()).padStart(2, '0') +
    String(now.getHours()).padStart(2, '0') +
    String(now.getMinutes()).padStart(2, '0') +
    String(now.getSeconds()).padStart(2, '0');
  const randomNum = Math.floor(1000 + Math.random() * 9000);
  paymentForm.value.transactionId = `TXN${timestamp}${randomNum}`;
}

function skipPayment() {
  showPaymentModal.value = false;
  currentNewBooking.value = null;
}

function openPaymentModal(booking) {
  currentNewBooking.value = booking;
  paymentForm.value = {
    amount: booking.bookingPrice ?? booking.booking_price ?? 0,
    paymentMethod: '信用卡',
    paymentStatus: '已付款',
    transactionId: ''
  };
  generateTxnId();
  showPaymentModal.value = true;
}

async function submitPayment() {
  if (isSubmittingPayment.value) return; // 防止重複連打

  const bookingId = currentNewBooking.value?.bookingId ?? currentNewBooking.value?.booking_id;
  if (!bookingId) {
    showMessage("無效的訂房 ID", "error");
    return;
  }

  // 檢查該訂房是否已有付款紀錄，防止重複建立
  const existing = getPaymentForBooking(bookingId);
  if (existing) {
    showMessage(`訂房 ID ${bookingId} 已有付款紀錄（付款 ID: ${existing.paymentId ?? existing.payment_id}），請勿重複新增`, "error");
    showPaymentModal.value = false;
    return;
  }

  isSubmittingPayment.value = true;
  try {
    const paymentPayload = {
      bookingId: bookingId,
      amount: paymentForm.value.amount,
      paymentMethod: paymentForm.value.paymentMethod,
      paymentStatus: paymentForm.value.paymentStatus,
      transactionId: paymentForm.value.transactionId || null,
    };

    await bookingPaymentApi.createPayment(paymentPayload);
    showMessage("付款紀錄建立成功", "success");
    showPaymentModal.value = false;
    currentNewBooking.value = null;
    await loadBookings(); // 重新拉取包含付款狀態的清單
  } catch (error) {
    showMessage(error.message || "建立付款失敗", "error");
  } finally {
    isSubmittingPayment.value = false; // 不管成功失敗都解除鎖定
  }
}

function getBookingStatusClass(status) {
  const map = {
    "待入住": "status-waiting",
    "已入住": "status-checked-in",
    "已退房": "status-checked-out",
    "已完成": "status-checked-out",
    "已取消": "status-cancelled",
  };
  return map[status] || "";
}

function formatPrice(price) {

  return new Intl.NumberFormat("zh-TW", {
    style: "currency",
    currency: "TWD",
    maximumFractionDigits: 0,
  }).format(price || 0);
}

let refreshInterval = null;

onMounted(async () => {
  await loadSelectOptions();
  await loadBookings();

  // 每分鐘自動重新拉取資料
  refreshInterval = setInterval(() => {
    loadBookings();
  }, 60000);
});

onUnmounted(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval);
  }
});

const currentFilter = ref("待入住"); // 預設顯示待入住的訂單

function setTabStatus(status) {
  currentFilter.value = status;
  currentPage.value = 1;
}

const filteredBookings = computed(() => {
  let result = bookings.value;
  
  if (currentFilter.value === "order_today") {
    const today = new Date();
    const tzOffset = today.getTimezoneOffset() * 60000;
    const todayStr = new Date(today.getTime() - tzOffset).toISOString().split('T')[0];

    result = result.filter(b => {
      const createdAt = b.createdAt ?? b.created_at;
      if (!createdAt) return false;
      return String(createdAt).startsWith(todayStr);
    });
  } else if (currentFilter.value === "stay_today") {
    const today = new Date();
    const tzOffset = today.getTimezoneOffset() * 60000;
    const todayStr = new Date(today.getTime() - tzOffset).toISOString().split('T')[0];

    result = result.filter(b => {
      const cin = b.checkInDate ?? b.check_in_date;
      const cout = b.checkOutDate ?? b.check_out_date;
      return cin <= todayStr && cout >= todayStr;
    });
  } else if (currentFilter.value !== "all") {
    result = result.filter(b => (b.bookingStatus ?? b.booking_status) === currentFilter.value);
  }
  
  return result;
});

const currentPage = ref(1);
const itemsPerPage = 20;
const totalPages = computed(() => Math.ceil(filteredBookings.value.length / itemsPerPage));
const sortKey = ref("bookingId");
const sortOrder = ref("desc"); // 預設從新到舊

function toggleSort(key) {
  if (sortKey.value === key) {
    sortOrder.value = sortOrder.value === "asc" ? "desc" : "asc";
  } else {
    sortKey.value = key;
    sortOrder.value = "desc";
  }
}

const sortedBookings = computed(() => {
  return [...filteredBookings.value].sort((a, b) => {
    let valA, valB;
    if (sortKey.value === 'bookingId') {
      valA = Number(a.bookingId);
      valB = Number(b.bookingId);
    } else if (sortKey.value === 'memberId') {
      valA = Number(a.memberId ?? a.member_id);
      valB = Number(b.memberId ?? b.member_id);
    } else {
      return 0;
    }
    
    if (valA < valB) return sortOrder.value === 'asc' ? -1 : 1;
    if (valA > valB) return sortOrder.value === 'asc' ? 1 : -1;
    return 0;
  });
});

const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  return sortedBookings.value.slice(start, start + itemsPerPage);
});
function nextPage() { if (currentPage.value < totalPages.value) currentPage.value++; }
function prevPage() { if (currentPage.value > 1) currentPage.value--; }

</script>

<template>
  <main class="booking-page">
    <header class="page-header">
      <h1>訂房明細管理</h1>
      <p>管理入住日期、退房日期、房型、房號及訂房狀態</p>
    </header>

    <div v-if="message" class="message" :class="messageType">
      {{ message }}
    </div>

    <!-- 條件查詢區塊 -->
    <section class="admin-card">
      <h2>條件查詢</h2>
      <div class="form-grid">
        <div class="form-group">
          <label>會員 ID</label>
          <input v-model="searchCriteria.memberId" type="text" placeholder="輸入會員 ID" />
        </div>

        <div class="form-group">
          <label>入住日期</label>
          <input v-model="searchCriteria.checkInDate" type="date" />
        </div>
        <div class="form-group">
          <label>退房日期</label>
          <input v-model="searchCriteria.checkOutDate" type="date" />
        </div>

      </div>
      <div class="form-actions" style="margin-top: 15px">
        <button type="button" class="btn primary" @click="loadBookings">
          查詢
        </button>
        <button type="button" class="btn secondary" @click="clearSearch">
          重設查詢
        </button>
      </div>
    </section>

    <!-- 表單區塊 -->
    <section class="admin-card">
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <h2>{{ formTitle }}</h2>
        <button type="button" class="btn secondary" @click="fillDummyData">一鍵寫入資料</button>
      </div>

      <form @submit.prevent="saveBooking">
        <div class="form-grid">
          <div class="form-group">
            <label>會員 ID *</label>
            <input v-model.number="form.memberId" type="number" required />
          </div>

          <div class="form-group">
            <label>入住日期 *</label>
            <input v-model="form.checkInDate" type="date" required @change="calculatePrice" />
          </div>

          <div class="form-group">
            <label>退房日期 *</label>
            <input v-model="form.checkOutDate" type="date" required @change="calculatePrice" />
          </div>

          <div class="form-group">
            <label>入住人數 *</label>
            <input v-model.number="form.guestNum" type="number" min="1" max="6" required />
          </div>

          <div class="form-group">
            <label>房型 *</label>
            <select v-model="form.roomTypeId" required @change="changeRoomType">
              <option value="" disabled>請選擇房型</option>
              <option v-for="roomType in roomTypes" :key="roomType.roomTypeId" :value="roomType.roomTypeId">
                {{ roomType.typeName }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>分配房號</label>
            <select v-model.number="form.roomId">
              <option value="">尚未分配</option>
              <option v-for="room in availableRooms" :key="room.roomId" :value="room.roomId">
                房號 {{ room.roomNumber }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>住宿晚數</label>
            <input :value="stayNights" type="number" disabled />
          </div>

          <div class="form-group">
            <label>訂房價格</label>
            <input v-model.number="form.bookingPrice" type="number" min="0" disabled />
          </div>

          <div class="form-group">
            <label>訂房狀態</label>
            <select v-model="form.bookingStatus">
              <option v-for="status in bookingStatuses" :key="status" :value="status">
                {{ status }}
              </option>
            </select>
          </div>
          
          <div class="form-group" v-if="form.bookingId && getPaymentForBooking(form.bookingId)">
            <label>付款方式 (唯讀)</label>
            <input :value="form.paymentMethod" type="text" disabled />
          </div>
          
          <div class="form-group" v-if="form.bookingId && getPaymentForBooking(form.bookingId)">
            <label>交易序號 (唯讀)</label>
            <input :value="form.transactionId || '無'" type="text" disabled />
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn primary">
            新增/修改
          </button>
          <button type="button" class="btn secondary" @click="clearForm">
            清除表單
          </button>
        </div>
      </form>
    </section>

    <!-- 列表區塊 -->
    <section class="admin-card">
      <div class="table-header">
        <h2>訂房明細列表</h2>
        <span>共 {{ filteredBookings.length }} 筆</span>
      </div>

      <!-- 快速狀態切換 -->
      <div class="status-tabs">
        <button type="button" :class="{ active: currentFilter === 'order_today' }" @click="setTabStatus('order_today')">今日新增訂單</button>
        <button type="button" :class="{ active: currentFilter === 'stay_today' }" @click="setTabStatus('stay_today')">今日住房</button>
        <button type="button" :class="{ active: currentFilter === 'all' }" @click="setTabStatus('all')">全部</button>
        <button type="button" :class="{ active: currentFilter === '待入住' }" @click="setTabStatus('待入住')">待入住</button>
        <button type="button" :class="{ active: currentFilter === '已入住' }" @click="setTabStatus('已入住')">已入住</button>
        <button type="button" :class="{ active: currentFilter === '已完成' }" @click="setTabStatus('已完成')">已完成</button>
        <button type="button" :class="{ active: currentFilter === '已取消' }" @click="setTabStatus('已取消')">已取消</button>
      </div>

      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th @click="toggleSort('bookingId')" class="sortable">
                ID <span v-if="sortKey === 'bookingId'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span>
              </th>
              <th @click="toggleSort('memberId')" class="sortable">
                會員ID <span v-if="sortKey === 'memberId'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span>
              </th>
              <th>房型</th>
              <th>房號</th>
              <th>入住</th>
              <th>退房</th>
              <th>人數</th>
              <th>價格</th>
              <th>狀態</th>

              <th>操作</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="filteredBookings.length === 0">
              <td colspan="10" style="text-align: center">
                目前沒有符合條件的訂房明細
              </td>
            </tr>
            <tr v-for="booking in paginatedData" :key="booking.bookingId">
              <td>{{ booking.bookingId }}</td>
              <td>{{ booking.memberId ?? booking.member_id }}</td>
              <td>
                {{
                  getRoomTypeName(booking.roomTypeId ?? booking.room_type_id)
                }}
              </td>
              <td>{{ getRoomNumber(booking.roomId ?? booking.room_id) }}</td>
              <td>{{ booking.checkInDate ?? booking.check_in_date }}</td>
              <td>{{ booking.checkOutDate ?? booking.check_out_date }}</td>
              <td>{{ booking.guestNum ?? booking.guest_num }} 人</td>
              <td>
                {{ formatPrice(booking.bookingPrice ?? booking.booking_price) }}
              </td>
              <td>
                <span :class="['booking-status', getBookingStatusClass(booking.bookingStatus ?? booking.booking_status)]">
                  {{ booking.bookingStatus ?? booking.booking_status }}
                </span>
              </td>

              <td class="actions">
                <button class="btn edit" @click="editBooking(booking)">
                  修改
                </button>
                <button class="btn payment" v-if="!getPaymentForBooking(booking.bookingId)" @click="openPaymentModal(booking)">
                  新增付款
                </button>
                <button class="btn delete" @click="deleteBooking(booking.bookingId)">
                  刪除
                </button>
              </td>
            </tr>
          </tbody>
        </table>

      <div class="pagination-container" v-if="totalPages > 1">
        <button @click="prevPage" :disabled="currentPage === 1" class="page-btn">◀ 上一頁</button>
        <span class="page-info">第 {{ currentPage }} 頁 / 共 {{ totalPages }} 頁</span>
        <button @click="nextPage" :disabled="currentPage === totalPages" class="page-btn">下一頁 ▶</button>
      </div>
  

      </div>
    </section>

    <!-- 付款紀錄小視窗 (Modal) -->
    <div v-if="showPaymentModal" class="modal-overlay">
      <div class="modal-content">
        <h3 style="margin-top: 0;">建立付款紀錄</h3>
        <p>訂單 <strong>{{ currentNewBooking?.bookingId }}</strong> 已建立！是否要一併新增付款紀錄？</p>

        <form @submit.prevent="submitPayment">
          <div class="form-group">
            <label>應付金額</label>
            <input v-model.number="paymentForm.amount" type="number" required />
          </div>

          <div class="form-group">
            <label>付款方式</label>
            <select v-model="paymentForm.paymentMethod" required>
              <option value="現金">現金</option>
              <option value="信用卡">信用卡</option>
              <option value="銀行轉帳">銀行轉帳</option>
              <option value="LINE PAY">LINE PAY</option>
              <option value="Apple PAY">Apple PAY</option>
            </select>
          </div>

          <div class="form-group">
            <label>付款狀態</label>
            <select v-model="paymentForm.paymentStatus" required>
              <option value="已付款">已付款</option>
              <option value="待付款">待付款</option>
              <option value="已退款">已退款</option>
              <option value="已取消">已取消</option>
            </select>
          </div>

          <div class="form-group">
            <label>交易序號</label>
            <div style="display: flex; gap: 8px;">
              <input v-model="paymentForm.transactionId" type="text" placeholder="留空或點擊產生" style="flex: 1;" />
              <button type="button" class="btn secondary" @click="generateTxnId" style="white-space: nowrap; padding: 0.5rem 1rem;">自動產生</button>
            </div>
          </div>

          <div class="modal-actions" style="margin-top: 20px; display: flex; gap: 10px; justify-content: flex-end;">
            <button type="button" class="btn secondary" @click="skipPayment" :disabled="isSubmittingPayment">跳過，不建立</button>
            <button type="submit" class="btn primary" :disabled="isSubmittingPayment">
              {{ isSubmittingPayment ? '建立中…' : '確認建立付款' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </main>
</template>
<style scoped>
.booking-page {
  padding: 28px;
  color: #243447;
}

.page-header,
.admin-card {
  margin-bottom: 24px;
  padding: 24px;
  background: white;
  border: 1px solid #e4e7ec;
  border-radius: 12px;
  box-shadow: 0 3px 12px rgb(0 0 0 / 6%);
}

.page-header h1 {
  margin: 0 0 8px;
}

.page-header p {
  margin: 0;
  color: #667085;
}

.message {
  margin-bottom: 20px;
  padding: 12px 16px;
  border-radius: 8px;
}

.success {
  color: #176b3a;
  background: #e9f8ef;
}

.error {
  color: #b42318;
  background: #feeceb;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

input,
select {
  padding: 11px 12px;
  font: inherit;
  border: 1px solid #cfd4dc;
  border-radius: 7px;
}

.form-actions,
.actions {
  display: flex;
  gap: 8px;
}

.form-actions {
  margin-top: 20px;
}

.btn {
  padding: 8px 13px;
  color: white;
  border: none;
  border-radius: 7px;
  cursor: pointer;
}

.primary {
  background: #315b7d;
}

.secondary {
  color: #344054;
  background: #e4e7ec;
}

.btn.edit:hover {
  background-color: #d97706;
}

.btn.payment {
  background-color: #10b981;
  color: #fff;
}

.btn.payment:hover {
  background-color: #059669;
}

.edit {
  background: #d59032;
}

.delete {
  background: #c84040;
}

.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.table-wrapper table thead th {
  background-color: #4a3b32 !important;
  /* 深棕色背景 */
  color: #ffffff !important;
  /* 純白文字 */
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  min-width: 85px;
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e4e7ec;
}

th {
  color: #344054;
  background: #f2f4f7;
}

.status-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}
.status-tabs button {
  padding: 8px 16px;
  border: 1px solid #cfd4dc;
  background: #f8fafc;
  color: #475467;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}
.status-tabs button:hover {
  background: #f1f5f9;
}
.status-tabs button.active {
  background: #315b7d;
  color: white;
  border-color: #315b7d;
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
.pagination-container { display: flex; justify-content: center; align-items: center; margin-top: 20px; gap: 15px; } .page-btn { padding: 8px 16px; background-color: #3b82f6; color: white; border: none; border-radius: 6px; cursor: pointer; font-weight: 500; transition: background-color 0.2s; } .page-btn:hover:not(:disabled) { background-color: #2563eb; } .page-btn:disabled { background-color: #d1d5db; cursor: not-allowed; } .page-info { font-weight: 500; color: #374151; }
.booking-status {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
  white-space: nowrap;
}

.booking-status.status-waiting {
  color: #1a56db;
  background-color: #e8f0fe;
}

.booking-status.status-checked-in {
  color: #087443;
  background-color: #e7f8ef;
}

.booking-status.status-checked-out {
  color: #475467;
  background-color: #f2f4f7;
}

.booking-status.status-cancelled {
  color: #b42318;
  background-color: #feeceb;
}

/* Modal 樣式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: #fff;
  padding: 2rem;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.modal-actions button {
  min-width: 100px;
}
</style>
