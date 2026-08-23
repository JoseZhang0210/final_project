<template>
  <div>
    <section class="hero restaurant">
      <h1>餐廳管理</h1>

      <p>管理餐廳資料、營業時段與顧客訂位資訊</p>
    </section>

    <main class="container">
      <div class="card">
        <h2>餐廳管理功能</h2>

        <div class="menu-grid">
          <RouterLink to="/restaurants" class="menu-item">
            <h3>🍽 餐廳資料管理</h3>

            <p>新增、修改、刪除餐廳基本資料</p>
          </RouterLink>

          <RouterLink to="/restaurant-times" class="menu-item">
            <h3>🕒 餐廳時段管理</h3>

            <p>管理早餐、午餐、晚餐與營業時間</p>
          </RouterLink>

          <RouterLink to="/reservation-manage" class="menu-item">
            <h3>📅 餐廳訂位管理</h3>

            <p>查看與管理顧客訂位資料</p>
          </RouterLink>
        </div>
      </div>
    </main>
  </div>
</template>
