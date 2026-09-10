<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from "vue";

const experiences = [
  {
    title: "舒適住宿",
    english: "STAY & RELAX",
    description: "留一段時間給自己，在星澄展開一夜好眠與悠閒旅程。",
    photo: "photo-1611892440504-42a792e24d32",
    to: "/room-booking",
    action: "探索住宿",
  },
  {
    title: "精緻餐飲",
    english: "TASTE & GATHER",
    description: "與重要的人共享一席佳餚，讓相聚的時刻值得回味。",
    photo: "photo-1517248135467-4c7edcad34c4",
    to: "/restaurant-menu",
    action: "探索餐廳",
  },
  {
    title: "飯店商品",
    english: "SHOP & ENJOY",
    description: "挑選喜愛的飯店好物，把旅途中的舒適與質感帶回日常。",
    photo: "/images/home-hotel-gourmet.png",
    to: "/products",
    action: "探索商品",
  },
  {
    title: "場地租借",
    english: "MEET & CELEBRATE",
    description: "為相聚、交流與重要活動，找到承載美好回憶的空間。",
    photo: "photo-1519167758481-83f550bb49b3",
    to: "/rentals",
    action: "探索場地",
  },
];
const selected = ref(0);
const rotation = ref(0);
const autoPlayPaused = ref(false);
const autoPlayActive = ref(false);
const current = computed(() => experiences[selected.value]);
const AUTO_ROTATE_DELAY = 5000;

let autoRotateTimer = null;
let reducedMotionQuery = null;
let pointerInsideWheel = false;
let focusInsideCarousel = false;

const photoUrl = (item, width) =>
  item.photo.startsWith("/")
    ? item.photo
    : `https://images.unsplash.com/${item.photo}?auto=format&fit=crop&w=${width}&q=80`;

function stopAutoRotate() {
  if (autoRotateTimer !== null) {
    window.clearInterval(autoRotateTimer);
    autoRotateTimer = null;
  }

  autoPlayActive.value = false;
}

function syncAutoRotate() {
  stopAutoRotate();

  if (
    autoPlayPaused.value ||
    pointerInsideWheel ||
    focusInsideCarousel ||
    document.hidden ||
    reducedMotionQuery?.matches
  ) {
    return;
  }

  autoRotateTimer = window.setInterval(() => {
    select(selected.value + 1, false);
  }, AUTO_ROTATE_DELAY);
  autoPlayActive.value = true;
}

function select(index, restartAutoRotate = true) {
  const target = (index + experiences.length) % experiences.length;
  let steps = target - selected.value;
  if (steps > 2) steps -= 4;
  if (steps < -2) steps += 4;
  rotation.value -= steps * 90;
  selected.value = target;

  if (restartAutoRotate) {
    syncAutoRotate();
  }
}

function handlePointerEnter() {
  pointerInsideWheel = true;
  syncAutoRotate();
}

function handlePointerLeave() {
  pointerInsideWheel = false;
  syncAutoRotate();
}

function handleFocusIn() {
  focusInsideCarousel = true;
  syncAutoRotate();
}

function handleFocusOut(event) {
  focusInsideCarousel = event.currentTarget.contains(event.relatedTarget);
  syncAutoRotate();
}

function toggleAutoPlay() {
  autoPlayPaused.value = !autoPlayPaused.value;
  syncAutoRotate();
}

onMounted(() => {
  reducedMotionQuery = window.matchMedia("(prefers-reduced-motion: reduce)");
  reducedMotionQuery.addEventListener("change", syncAutoRotate);
  document.addEventListener("visibilitychange", syncAutoRotate);
  syncAutoRotate();
});

onBeforeUnmount(() => {
  stopAutoRotate();
  reducedMotionQuery?.removeEventListener("change", syncAutoRotate);
  document.removeEventListener("visibilitychange", syncAutoRotate);
});
</script>

<template>
  <section
    class="experience-hero"
    aria-label="星澄飯店服務輪盤"
    @focusin="handleFocusIn"
    @focusout="handleFocusOut"
  >
    <div class="experience-backgrounds" aria-hidden="true">
      <img
        v-for="(item, index) in experiences"
        :key="item.title"
        :src="photoUrl(item, 1920)"
        alt=""
        :class="{ visible: selected === index }"
        :fetchpriority="index === 0 ? 'high' : 'low'"
      />
    </div>
    <div class="experience-shade" aria-hidden="true"></div>

    <div
      class="experience-wheel"
      @mouseenter="handlePointerEnter"
      @mouseleave="handlePointerLeave"
    >
      <div class="wheel-marker" aria-hidden="true"></div>
      <div class="wheel-ring" :style="{ transform: `rotate(${rotation}deg)` }">
        <button
          v-for="(item, index) in experiences"
          :key="item.title"
          type="button"
          class="wheel-segment"
          :class="{ selected: selected === index }"
          :style="{ '--angle': `${index * 90}deg` }"
          :aria-label="`切換至${item.title}`"
          :aria-pressed="selected === index"
          @click="select(index)"
        >
          <span
            class="segment-photo"
            :style="{ backgroundImage: `url(${photoUrl(item, 700)})` }"
          ></span>
          <span
            class="segment-label"
            :style="{ transform: `rotate(${-rotation - index * 90}deg)` }"
            >{{ item.title }}</span
          >
        </button>
      </div>

      <div class="wheel-center">
        <p class="wheel-brand">星澄飯店歡迎您</p>
        <div :aria-live="autoPlayActive ? 'off' : 'polite'" aria-atomic="true">
          <p class="wheel-eyebrow">{{ current.english }}</p>
          <h1>{{ current.title }}</h1>
          <p class="wheel-description">{{ current.description }}</p>
        </div>
        <RouterLink :to="current.to" class="wheel-link"
          >{{ current.action }} <span aria-hidden="true">→</span></RouterLink
        >
      </div>
    </div>

    <div class="wheel-controls" aria-label="輪盤切換">
      <button
        type="button"
        aria-label="上一個服務"
        @click="select(selected - 1)"
      >
        ‹
      </button>
      <span class="wheel-count">0{{ selected + 1 }} <span>/ 04</span></span>
      <button
        type="button"
        aria-label="下一個服務"
        @click="select(selected + 1)"
      >
        ›
      </button>
      <button
        type="button"
        class="autoplay-toggle"
        :aria-label="autoPlayPaused ? '繼續自動播放' : '暫停自動播放'"
        :aria-pressed="autoPlayPaused"
        @click="toggleAutoPlay"
      >
        <svg v-if="autoPlayPaused" viewBox="0 0 24 24" aria-hidden="true">
          <path d="m9 7 8 5-8 5V7Z" />
        </svg>
        <svg v-else viewBox="0 0 24 24" aria-hidden="true">
          <path d="M9 7v10M15 7v10" />
        </svg>
      </button>
    </div>
    <a href="#home-services" class="wheel-scroll"
      >探索更多 <span aria-hidden="true">↓</span></a
    >
  </section>
</template>

<style scoped>
.experience-hero {
  position: relative;
  isolation: isolate;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 18px;
  min-height: 740px;
  padding: 48px 20px 28px;
  overflow: hidden;
  background: #443c30;
}
.experience-backgrounds,
.experience-shade {
  position: absolute;
  inset: 0;
  z-index: -1;
}
.experience-backgrounds img {
  position: absolute;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0;
  transition: opacity 700ms ease;
}
.experience-backgrounds img.visible {
  opacity: 1;
}
.experience-shade {
  background: linear-gradient(180deg, #15242d30, #151e2466);
}
.experience-wheel {
  position: relative;
  width: min(580px, 84vw);
  aspect-ratio: 1;
  flex-shrink: 0;
}
.experience-wheel::before {
  content: "";
  position: absolute;
  inset: -14px;
  border: 2px dotted #ead6b2;
  border-radius: 50%;
  pointer-events: none;
}
.wheel-marker {
  position: absolute;
  z-index: 3;
  top: -26px;
  left: calc(50% - 2px);
  width: 4px;
  height: 28px;
  background: #f3dfba;
}
.wheel-ring {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  overflow: hidden;
  clip-path: circle(50% at 50% 50%);
  transition: transform 800ms cubic-bezier(0.22, 0.7, 0.25, 1);
}
.wheel-segment {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  padding: 0;
  border: 0;
  border-radius: 50%;
  background: transparent;
  clip-path: polygon(50% 50%, 0 0, 100% 0);
  transform: rotate(var(--angle));
  cursor: pointer;
  transition: none;
}
.wheel-segment:hover {
  transform: rotate(var(--angle));
}
.segment-photo {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  filter: brightness(0.58);
  transition: filter 250ms;
}
.wheel-segment.selected .segment-photo,
.wheel-segment:hover .segment-photo {
  filter: brightness(0.95);
}
.wheel-segment:focus-visible {
  outline: none;
}
.wheel-segment:focus-visible .segment-photo {
  box-shadow: inset 0 0 0 7px #ffe3a5;
  filter: brightness(1);
}
.segment-label {
  position: absolute;
  left: calc(50% - 48px);
  top: 8%;
  width: 96px;
  color: white;
  font-size: 15px;
  font-weight: 700;
  text-shadow: 0 1px 5px #000;
  transition: transform 800ms cubic-bezier(0.22, 0.7, 0.25, 1);
}
.wheel-center {
  position: absolute;
  inset: 21%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 24px;
  border: 1px solid #ffffffba;
  border-radius: 50%;
  background: #faf7f0f2;
  box-shadow:
    0 0 0 7px #ffffff40,
    0 8px 30px #0002;
  color: #443929;
}
.wheel-brand {
  font-size: 14px;
  letter-spacing: 0.14em;
  margin: 0 0 20px;
  color: #6c5637;
}
.wheel-eyebrow {
  font-size: 11px;
  letter-spacing: 0.15em;
  color: #77592c;
  margin: 0 0 8px;
}
.wheel-center h1 {
  margin: 0 0 12px;
  font-size: 32px;
  letter-spacing: 0.12em;
}
.wheel-description {
  font-size: 14px;
  line-height: 1.8;
  margin: 0;
}
.wheel-link {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  min-height: 44px;
  margin-top: 12px;
  color: #775322;
  text-underline-offset: 5px;
  font-size: 14px;
}
.wheel-controls {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-top: 6px;
  color: #fff;
}
.wheel-controls button {
  display: grid;
  place-items: center;
  width: 44px;
  height: 44px;
  padding: 0;
  border: 1px solid #dec7a3;
  border-radius: 50%;
  background: #fffcf5;
  color: #775322;
  font-size: 28px;
}
.wheel-controls .autoplay-toggle {
  margin-left: -12px;
}
.autoplay-toggle svg {
  width: 18px;
  height: 18px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}
.wheel-count {
  font-size: 15px;
  letter-spacing: 0.15em;
}
.wheel-count span {
  opacity: 0.75;
}
.wheel-scroll {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  min-height: 44px;
  color: white;
  font-size: 13px;
  text-decoration: none;
}
.wheel-link:focus-visible,
.wheel-controls button:focus-visible,
.wheel-scroll:focus-visible {
  outline: 3px solid #bc8a3a;
  outline-offset: 4px;
}
@media (max-width: 600px) {
  .experience-hero {
    min-height: 580px;
    padding-top: 40px;
  }
  .experience-wheel {
    width: min(440px, 90vw);
  }
  .wheel-center {
    inset: 20%;
    padding: 14px;
  }
  .wheel-brand {
    font-size: 11px;
    margin-bottom: 10px;
    letter-spacing: 0.04em;
  }
  .wheel-eyebrow {
    font-size: 9px;
    letter-spacing: 0.05em;
  }
  .wheel-center h1 {
    font-size: 23px;
    margin-bottom: 8px;
  }
  .wheel-description {
    font-size: 12px;
    line-height: 1.5;
  }
  .wheel-link {
    margin-top: 2px;
    font-size: 12px;
  }
  .segment-label {
    font-size: 12px;
    top: 6%;
  }
}
@media (prefers-reduced-motion: reduce) {
  .wheel-ring,
  .segment-label,
  .segment-photo,
  .experience-backgrounds img {
    transition: none;
  }
}
</style>
