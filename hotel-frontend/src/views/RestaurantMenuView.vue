<template>
    <main class="restaurant-menu">
        <section class="hero">
            <h1>餐廳管理</h1>
            <p>管理餐廳資料、營業時段與顧客訂位資訊</p>
        </section>

        <section class="menu-card">
            <h2>餐廳管理功能</h2>

            <div class="function-list">
                <RouterLink to="/restaurants" class="function-button">
                    🍽 餐廳資料管理
                    <small>新增、修改、刪除餐廳基本資料</small>
                </RouterLink>

                <RouterLink to="/restaurant-times" class="function-button">
                    🕒 餐廳時段管理
                    <small>管理早餐、午餐、晚餐等營業時段</small>
                </RouterLink>

                <RouterLink to="/reservation-manage" class="function-button">
                    🗓 餐廳訂位管理
                    <small>查看與管理顧客訂位資料</small>
                </RouterLink>
            </div>

            <RouterLink to="/" class="home-button">← 回首頁</RouterLink>
        </section>
    </main>
</template>

<style scoped>
.restaurant-menu {
    min-height: 100vh;
    background: #f7f4ee;
    color: #4d3c28;
}

.hero {
    padding: 56px 24px;
    text-align: center;
    color: white;
    background: linear-gradient(rgba(32, 23, 15, 0.58), rgba(32, 23, 15, 0.58)),
        url('https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=1800&q=80') center / cover;
}

.hero h1 {
    margin: 0 0 12px;
    font-size: 42px;
}

.hero p {
    margin: 0;
    font-size: 18px;
}

.menu-card {
    max-width: 1050px;
    margin: 50px auto;
    padding: 38px;
    text-align: center;
    background: white;
    border-radius: 18px;
    box-shadow: 0 12px 28px rgba(76, 60, 40, 0.12);
}

.menu-card h2 {
    margin-top: 0;
    color: #a77932;
}

.function-list {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 22px;
    margin: 28px 0;
}

.function-button {
    display: flex;
    min-height: 105px;
    padding: 20px;
    flex-direction: column;
    justify-content: center;
    border: 1px solid #e6d5b7;
    border-radius: 14px;
    color: #4d3c28;
    font-size: 20px;
    font-weight: bold;
    text-decoration: none;
    transition: 0.2s;
}

.function-button small {
    margin-top: 12px;
    color: #877b6d;
    font-size: 14px;
    font-weight: normal;
}

.function-button:hover {
    background: #fff8ed;
    border-color: #bf8f42;
    transform: translateY(-3px);
}

.home-button {
    display: inline-block;
    padding: 10px 18px;
    border-radius: 8px;
    background: #eee9e0;
    color: #4d3c28;
    text-decoration: none;
}

@media (max-width: 768px) {
    .function-list {
        grid-template-columns: 1fr;
    }
}
</style>