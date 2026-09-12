package com.hotel.repository;

// Java 資料結構。
import java.util.Map;

// Spring JDBC。
import org.springframework.jdbc.core.JdbcTemplate;

// Spring Repository。
import org.springframework.stereotype.Repository;


/**
 * 場地租借付款專用資料存取。
 *
 * 僅操作 dbo.rental_payment，
 * 不使用商城或訂房付款模組。
 */
@Repository
public class RentalPaymentRepository {

    /**
     * 沿用專案既有 JDBC 連線與交易。
     */
    private final JdbcTemplate jdbc;


    /**
     * 建立場地租借付款資料存取物件。
     */
    public RentalPaymentRepository(JdbcTemplate jdbc) {

        this.jdbc = jdbc;
    }


    /**
     * 讀取指定付款紀錄。
     */
    public Map<String, Object> read(Integer id) {

        return jdbc.queryForMap(
            "SELECT * FROM dbo.rental_payment WHERE payment_id=?",
            id
        );
    }


    /**
     * 鎖定並讀取指定付款紀錄。
     *
     * UPDLOCK 與 HOLDLOCK 用於避免
     * 付款準備與付款回呼同時修改同一筆資料。
     */
    public Map<String, Object> lock(Integer id) {

        return jdbc.queryForMap(
            "SELECT * FROM dbo.rental_payment " +
            "WITH (UPDLOCK,HOLDLOCK) " +
            "WHERE payment_id=?",
            id
        );
    }


    /**
     * 依特店交易編號鎖定付款紀錄。
     */
    public Map<String, Object> lockTrade(String trade) {

        return jdbc.queryForMap(
            "SELECT * FROM dbo.rental_payment " +
            "WITH (UPDLOCK,HOLDLOCK) " +
            "WHERE merchant_trade_no=?",
            trade
        );
    }


    /**
     * 為尚未配置交易編號的付款
     * 指派 MerchantTradeNo。
     */
    public void assign(Integer id, String trade) {

        int updated = jdbc.update(
            "UPDATE dbo.rental_payment " +
            "SET merchant_trade_no=? " +
            "WHERE payment_id=? " +
            "AND merchant_trade_no IS NULL",
            trade,
            id
        );

        /*
         * 必須剛好更新一筆。
         *
         * 若沒有更新成功，
         * 代表交易編號可能已被其他流程建立，
         * 因此不允許覆蓋原交易。
         */
        if (updated != 1) {

            throw new IllegalStateException(
                "付款交易編號已建立"
            );
        }
    }


    /**
     * 在付款仍為待付款時安全更新 MerchantTradeNo。
     *
     * previousTrade 必須仍與資料庫一致，
     * 避免兩個付款準備流程互相覆蓋。
     */
    public void replacePendingTrade(
        Integer id,
        String previousTrade,
        String trade
    ) {

        int updated;

        if (previousTrade == null) {

            updated = jdbc.update(
                "UPDATE dbo.rental_payment " +
                "SET merchant_trade_no=? " +
                "WHERE payment_id=? " +
                "AND payment_status=? " +
                "AND merchant_trade_no IS NULL",
                trade,
                id,
                "待付款"
            );

        } else {

            updated = jdbc.update(
                "UPDATE dbo.rental_payment " +
                "SET merchant_trade_no=? " +
                "WHERE payment_id=? " +
                "AND payment_status=? " +
                "AND merchant_trade_no=?",
                trade,
                id,
                "待付款",
                previousTrade
            );
        }

        if (updated != 1) {

            throw new IllegalStateException(
                "付款交易編號更新失敗"
            );
        }
    }

    /**
     * 本機示範付款：同步更新付款與租借狀態。
     *
     * 只供 VENUE_PAYMENT_DEMO=true 的本機測試流程使用，
     * 不代表綠界真實付款驗證。
     */
    public int stageDemoPaid(
        Integer paymentId,
        Integer rentalId,
        String trade,
        java.time.LocalDateTime at
    ) {

        int paymentUpdated = jdbc.update(
            "UPDATE dbo.rental_payment " +
            "SET payment_status=?, " +
            "payment_method=?, " +
            "payment_time=?, " +
            "ecpay_trade_no=? " +
            "WHERE payment_id=? " +
            "AND payment_status=?",
            "已付款",
            "測試付款",
            at,
            trade,
            paymentId,
            "待付款"
        );

        if (paymentUpdated == 0) {
            Map<String, Object> current = read(paymentId);

            if (!"已付款".equals(current.get("payment_status"))) {
                return 0;
            }
        }

        jdbc.update(
            "UPDATE dbo.rental " +
            "SET rental_status=? " +
            "WHERE rental_id=? " +
            "AND payment_id=? " +
            "AND rental_status IN (?,?)",
            "CONFIRMED",
            rentalId,
            paymentId,
            "PENDING",
            "待確認"
        );

        return 1;
    }

    /**
     * 將待付款紀錄更新為已付款。
     *
     * 僅允許從「待付款」狀態轉換，
     * 避免重複付款回呼造成重複入帳。
     */
    public int paid(
        Integer id,
        String trade,
        java.time.LocalDateTime at
    ) {

        return jdbc.update(
            "UPDATE dbo.rental_payment " +
            "SET payment_status=?, " +
            "payment_method=?, " +
            "payment_time=?, " +
            "ecpay_trade_no=? " +
            "WHERE payment_id=? " +
            "AND payment_status=?",
            "已付款",
            "信用卡",
            at,
            trade,
            id,
            "待付款"
        );
    }


    /**
     * 讀取付款通知信所需的
     * 租借與場地資料。
     */
    public Map<String, Object> rentalFor(Integer paymentId) {

        return jdbc.queryForMap(
            "SELECT r.*, v.venue_name " +
            "FROM dbo.rental r " +
            "JOIN dbo.venue v " +
            "ON v.venue_id=r.venue_id " +
            "WHERE r.payment_id=?",
            paymentId
        );
    }
}