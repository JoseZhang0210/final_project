package com.hotel.service; // 僅供場地模組的真實資料庫隔離驗收。
import java.time.*; // 產生未來的測試日期。
import java.util.*; // 保存驗收前後的資料筆數。
import org.junit.jupiter.api.Test; // 使用既有測試框架。
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable; // 明確啟用才連線真實測試庫。
import static org.junit.jupiter.api.Assertions.*; // 驗證資料庫與業務結果。
import org.springframework.jdbc.datasource.DriverManagerDataSource; // 建立不依賴完整應用程式的連線。
import org.springframework.jdbc.core.JdbcTemplate; // 使用正式服務所需的 JDBC。
import org.springframework.orm.jpa.*; // 建立僅管理場地實體的交易環境。
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter; // 使用專案既有 Hibernate。
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes; // 精確列出兩個實體，禁止掃描其他模組。
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory; // 建立正式場地 Repository。
import org.springframework.transaction.support.TransactionTemplate; // 測試完成或失敗皆回滾。
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // 模擬已登入會員，不建立帳號。
import org.springframework.web.server.ResponseStatusException; // 驗證跨會員存取遭拒。
import com.hotel.model.entity.*; // 僅將 Rental 與 Venue 註冊為受管理實體。
import com.hotel.repository.*; // 使用正式場地資料存取實作。
import com.hotel.dto.RentalCreateRequest; // 使用正式新增租借請求。
@EnabledIfEnvironmentVariable(named="VENUE_SQL_ACCEPTANCE", matches="true") // 預設測試不會碰資料庫。
class VenueRentalSqlServerTest { // 不啟動 Spring Boot、初始化器、排程或郵件服務。
    /** 驗證真實 SQL Server 流程並回滾所有測試資料。 */
    @Test void realDatabaseFlowRollsBack() { // 驗收真實資料庫交易與會員隔離。
        ((ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(JdbcTemplate.class)).setLevel(ch.qos.logback.classic.Level.DEBUG); // 在測試報告記錄實際 JDBC SQL 範本，不記錄參數或密碼。
        var ds = new DriverManagerDataSource("jdbc:sqlserver://localhost:1433;databaseName=finalproject_git_latest;encrypt=true;trustServerCertificate=true", "hotel_app", Objects.requireNonNull(System.getenv("VENUE_SQL_PASSWORD"))); // 固定唯一核准的測試庫與既有登入。
        var jdbc = new JdbcTemplate(ds); // 所有 JDBC 使用同一資料來源。
        assertEquals("finalproject_git_latest", jdbc.queryForObject("SELECT DB_NAME()", String.class)); // 先確認資料庫再執行任何写入。
        assertEquals(0, jdbc.queryForObject("SELECT IS_ROLEMEMBER('db_owner')", Integer.class)); // 確認未授予資料庫擁有者權限。
        for (String table : List.of("account", "member", "profile", "booking", "room", "product", "restaurant", "coupon")) { // 檢查所有禁止寫入的模組。
            for (String permission : List.of("INSERT", "UPDATE", "DELETE", "ALTER")) { // 不以實際寫入方式測試禁止操作。
                assertNotEquals(1, jdbc.queryForObject("SELECT HAS_PERMS_BY_NAME(?, 'OBJECT', ?)", Integer.class, "dbo." + table, permission)); // 不存在或不可見的表也不可具有操作權。
            } // 結束權限項目檢查。
        } // 結束禁止模組檢查。
        var tables = List.of("venue", "rental", "rental_payment", "account", "member", "profile"); // 僅查詢已授權的資料表。
        var before = new HashMap<String,Integer>(); // 記錄交易前筆數。
        tables.forEach(t -> before.put(t, jdbc.queryForObject("SELECT COUNT(*) FROM dbo." + t, Integer.class))); // 表名均來自上方固定清單。
        var factory = new LocalContainerEntityManagerFactoryBean(); // 不建立完整應用程式上下文。
        factory.setDataSource(ds); // JPA 與 JDBC 共用交易來源。
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter()); // 沿用既有 JPA 引擎。
        factory.setManagedTypes(PersistenceManagedTypes.of(Rental.class.getName(), Venue.class.getName())); // 不載入其他組員實體。
        factory.setJpaPropertyMap(Map.of("hibernate.hbm2ddl.auto", "none", "jakarta.persistence.schema-generation.database.action", "none", "hibernate.show_sql", "true")); // 禁止修改 Schema，並記錄實際 JPA SQL 供審閱。
        factory.afterPropertiesSet(); // 僅初始化兩個場地實體的持久層。
        try { // 確保實體工廠最終關閉。
            var emf = Objects.requireNonNull(factory.getObject()); // 取得隔離持久化工廠。
            assertEquals(Set.of(Rental.class, Venue.class), emf.getMetamodel().getEntities().stream().map(e -> e.getJavaType()).collect(java.util.stream.Collectors.toSet())); // 證明沒有其他模組實體被載入。
            var em = SharedEntityManagerCreator.createSharedEntityManager(emf); // 使用綁定目前交易的實體管理器。
            var repos = new JpaRepositoryFactory(em); // 建立正式 Repository 代理。
            var rentals = repos.getRepository(RentalRepository.class); // 執行正式占用查詢。
            var venues = repos.getRepository(VenueRepository.class); // 僅讀取既有場地。
            var service = new RentalService(rentals, venues, jdbc); // 載入正式租借服務，不修改其他元件。
            var payments = new RentalPaymentRepository(jdbc); // 載入正式場地付款資料層。
            var tx = new TransactionTemplate(new JpaTransactionManager(emf)); // JPA 與 JDBC 寫入由同一交易回滾。
            tx.executeWithoutResult(status -> { // 所有暫存租借與付款都限定在此交易。
                status.setRollbackOnly(); // 即使測試成功也不得留下驗收資料。
                var venue = venues.findAll().stream().filter(v -> "AVAILABLE".equalsIgnoreCase(v.getVenueStatus()) || "可預約".equals(v.getVenueStatus())).findFirst().orElseThrow(); // 使用既有可預約場地，不新增場地。
                var day = LocalDate.now(ZoneId.of("Asia/Taipei")).plusDays(30); // 從未來日期尋找未占用時段。
                while (!service.occupied(venue.getVenueId(), day, day).isEmpty()) day = day.plusDays(1); // 避開使用者既有預約。
                final var date = day; // 保留同一天供重複租借驗證。
                var request = new RentalCreateRequest(venue.getVenueId(), "隔離驗收回滾", date.atTime(15, 0), 1); // 使用既有會員建立可回滾測試租借。
                var saved = service.createForCurrentUser(request, "customer01"); // 執行正式服務的租借及付款 INSERT。
                assertEquals(date.atStartOfDay(), saved.getRentalDate()); // 驗證日期正規化為整天。
                assertEquals(venue.getPricePerDay(), payments.read(saved.getPaymentId()).get("total_price")); // 驗證歷史付款金額。
                assertEquals("待付款", payments.read(saved.getPaymentId()).get("payment_status")); // 真實 SQL Server 保留中文付款狀態。
                assertEquals(1, service.occupied(venue.getVenueId(), date, date).size()); // 驗證正式 JPQL 占用查詢。
                assertThrows(IllegalArgumentException.class, () -> service.createForCurrentUser(new RentalCreateRequest(venue.getVenueId(), "同日不同時間", date.atTime(20, 0), 1), "customer02")); // 不同會員與不同時間仍不可雙訂。
                var owner = new UsernamePasswordAuthenticationToken("customer01", "unused", List.of()); // 僅在隔離測試提供已登入身分。
                var other = new UsernamePasswordAuthenticationToken("customer02", "unused", List.of()); // 使用另一個既有會員。
                assertEquals(saved.getRentalId(), service.findAccessible(saved.getRentalId(), owner).getRentalId()); // 本人可以讀取。
                assertEquals(403, assertThrows(ResponseStatusException.class, () -> service.findAccessible(saved.getRentalId(), other)).getStatusCode().value()); // 他人不可讀取。
                saved.setRentalStatus("CANCELLED"); // 只修改本交易建立的場地租借。
                service.update(saved); em.flush(); // 使用正式管理更新並同步查詢狀態。
                assertTrue(service.occupied(venue.getVenueId(), date, date).isEmpty()); // 取消後釋放日期。
                var replacement = service.createForCurrentUser(request, "customer02"); // 驗證日期釋放後另一會員可預約。
                var trade = "VR" + UUID.randomUUID().toString().replace("-", "").substring(0,18); // 產生不會送到外部金流的測試交易號。
                payments.assign(replacement.getPaymentId(), trade); // 真實驗證交易號寫入與唯一索引相容。
                assertEquals(replacement.getPaymentId(), payments.lockTrade(trade).get("payment_id")); // 驗證交易號查詢與鎖定。
                assertEquals(1, payments.paid(replacement.getPaymentId(), trade, LocalDateTime.now())); // 在可回滾交易驗證付款更新。
                assertEquals(0, payments.paid(replacement.getPaymentId(), trade, LocalDateTime.now())); // 重複通知不得再次更新。
                assertEquals("已付款", payments.read(replacement.getPaymentId()).get("payment_status")); // 驗證付款完成中文狀態。
            }); // 結束交易並回滾所有測試寫入，不觸發寄信。
        } finally { factory.destroy(); } // 不留下背景服務或資料庫連線。
        tables.forEach(t -> assertEquals(before.get(t), jdbc.queryForObject("SELECT COUNT(*) FROM dbo." + t, Integer.class), t)); // 回滾後所有可見表筆數必須一致。
    } // 結束真實資料庫驗收。
} // 結束僅場地模組的隔離測試。
