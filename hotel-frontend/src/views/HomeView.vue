<script setup>
import HomeExperienceWheel from "@/components/HomeExperienceWheel.vue";
</script>

<template>
  <div class="home-page">
    <HomeExperienceWheel />
    <div>
      <section
        id="home-services"
        class="home-section home-services"
        aria-labelledby="services-title"
      >
        <div class="home-section__heading">
          <p class="home-section__eyebrow">OUR SERVICES</p>
          <h2 id="services-title">展開你的星澄體驗</h2>
          <p>無論是一夜好眠、一席佳餚，或把飯店質感帶回家，都能從這裡開始。</p>
        </div>

        <div class="home-services__grid">
          <article class="service-card">
            <div class="service-card__icon" aria-hidden="true">
              <svg viewBox="0 0 24 24">
                <path
                  d="M4 19v-8m16 8v-6a2 2 0 0 0-2-2H6a2 2 0 0 0-2 2v6m0-3h16M7 11V7a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v4"
                />
              </svg>
            </div>
            <p class="service-card__number">01</p>
            <h3>舒適住宿</h3>
            <p>選擇適合你的房型與日期，輕鬆安排下一段悠閒旅程。</p>
            <RouterLink to="/room-booking" class="service-card__link">
              查看房型
              <svg aria-hidden="true" viewBox="0 0 24 24">
                <path d="m9 18 6-6-6-6" />
              </svg>
            </RouterLink>
          </article>

          <article class="service-card">
            <div class="service-card__icon" aria-hidden="true">
              <svg viewBox="0 0 24 24">
                <path
                  d="M7 3v8m-3-8v5a3 3 0 0 0 6 0V3m-3 8v10M17 3v18m0-18c2 2 3 4 3 7h-3"
                />
              </svg>
            </div>
            <p class="service-card__number">02</p>
            <h3>精緻餐飲</h3>
            <p>以當季食材與細緻料理，為相聚時光增添值得回味的風景。</p>
            <RouterLink to="/restaurant-menu" class="service-card__link">
              瀏覽餐廳
              <svg aria-hidden="true" viewBox="0 0 24 24">
                <path d="m9 18 6-6-6-6" />
              </svg>
            </RouterLink>
          </article>

          <article class="service-card">
            <div class="service-card__icon" aria-hidden="true">
              <svg viewBox="0 0 24 24">
                <path d="M6 8h12l1 13H5L6 8Zm3 2V6a3 3 0 0 1 6 0v4" />
              </svg>
            </div>
            <p class="service-card__number">03</p>
            <h3>飯店嚴選</h3>
            <p>精選寢具、香氛與特色商品，讓旅途中的舒適延續到日常。</p>
            <RouterLink to="/products" class="service-card__link">
              前往商城
              <svg aria-hidden="true" viewBox="0 0 24 24">
                <path d="m9 18 6-6-6-6" />
              </svg>
            </RouterLink>
          </article>
        </div>
      </section>

      <section class="home-promise" aria-labelledby="promise-title">
        <div class="home-promise__intro">
          <p class="home-section__eyebrow">OUR PROMISE</p>
          <h2 id="promise-title">用心，藏在每個細節裡</h2>
        </div>

        <div class="home-promise__items">
          <div class="promise-item">
            <svg aria-hidden="true" viewBox="0 0 24 24">
              <path
                d="m12 3 2.4 4.9 5.4.8-3.9 3.8.9 5.4-4.8-2.5-4.8 2.5.9-5.4-3.9-3.8 5.4-.8L12 3Z"
              />
            </svg>
            <div>
              <h3>品質嚴選</h3>
              <p>從住宿到商品，維持一致的品質標準。</p>
            </div>
          </div>

          <div class="promise-item">
            <svg aria-hidden="true" viewBox="0 0 24 24">
              <path d="M20 12a8 8 0 1 1-3-6.2M20 4v6h-6m-5 2 2 2 4-5" />
            </svg>
            <div>
              <h3>安心服務</h3>
              <p>清楚的流程與資訊，讓每次選擇更放心。</p>
            </div>
          </div>

          <div class="promise-item">
            <svg aria-hidden="true" viewBox="0 0 24 24">
              <path
                d="M12 21s-8-4.7-8-11a4 4 0 0 1 7-2.6L12 9l1-1.6A4 4 0 0 1 20 10c0 6.3-8 11-8 11Z"
              />
            </svg>
            <div>
              <h3>真誠款待</h3>
              <p>以貼近需求的服務，陪你創造美好回憶。</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.home-page {
  --home-gold: #b58a46;
  --home-gold-dark: #795525;
  --home-ink: #342a20;
  --home-muted: #70675e;
  --home-cream: #f8f5ef;
  overflow: hidden;
  color: var(--home-ink);
}

.home-hero {
  position: relative;
  min-height: clamp(520px, 72vh, 720px);
  display: grid;
  place-items: start;
  padding: 72px max(24px, 8vw) 92px;
  isolation: isolate;
  color: #fff;
  text-align: left;
  background: url("https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1920&q=85")
    center / cover no-repeat;
}

.home-hero__overlay {
  position: absolute;
  inset: 0;
  z-index: -1;
  background:
    linear-gradient(
      90deg,
      rgba(27, 21, 15, 0.82) 0%,
      rgba(31, 24, 17, 0.58) 48%,
      rgba(31, 24, 17, 0.28) 100%
    ),
    linear-gradient(0deg, rgba(18, 14, 10, 0.35), transparent 45%);
}

.home-hero__content {
  width: 100%;
  margin-left: -400px;
}

.home-hero__eyebrow,
.home-section__eyebrow {
  margin: 0 0 16px;
  color: #dabb83;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.22em;
}

.home-hero h1 {
  max-width: 720px;
  margin: 0;
  color: #fff;
  font-family: "Noto Serif TC", "Microsoft JhengHei", serif;
  font-size: clamp(40px, 5vw, 68px);
  font-weight: 700;
  line-height: 1.24;
  letter-spacing: 0.06em;
  text-wrap: balance;
  white-space: nowrap;
}

.home-hero__description {
  max-width: 630px;
  margin: 24px 0 0;
  color: rgba(255, 255, 255, 0.9);
  font-size: clamp(16px, 1.5vw, 19px);
  line-height: 1.9;
}

.service-card__link svg,
.home-hero__scroll svg {
  width: 18px;
  height: 18px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.service-card__link:focus-visible,
.home-hero__scroll:focus-visible {
  outline: 3px solid #fff;
  outline-offset: 4px;
}

.service-card__link:focus-visible {
  outline-color: var(--home-gold-dark);
}

.home-hero__scroll {
  position: absolute;
  bottom: 28px;
  left: 50%;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 44px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 13px;
  letter-spacing: 0.12em;
  text-decoration: none;
  transform: translateX(-50%);
}

.home-section {
  padding: 88px max(24px, 8vw) 96px;
}
.home-services {
  background: var(--home-cream);
}

.home-section__heading {
  width: min(720px, 100%);
  margin: 0 auto 48px;
  text-align: center;
}

.home-section__heading .home-section__eyebrow,
.home-promise .home-section__eyebrow {
  color: var(--home-gold-dark);
}

.home-section__heading h2,
.home-promise__intro h2 {
  margin: 0;
  color: var(--home-ink);
  font-family: "Noto Serif TC", "Microsoft JhengHei", serif;
  font-size: clamp(30px, 3.5vw, 44px);
  line-height: 1.35;
  letter-spacing: 0.05em;
}

.home-section__heading > p:last-child {
  margin: 16px auto 0;
  color: var(--home-muted);
  font-size: 16px;
  line-height: 1.8;
}

.home-services__grid {
  width: min(1180px, 100%);
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 22px;
  margin-inline: auto;
}

.service-card {
  position: relative;
  min-height: 360px;
  display: flex;
  flex-direction: column;
  padding: 36px 34px 32px;
  overflow: hidden;
  border: 1px solid #e9e0d4;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 12px 36px rgba(67, 48, 29, 0.07);
  transition:
    border-color 200ms ease,
    box-shadow 200ms ease,
    transform 200ms ease;
}

.service-card::before {
  content: "";
  position: absolute;
  inset: 0 0 auto;
  height: 4px;
  background: var(--home-gold);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 200ms ease;
}

.service-card:hover {
  border-color: #d6c2a2;
  box-shadow: 0 18px 42px rgba(67, 48, 29, 0.12);
  transform: translateY(-4px);
}
.service-card:hover::before {
  transform: scaleX(1);
}

.service-card__icon {
  width: 58px;
  height: 58px;
  display: grid;
  place-items: center;
  margin-bottom: 28px;
  border-radius: 50%;
  color: var(--home-gold-dark);
  background: #f4eadb;
}

.service-card__icon svg {
  width: 28px;
  height: 28px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.7;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.service-card__number {
  position: absolute;
  top: 28px;
  right: 30px;
  margin: 0;
  color: #d9cbb8;
  font-family: Georgia, serif;
  font-size: 28px;
}

.service-card h3 {
  margin: 0 0 12px;
  color: var(--home-ink);
  font-size: 23px;
  letter-spacing: 0.04em;
}
.service-card > p:not(.service-card__number) {
  margin: 0 0 24px;
  color: var(--home-muted);
  font-size: 15px;
  line-height: 1.8;
}

.service-card__link {
  min-height: 44px;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  align-self: flex-start;
  margin-top: auto;
  color: var(--home-gold-dark);
  font-weight: 700;
  text-decoration: none;
}

.service-card__link:hover {
  color: #5d3e18;
  text-decoration: underline;
  text-underline-offset: 5px;
}

.home-promise {
  width: min(1180px, calc(100% - 48px));
  display: grid;
  grid-template-columns: minmax(240px, 0.8fr) minmax(0, 1.7fr);
  gap: 72px;
  margin: 0 auto;
  padding: 82px 0;
}

.home-promise__intro .home-section__eyebrow {
  margin-bottom: 12px;
}
.home-promise__items {
  display: grid;
  gap: 28px;
}

.promise-item {
  display: grid;
  grid-template-columns: 48px 1fr;
  gap: 18px;
  align-items: start;
}

.promise-item + .promise-item {
  padding-top: 28px;
  border-top: 1px solid #e7ded2;
}

.promise-item > svg {
  width: 32px;
  height: 32px;
  margin: 3px 0 0 5px;
  fill: none;
  stroke: var(--home-gold-dark);
  stroke-width: 1.7;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.promise-item h3 {
  margin: 0 0 7px;
  color: var(--home-ink);
  font-size: 18px;
}
.promise-item p {
  margin: 0;
  color: var(--home-muted);
  font-size: 15px;
  line-height: 1.7;
}

@media (max-width: 900px) {
  .home-hero__content {
    width: 100%;
    margin-left: 0;
  }
  .home-hero h1 {
    font-size: clamp(28px, 5.2vw, 42px);
  }
  .home-services__grid {
    grid-template-columns: 1fr;
  }
  .service-card {
    min-height: 280px;
  }
  .home-promise {
    grid-template-columns: 1fr;
    gap: 44px;
  }
}

@media (max-width: 600px) {
  .home-hero {
    min-height: 520px;
    place-items: start;
    padding: 64px 20px 82px;
    background-position: 58% center;
  }

  .home-hero__overlay {
    background: linear-gradient(
      0deg,
      rgba(24, 18, 13, 0.9),
      rgba(28, 21, 15, 0.42)
    );
  }
  .home-hero h1 {
    font-size: clamp(20px, 5.5vw, 34px);
  }
  .home-hero__description {
    margin-top: 18px;
    font-size: 16px;
    line-height: 1.75;
  }
  .home-hero__content {
    width: 100%;
    margin-left: 0;
  }

  .home-hero__scroll {
    display: none;
  }
  .home-section {
    padding: 64px 20px 72px;
  }
  .home-section__heading {
    margin-bottom: 32px;
    text-align: left;
  }
  .service-card {
    min-height: 0;
    padding: 30px 26px 26px;
  }
  .home-promise {
    width: calc(100% - 40px);
    padding: 64px 0 72px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .home-page *,
  .home-page *::before,
  .home-page *::after {
    scroll-behavior: auto !important;
    transition-duration: 0.01ms !important;
  }
}
</style>
