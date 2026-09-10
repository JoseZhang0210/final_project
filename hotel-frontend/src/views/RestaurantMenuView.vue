<script setup>
const restaurants = [
  {
    name: "雲饗中式自助餐廳",
    type: "中式自助餐",
    time: "早餐 06:30 - 10:00｜午餐 11:30 - 14:00｜晚餐 17:30 - 21:00",
    description:
      "匯集經典中式料理、現切肉品與季節海鮮，適合與家人朋友一同享受豐盛饗宴。",
    image:
      "https://images.unsplash.com/photo-1559339352-11d035aa65de?auto=format&fit=crop&w=1200&q=85",
  },
  {
    name: "晨光西式餐廳",
    type: "西式料理",
    time: "午餐 11:30 - 14:00｜晚餐 17:30 - 21:30",
    description:
      "以當季食材呈現精緻西式料理，從前菜、主餐到甜點，享受優雅而舒適的用餐時光。",
    image:
      "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=1200&q=85",
  },
  {
    name: "托斯卡尼義式餐廳",
    type: "義式料理",
    time: "午餐 11:30 - 14:00｜晚餐 17:30 - 21:30",
    description:
      "提供現做義大利麵、手工披薩與經典義式料理，讓您在飯店中感受地中海的熱情風味。",
    image:
      "https://images.unsplash.com/photo-1579751626657-72bc17010498?auto=format&fit=crop&w=1200&q=85",
  },
  {
    name: "星夜駐唱酒吧",
    type: "酒吧・現場音樂",
    time: "每日 19:00 - 00:30",
    description:
      "夜晚由駐唱歌手陪伴，提供調酒、精釀啤酒與輕食；適合旅客在一天結束後放鬆小酌。",
    image:
      "https://images.unsplash.com/photo-1514933651103-005eec06c04b?auto=format&fit=crop&w=1200&q=85",
  },
];
</script>

<template>
  <div class="restaurant-page">
    <section class="hero">
      <div class="hero-content">
        <p class="eyebrow">DINING AT XINGCHENG HOTEL</p>
        <h1>品味每一段美好時光</h1>
        <p>
          從豐盛自助餐、精緻西式料理，到浪漫義式餐點與夜晚駐唱酒吧，<br />
          星澄飯店為您準備不同風味的用餐體驗。
        </p>

        <a href="#restaurant-list" class="hero-button">探索餐廳</a>
      </div>
    </section>

    <section class="intro-section">
      <p class="section-label">RESTAURANT COLLECTION</p>
      <h2>四種風味，一次滿足</h2>
      <p>
        無論是家人聚餐、商務餐敘、浪漫晚餐，或是夜晚想放鬆一下，
        都能在星澄飯店找到適合您的餐桌。
      </p>
    </section>

    <section id="restaurant-list" class="restaurant-grid">
      <article v-for="restaurant in restaurants" :key="restaurant.name" class="restaurant-card">
        <img :src="restaurant.image" :alt="restaurant.name" />

        <div class="card-content">
          <p class="restaurant-type">{{ restaurant.type }}</p>

          <h3>{{ restaurant.name }}</h3>

          <p class="description">{{ restaurant.description }}</p>

          <div class="time-box">
            <span>🕒</span>
            <p>{{ restaurant.time }}</p>
          </div>

          <RouterLink :to="{
            name: 'restaurant-reservation',
            query: { restaurant: restaurant.name },
          }" class="reservation-link">
            立即訂位
          </RouterLink>
        </div>
      </article>
    </section>

    <section class="ending-section">
      <p class="section-label">A TASTE TO REMEMBER</p>
      <h2>讓每一餐，成為旅程中的回憶</h2>
      <p>選擇您喜愛的餐廳，立即預約專屬的用餐時光。</p>
    </section>
  </div>
</template>

<style scoped>
.restaurant-page {
  color: #3d332b;
  background: #fcfaf7;
}

/* 首頁大圖 */
.hero {
  min-height: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 10%;
  text-align: center;
  background:
    linear-gradient(90deg, rgba(30, 24, 18, 0.76), rgba(30, 24, 18, 0.22)),
    url("https://images.unsplash.com/photo-1414235077428-338989a2e8c0?auto=format&fit=crop&w=1800&q=90") center / cover;
}

.hero-content {
  max-width: 650px;
  color: #fff;
}

.eyebrow,
.section-label {
  margin: 0 0 14px;
  color: #c99b55;
  font-size: 13px;
  font-weight: bold;
  letter-spacing: 2px;
}

.hero h1 {
  margin: 0;
  font-size: clamp(42px, 6vw, 68px);
  letter-spacing: 2px;
}

.hero-content>p:not(.eyebrow) {
  max-width: 800px;
  margin: 22px auto 32px;
  color: #f3ece3;
  font-size: 17px;
  line-height: 2;
}

.hero-button {
  display: inline-block;
  padding: 13px 28px;
  border: 1px solid #d8ae6b;
  color: #fff;
  text-decoration: none;
  transition: 0.25s;
}

.hero-button:hover {
  color: #3d2f21;
  background: #d8ae6b;
}

/* 介紹 */
.intro-section,
.ending-section {
  max-width: 760px;
  padding: 90px 20px 55px;
  margin: auto;
  text-align: center;
}

.intro-section h2,
.ending-section h2 {
  margin: 0;
  color: #5c4227;
  font-size: clamp(28px, 4vw, 38px);
}

.intro-section>p:last-child,
.ending-section>p:last-child {
  margin: 18px auto 0;
  color: #776b60;
  line-height: 1.9;
}

/* 餐廳卡片 */
.restaurant-grid {
  width: min(1180px, 90%);
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
  margin: 20px auto 0;
}

.restaurant-card {
  overflow: hidden;
  background: #fff;
  box-shadow: 0 10px 30px rgba(77, 57, 34, 0.1);
  transition: transform 0.25s, box-shadow 0.25s;
}

.restaurant-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 18px 38px rgba(77, 57, 34, 0.18);
}

.restaurant-card img {
  width: 100%;
  height: 260px;
  display: block;
  object-fit: cover;
}

.card-content {
  padding: 28px;
}

.restaurant-type {
  margin: 0 0 10px;
  color: #af7b35;
  font-size: 13px;
  font-weight: bold;
  letter-spacing: 1px;
}

.card-content h3 {
  margin: 0;
  color: #493523;
  font-size: 25px;
}

.description {
  min-height: 76px;
  margin: 16px 0;
  color: #71665b;
  line-height: 1.8;
}

.time-box {
  display: flex;
  gap: 10px;
  align-items: flex-start;
  padding-top: 15px;
  border-top: 1px solid #eee5da;
  color: #6a5540;
  font-size: 14px;
}

.time-box p {
  margin: 0;
  line-height: 1.7;
}

.ending-section {
  padding-bottom: 100px;
}

.reservation-link {
  display: block;
  margin-top: 20px;
  padding: 11px;
  border: 1px solid #a77b3d;
  color: #8d6229;
  text-align: center;
  text-decoration: none;
  font-weight: bold;
  transition: 0.2s;
}

.reservation-link:hover {
  color: #fff;
  background: #a77b3d;
}

@media (max-width: 750px) {
  .hero {
    min-height: 440px;
    padding: 50px 8%;
  }

  .restaurant-grid {
    grid-template-columns: 1fr;
  }

  .restaurant-card img {
    height: 230px;
  }
}
</style>
