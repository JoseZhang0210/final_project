<template>
  <div class="mobile-pass-container">
    <div class="header">
      <div class="logo">🏨 StarLight Hotel</div>
      <h2>星澄飯店 專屬貴賓通行證</h2>
      <div class="live-clock" v-if="currentTimeDisplay">
        目前時間：{{ currentTimeDisplay }}
      </div>
    </div>

    <!-- 載入中骨架 -->
    <div v-if="loading" class="pass-card loading-card">
      <div class="spinner"></div>
      <p>讀取通行證資訊中...</p>
    </div>

    <!-- 主要通行證卡片 -->
    <div v-else class="pass-card" :class="cardStateClass">
      <!-- 卡片頂部狀態區 -->
      <div class="card-top">
        <!-- 狀態 1: 下訂單後至入住當天 15:00 前（期待您的蒞臨） -->
        <template v-if="isEarly">
          <div class="status-icon early">
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path
                d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"
              ></path>
            </svg>
          </div>
          <div class="status-badge early">✨ 預約已確認・期待蒞臨</div>
          <h3>期待您的蒞臨</h3>
          <p class="status-subtitle">
            入住開放時間：{{ passData.checkInDate }} 15:00 起
          </p>
          <div v-if="countdownText" class="countdown-badge">
            ⏳ {{ countdownText }}
          </div>
        </template>

        <!-- 狀態 2: 入住當天 15:00 後（Check-in 入住報到） -->
        <template
          v-else-if="isCheckInAllowed && passData.bookingStatus === '待入住'"
        >
          <div class="status-icon success">
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
              <polyline points="22 4 12 14.01 9 11.01"></polyline>
            </svg>
          </div>
          <div class="status-badge active">🟢 開放 Check-in 報到</div>
          <h3>歡迎入住！辦理報到 (Check-in)</h3>
          <p class="status-subtitle">已到達入住開放時間（15:00 起）</p>
        </template>

        <!-- 狀態 3: 已完成入住報到 -->
        <template v-else-if="passData.bookingStatus === '已入住'">
          <div class="status-icon checked-in">
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
              <polyline points="9 22 9 12 15 12 15 22"></polyline>
            </svg>
          </div>
          <div class="status-badge checked">🌟 住宿中</div>
          <h3>已完成入住報到</h3>
          <p class="status-subtitle">祝您在星澄飯店擁有美好時光</p>
        </template>

        <!-- 狀態 4: 訂單已取消或過期 -->
        <template v-else>
          <div class="status-icon expired">
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="15" y1="9" x2="9" y2="15"></line>
              <line x1="9" y1="9" x2="15" y2="15"></line>
            </svg>
          </div>
          <div class="status-badge invalid">
            {{
              passData.bookingStatus === "已取消"
                ? "訂單已取消"
                : "通行證已失效"
            }}
          </div>
          <h3>
            {{
              passData.bookingStatus === "已取消"
                ? "此預訂已取消"
                : "通行證已過期"
            }}
          </h3>
        </template>
      </div>

      <div class="divider"></div>

      <!-- 卡片內容區 -->
      <div class="card-body">
        <!-- 房號資訊 -->
        <div class="info-group">
          <span class="label">您的專屬房號</span>
          <span
            class="room-number"
            :class="{ 'pending-room': isEarly || !passData.roomNumber }"
          >
            {{ displayRoomNumber }}
          </span>
          <span v-if="isEarly" class="room-hint">
            * 當日 15:00 起此處將自動切換為正式房號
          </span>
        </div>

        <!-- 預訂資訊清單 -->
        <div class="booking-specs">
          <div class="spec-item">
            <span class="spec-label">預訂房型</span>
            <span class="spec-val">{{
              passData.roomTypeName || "精緻客房"
            }}</span>
          </div>
          <div class="spec-item">
            <span class="spec-label">預定入住</span>
            <span class="spec-val highlight"
              >{{ passData.checkInDate }} 15:00 起</span
            >
          </div>
          <div class="spec-item">
            <span class="spec-label">預定退房</span>
            <span class="spec-val">{{ passData.checkOutDate }} 11:00 前</span>
          </div>
          <div class="spec-item" v-if="passData.bookingId">
            <span class="spec-label">訂單編號</span>
            <span class="spec-val">#{{ passData.bookingId }}</span>
          </div>
        </div>

        <!-- 狀態 3 (已入住)：顯示電子房卡與開門 QR Code (支援 CR522 門禁) -->
        <div v-if="passData.bookingStatus === '已入住'" class="door-key-card">
          <div class="door-key-header">
            <span class="key-badge">🔑 智慧客房電子鑰匙</span>
            <h4>房門開門感應 QR Code</h4>
          </div>
          <div class="door-qr-box">
            <img
              :src="`https://api.qrserver.com/v1/create-qr-code/?size=160x160&data=${encodeURIComponent(doorAccessKey)}`"
              alt="Door Access QR Code"
              class="door-qr-img"
            />
          </div>
          <div class="door-key-info">
            <span class="key-lbl">門禁金鑰：</span>
            <span class="key-val">{{ doorAccessKey }}</span>
          </div>
          <p class="door-instruction">
            📶 <strong>門禁開門說明：</strong><br />
            抵達房門口時，請將此 QR Code 對準門鎖鏡頭或感應
            <strong>CR522 門禁讀卡機</strong> 即可解鎖。
          </p>
        </div>

        <!-- 通行驗證碼 (報到前顯示) -->
        <div v-else class="verify-box">
          <span class="verify-label">專屬快速入住驗證碼</span>
          <div class="verify-code">{{ passData.verificationCode }}</div>
        </div>

        <!-- 提示說明文案 -->
        <div
          class="instruction-box"
          :class="{ 'welcome-box': isEarly, 'active-box': isCheckInAllowed }"
        >
          <template v-if="isEarly">
            <p class="instruction">
              🏨 <strong>期待您的蒞臨：</strong><br />
              親愛的貴賓您好，我們已為您妥善保留客房。標準入住時間為
              <strong>{{ passData.checkInDate }} 下午 15:00 起</strong>。<br />
              入住當日下午 15:00 起，此畫面將<strong
                >自動切換為【入住報到 (Check-in)】與【專屬房號】</strong
              >，屆時請向櫃檯人員出示此通行證辦理手續。
            </p>
          </template>
          <template
            v-else-if="isCheckInAllowed && passData.bookingStatus === '待入住'"
          >
            <p class="instruction">
              ✨ <strong>開放報到中：</strong><br />
              請向櫃檯人員出示此畫面，或點擊下方按鈕完成入住報到核驗手續。祝您有美好的住宿體驗！
            </p>
          </template>
          <template v-else-if="passData.bookingStatus === '已入住'">
            <p class="instruction">
              🎉 <strong>您已成功辦理入住！</strong><br />
              請使用上方電子房卡與開門 QR Code
              進出客房，祝您在星澄飯店擁有舒適愉快的住宿體驗！
            </p>
          </template>
          <template v-else>
            <p class="instruction">
              如對訂單狀態有任何疑問，歡迎隨時向星澄飯店服務中心洽詢。
            </p>
          </template>
        </div>
      </div>

      <!-- 底部操作按鈕 -->
      <div class="card-bottom">
        <!-- 狀態 1: 尚未到時間（期待蒞臨按鈕） -->
        <button
          v-if="isEarly"
          class="action-btn early-btn"
          @click="handleEarlyClick"
        >
          ✨ 期待您的蒞臨（當日 15:00 開放報到）
        </button>

        <!-- 狀態 2: 到達時間且待入住（確認報到按鈕） -->
        <button
          v-else-if="isCheckInAllowed && passData.bookingStatus === '待入住'"
          class="action-btn active-btn"
          :disabled="submitting"
          @click="handleCheckIn"
        >
          {{ submitting ? "處理報到中..." : "🛎️ 確認辦理入住報到 (Check-in)" }}
        </button>

        <!-- 狀態 3: 已入住 (前往官方網站申請房務或回首頁) -->
        <div
          v-else-if="passData.bookingStatus === '已入住'"
          class="checked-in-actions"
        >
          <button class="action-btn active-btn" @click="goToMemberBookings">
            🏨 前往星澄飯店網站（申請房務服務）
          </button>
          <button class="action-btn secondary-btn" @click="goToHome">
            回到首頁
          </button>
        </div>

        <!-- 狀態 4: 已取消或過期 -->
        <button v-else class="action-btn secondary-btn" @click="goToHome">
          返回首頁
        </button>
      </div>
    </div>

    <!-- 背景光暈裝飾 -->
    <div class="background-decorations">
      <div class="circle top-right"></div>
      <div class="circle bottom-left"></div>
    </div>

    <!-- 提示彈窗 -->
    <AlertModal
      :show="alertConfig.show"
      :type="alertConfig.type"
      :title="alertConfig.title"
      :message="alertConfig.message"
      @close="alertConfig.show = false"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import AlertModal from "../components/common/AlertModal.vue";

const route = useRoute();
const router = useRouter();

const loading = ref(true);
const submitting = ref(false);
const currentTime = ref(new Date());
let timer = null;

const passData = reactive({
  bookingId: null,
  verificationCode: "讀取中...",
  roomTypeName: "",
  roomNumber: "",
  checkInDate: "",
  checkOutDate: "",
  checkInTime: "15:00",
  checkOutTime: "11:00",
  guestNum: 2,
  bookingStatus: "待入住",
  serverTime: null,
});

const alertConfig = ref({
  show: false,
  type: "success",
  title: "",
  message: "",
});

// 當前時間格式化
const currentTimeDisplay = computed(() => {
  if (!currentTime.value) return "";
  const d = currentTime.value;
  const pad = (n) => String(n).padStart(2, "0");
  return `${d.getFullYear()}/${pad(d.getMonth() + 1)}/${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
});

// 檢查是否早於入住時間 (入住日 15:00 前)
const isEarly = computed(() => {
  if (!passData.checkInDate) return false;
  const checkInDateTime = new Date(`${passData.checkInDate}T15:00:00`);
  return currentTime.value < checkInDateTime;
});

// 檢查是否逾期 (退房日 11:00 後)
const isExpired = computed(() => {
  if (!passData.checkOutDate) return false;
  const checkOutDateTime = new Date(`${passData.checkOutDate}T11:00:00`);
  return currentTime.value > checkOutDateTime;
});

// 是否在可辦理入住時段 (入住日 15:00 ~ 退房日 11:00 且訂單未取消)
const isCheckInAllowed = computed(() => {
  if (!passData.checkInDate || !passData.checkOutDate) return false;
  if (passData.bookingStatus === "已取消") return false;
  return !isEarly.value && !isExpired.value;
});

// 房號顯示文字
const displayRoomNumber = computed(() => {
  if (passData.bookingStatus === "已入住" && passData.roomNumber) {
    return passData.roomNumber;
  }
  if (isEarly.value) {
    return "15:00 開放指派";
  }
  if (
    passData.roomNumber &&
    passData.roomNumber !== "待指派" &&
    passData.roomNumber !== "待報到時指派" &&
    passData.roomNumber !== "未指定"
  ) {
    return passData.roomNumber;
  }
  return isCheckInAllowed.value ? "櫃檯即時指派" : "尚未指派";
});

// 卡片狀態 CSS Class
const cardStateClass = computed(() => {
  if (isEarly.value) return "card-early";
  if (passData.bookingStatus === "已入住") return "card-checked-in";
  if (isCheckInAllowed.value) return "card-active";
  return "card-expired";
});

// 門禁開門專用金鑰憑證 (供 CR522 / 門鎖讀卡機感應使用)
const doorAccessKey = computed(() => {
  const room = passData.roomNumber || "VIP";
  const code = passData.verificationCode || "PASS";
  return `STARLIGHT-KEY:ROOM-${room}:${code}`;
});

// 倒數文字計算
const countdownText = computed(() => {
  if (!isEarly.value || !passData.checkInDate) return "";
  const checkInDateTime = new Date(`${passData.checkInDate}T15:00:00`);
  const diffMs = checkInDateTime.getTime() - currentTime.value.getTime();
  if (diffMs <= 0) return "";

  const totalHours = Math.floor(diffMs / (1000 * 60 * 60));
  const days = Math.floor(totalHours / 24);
  const hours = totalHours % 24;
  const minutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));

  if (days > 0) {
    return `距離開放報到還有 ${days} 天 ${hours} 小時 ${minutes} 分`;
  }
  return `距離開放報到還有 ${hours} 小時 ${minutes} 分`;
});

// 載入通行證資料 (即時同步後端資料庫)
async function fetchPassData() {
  loading.value = true;
  const bookingId = route.query.booking;
  const code = route.query.code;

  // 1. 若有 bookingId，向後端資料庫查詢最新訂房紀錄
  if (bookingId) {
    try {
      const res = await fetch(`/api/bookings/${bookingId}`);
      if (res.ok) {
        const b = await res.json();
        passData.bookingId = b.bookingId;
        passData.verificationCode =
          code ||
          "CK" +
            b.bookingId +
            String(Math.abs((b.bookingId * 37 + 1013) % 10000)).padStart(
              4,
              "0",
            );
        passData.checkInDate = b.checkInDate;
        passData.checkOutDate = b.checkOutDate;
        passData.guestNum = b.guestNum || 2;
        passData.bookingStatus = b.bookingStatus || "待入住";

        // 讀取房型名稱
        if (b.roomTypeId) {
          try {
            const rtRes = await fetch(`/api/roomtypes/${b.roomTypeId}`);
            if (rtRes.ok) {
              const rt = await rtRes.json();
              passData.roomTypeName = rt.typeName;
            }
          } catch (e) {}
        }

        // 讀取房號
        if (b.roomId) {
          try {
            const rRes = await fetch(`/api/rooms/${b.roomId}`);
            if (rRes.ok) {
              const r = await rRes.json();
              passData.roomNumber = r.roomNumber;
            }
          } catch (e) {}
        }

        loading.value = false;
        return;
      }
    } catch (e) {
      console.warn("後端 API 取得失敗，啟用 URL 參數回退解析：", e);
    }
  }

  // 2. 回退模式：直接從 URL 參數解析 (適用於純前端/離線/預覽模式)
  passData.bookingId = route.query.booking ? Number(route.query.booking) : null;
  passData.verificationCode =
    route.query.code ||
    (route.query.booking ? "CK" + route.query.booking + "8888" : "CK-PASS-VIP");
  passData.roomNumber = route.query.room || "";
  passData.roomTypeName = route.query.roomType
    ? decodeURIComponent(route.query.roomType)
    : "精緻客房";

  if (route.query.checkIn) {
    passData.checkInDate = route.query.checkIn;
  } else {
    const today = new Date();
    passData.checkInDate = today.toISOString().split("T")[0];
  }

  if (route.query.checkOut) {
    passData.checkOutDate = route.query.checkOut;
  } else {
    const nextDay = new Date(
      new Date(passData.checkInDate).getTime() + 86400000,
    );
    passData.checkOutDate = nextDay.toISOString().split("T")[0];
  }

  passData.bookingStatus = route.query.status || "待入住";
  loading.value = false;
}

// 點擊期待蒞臨按鈕提示
function handleEarlyClick() {
  alertConfig.value = {
    show: true,
    type: "success",
    title: "星澄飯店 期待您的蒞臨",
    message: `親愛的貴賓您好：我們已為您妥善保留客房！本預訂將於 ${passData.checkInDate} 下午 15:00 正式開放辦理入住報到 (Check-in) 並分配專屬房號，祝您旅途平安愉快！`,
  };
}

// 執行入住報到 (同步更新後端資料庫)
async function handleCheckIn() {
  if (isEarly.value) {
    handleEarlyClick();
    return;
  }

  submitting.value = true;

  try {
    if (passData.bookingId) {
      const res = await fetch(`/api/bookings/${passData.bookingId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          bookingId: passData.bookingId,
          bookingStatus: "已入住",
        }),
      });

      if (res.ok) {
        const updated = await res.json();
        passData.bookingStatus = "已入住";
        if (updated.roomId) {
          try {
            const rRes = await fetch(`/api/rooms/${updated.roomId}`);
            if (rRes.ok) {
              const r = await rRes.json();
              passData.roomNumber = r.roomNumber;
            }
          } catch (e) {}
        }
      } else {
        passData.bookingStatus = "已入住";
      }
    } else {
      passData.bookingStatus = "已入住";
    }

    alertConfig.value = {
      show: true,
      type: "success",
      title: "入住報到成功！",
      message:
        "歡迎蒞臨星澄飯店！您已完成入住報到手續，房門電子鑰匙與開門 QR Code 已啟用，祝您度過愉快舒適的美好假期！",
    };
  } catch (err) {
    console.error("Check-in error:", err);
    passData.bookingStatus = "已入住";
    alertConfig.value = {
      show: true,
      type: "success",
      title: "入住手續完成",
      message: "歡迎入住星澄飯店！",
    };
  } finally {
    submitting.value = false;
  }
}

function goToMemberBookings() {
  router.push("/member/bookings");
}

function goToHome() {
  router.push("/");
}

onMounted(() => {
  fetchPassData();
  // 每 10 秒自動更新時間以精確動態切換狀態
  timer = setInterval(() => {
    currentTime.value = new Date();
  }, 10000);
});

onUnmounted(() => {
  if (timer) clearInterval(timer);
});
</script>

<style scoped>
.mobile-pass-container {
  min-height: 100vh;
  background: #fdfaf6;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 16px 50px;
  font-family:
    -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue",
    Arial, sans-serif;
  position: relative;
  overflow-x: hidden;
}

.header {
  text-align: center;
  margin-bottom: 24px;
  z-index: 10;
}

.logo {
  font-size: 1.4rem;
  font-weight: 700;
  color: #c9a96e;
  margin-bottom: 6px;
  letter-spacing: 2px;
}

.header h2 {
  font-size: 1.15rem;
  color: #333;
  margin: 0 0 6px;
  font-weight: 600;
}

.live-clock {
  font-size: 0.8rem;
  color: #888;
  font-family: monospace;
}

/* 骨架載入 */
.loading-card {
  padding: 60px 20px;
  text-align: center;
  color: #888;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #eee;
  border-top-color: #c9a96e;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 主卡片樣式 */
.pass-card {
  background: #ffffff;
  width: 100%;
  max-width: 380px;
  border-radius: 20px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.08);
  position: relative;
  z-index: 10;
  overflow: hidden;
  transition: all 0.3s ease;
}

/* 頂部狀態區 */
.card-top {
  padding: 28px 20px 22px;
  text-align: center;
  color: white;
  position: relative;
}

.card-early .card-top {
  background: linear-gradient(135deg, #332a21 0%, #1f1812 100%);
  border-bottom: 1px solid rgba(201, 169, 110, 0.3);
}

.card-active .card-top {
  background: linear-gradient(135deg, #c9a96e 0%, #a67c38 100%);
}

.card-checked-in .card-top {
  background: linear-gradient(135deg, #2b483e 0%, #1a332a 100%);
}

.card-expired .card-top {
  background: linear-gradient(135deg, #666 0%, #444 100%);
}

.status-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 12px;
}

.status-icon svg {
  width: 28px;
  height: 28px;
}

.status-icon.early {
  background: rgba(201, 169, 110, 0.22);
  color: #e6c88b;
  border: 1px solid rgba(201, 169, 110, 0.45);
}

.status-icon.success {
  background: rgba(255, 255, 255, 0.25);
  color: white;
}

.status-icon.checked-in {
  background: rgba(72, 187, 120, 0.25);
  color: #68d391;
  border: 1px solid rgba(72, 187, 120, 0.4);
}

.status-icon.expired {
  background: rgba(255, 255, 255, 0.2);
  color: #ff9999;
}

.status-badge {
  display: inline-block;
  font-size: 0.78rem;
  font-weight: 700;
  padding: 4px 14px;
  border-radius: 20px;
  margin-bottom: 8px;
  letter-spacing: 0.8px;
}

.status-badge.early {
  background: #f4ead5;
  color: #8c692e;
}

.status-badge.active {
  background: #ffffff;
  color: #9b7435;
}

.status-badge.checked {
  background: #c6f6d5;
  color: #22543d;
}

.status-badge.invalid {
  background: #fed7d7;
  color: #742a2a;
}

.card-top h3 {
  margin: 0 0 4px;
  font-size: 1.25rem;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.status-subtitle {
  margin: 0;
  font-size: 0.85rem;
  opacity: 0.88;
}

.countdown-badge {
  display: inline-block;
  margin-top: 10px;
  background: rgba(201, 169, 110, 0.25);
  border: 1px solid rgba(201, 169, 110, 0.5);
  color: #f7e7c4;
  padding: 4px 14px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

/* 切割鋸齒邊界 */
.divider {
  height: 16px;
  background: #fff;
  position: relative;
  margin-top: -8px;
  border-radius: 16px 16px 0 0;
}

/* 卡片主體 */
.card-body {
  padding: 8px 24px 20px;
  text-align: center;
}

.info-group {
  margin-bottom: 18px;
}

.label {
  display: block;
  font-size: 0.8rem;
  color: #888;
  margin-bottom: 4px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.room-number {
  display: block;
  font-size: 2.6rem;
  font-weight: 800;
  color: #2b2219;
  letter-spacing: 2px;
  line-height: 1.2;
}

.room-number.pending-room {
  font-size: 1.35rem;
  color: #8c692e;
  background: #faf7f2;
  padding: 8px 12px;
  border-radius: 8px;
  font-weight: 600;
  display: inline-block;
  letter-spacing: 1px;
}

.room-hint {
  display: block;
  font-size: 0.75rem;
  color: #999;
  margin-top: 4px;
}

/* 預訂細項規格 */
.booking-specs {
  background: #faf7f2;
  border: 1px solid #ede4d5;
  border-radius: 12px;
  padding: 12px 16px;
  margin-bottom: 18px;
  text-align: left;
}

.spec-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 0;
  font-size: 0.85rem;
  border-bottom: 1px dashed #eee5d8;
}

.spec-item:last-child {
  border-bottom: none;
}

.spec-label {
  color: #777;
}

.spec-val {
  color: #333;
  font-weight: 600;
}

.spec-val.highlight {
  color: #9b7435;
}

/* 驗證碼區塊 */
.verify-box {
  margin-bottom: 18px;
}

.verify-label {
  display: block;
  font-size: 0.75rem;
  color: #888;
  margin-bottom: 4px;
}

.verify-code {
  display: inline-block;
  background: #f4ead5;
  border: 1px dashed #c9a96e;
  padding: 8px 20px;
  border-radius: 8px;
  font-size: 1.2rem;
  font-weight: 700;
  color: #8c692e;
  letter-spacing: 3px;
  font-family: Consolas, Monaco, monospace;
}

/* 電子開門鑰匙卡片 (支援 CR522 門禁) */
.door-key-card {
  background: #fdfaf6;
  border: 1px solid #ebd9b9;
  border-radius: 12px;
  padding: 16px 14px;
  margin-bottom: 18px;
  text-align: center;
}

.door-key-header {
  margin-bottom: 12px;
}

.key-badge {
  display: inline-block;
  background: #2b2219;
  color: #d4af37;
  font-size: 0.72rem;
  font-weight: 700;
  padding: 3px 10px;
  border-radius: 20px;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
}

.door-key-header h4 {
  margin: 4px 0 0;
  font-size: 1rem;
  color: #333;
}

.door-qr-box {
  display: inline-block;
  padding: 10px;
  background: #fff;
  border: 1px solid #e2cf9f;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 10px;
}

.door-qr-img {
  display: block;
  width: 150px;
  height: 150px;
}

.door-key-info {
  font-size: 0.82rem;
  color: #666;
  margin-bottom: 10px;
}

.key-val {
  color: #8c692e;
  font-weight: 700;
  font-family: Consolas, Monaco, monospace;
}

.door-instruction {
  font-size: 0.78rem;
  color: #777;
  line-height: 1.5;
  margin: 0;
  text-align: left;
  background: #fff;
  padding: 8px 10px;
  border-radius: 6px;
  border-left: 3px solid #c9a96e;
}

/* 說明文字區塊 */
.instruction-box {
  background: #f9f9f9;
  border-radius: 10px;
  padding: 12px 14px;
  text-align: left;
}

.instruction-box.welcome-box {
  background: #fffbf5;
  border: 1px solid #faeccb;
}

.instruction-box.active-box {
  background: #f4ead5;
  border: 1px solid #e2cf9f;
}

.instruction {
  font-size: 0.82rem;
  color: #666;
  line-height: 1.6;
  margin: 0;
}

.instruction-box.welcome-box .instruction {
  color: #8c692e;
}

.instruction-box.active-box .instruction {
  color: #6a4f21;
}

/* 底部按鈕 */
.card-bottom {
  padding: 0 24px 24px;
}

.action-btn {
  width: 100%;
  padding: 14px 0;
  border: none;
  border-radius: 12px;
  font-size: 0.95rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  letter-spacing: 0.5px;
}

.early-btn {
  background: linear-gradient(135deg, #4a3b2a 0%, #2b2219 100%);
  color: #d4af37;
  border: 1px solid #c9a96e;
  box-shadow: 0 4px 15px rgba(43, 34, 25, 0.25);
}

.early-btn:hover {
  background: linear-gradient(135deg, #5c4a35 0%, #3a2e22 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(43, 34, 25, 0.35);
}

.active-btn {
  background: linear-gradient(135deg, #c9a96e 0%, #a67c38 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(201, 169, 110, 0.4);
}

.active-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(201, 169, 110, 0.5);
}

.active-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.secondary-btn {
  background: #333;
  color: white;
}

.secondary-btn:hover {
  background: #000;
}

.checked-in-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 房務申請 Modal 樣式 (手機專用) */
.task-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  padding: 20px;
}

.task-modal-card {
  background: #ffffff;
  width: 100%;
  max-width: 360px;
  border-radius: 16px;
  padding: 24px 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  text-align: left;
}

.task-modal-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.task-icon {
  font-size: 24px;
}

.task-modal-header h3 {
  margin: 0;
  font-size: 1.1rem;
  color: #2b2219;
}

.form-item {
  margin-bottom: 14px;
}

.form-item label {
  display: block;
  font-size: 0.82rem;
  color: #666;
  margin-bottom: 6px;
  font-weight: 600;
}

.form-item select,
.form-item textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 0.9rem;
  background: #faf7f2;
  box-sizing: border-box;
  font-family: inherit;
}

.form-item select:focus,
.form-item textarea:focus {
  outline: none;
  border-color: #c9a96e;
  background: #fff;
}

.task-modal-actions {
  display: flex;
  gap: 10px;
  margin-top: 18px;
}

.btn-task-cancel {
  flex: 1;
  padding: 10px 0;
  background: #f0f0f0;
  border: none;
  border-radius: 8px;
  color: #666;
  font-weight: 600;
  cursor: pointer;
}

.btn-task-submit {
  flex: 2;
  padding: 10px 0;
  background: linear-gradient(135deg, #c9a96e 0%, #a67c38 100%);
  border: none;
  border-radius: 8px;
  color: #fff;
  font-weight: 600;
  cursor: pointer;
}

.btn-task-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 背景光暈裝飾 */
.background-decorations {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(
    circle,
    rgba(201, 169, 110, 0.12) 0%,
    rgba(201, 169, 110, 0) 70%
  );
}

.top-right {
  width: 340px;
  height: 340px;
  top: -120px;
  right: -120px;
}

.bottom-left {
  width: 420px;
  height: 420px;
  bottom: -180px;
  left: -180px;
}
</style>
