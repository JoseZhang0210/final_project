package com.hotel.repository; // 場地付款專用資料存取，不使用其他付款模組。
import java.util.Map; // 以欄位映射保留既有 JDBC 架構。
import org.springframework.jdbc.core.JdbcTemplate; // 使用現有資料庫連線與交易。
import org.springframework.stereotype.Repository; // 交由框架管理專用資料存取。
@Repository // 僅存取租借付款資料。
public class RentalPaymentRepository { // 付款交易與歷史金額的唯一資料入口。
    private final JdbcTemplate jdbc; // 沿用現有 JDBC 連線。
    /** 建立使用指定 JDBC 連線的付款資料存取物件。 */
    public RentalPaymentRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; } // 注入既有連線元件。
    /** 讀取指定付款紀錄。 */
    public Map<String,Object> read(Integer id) { // 讀取指定付款，不公開給未授權使用者。
        return jdbc.queryForMap("SELECT * FROM dbo.rental_payment WHERE payment_id=?", id); // 固定表名並使用綁定參數。
    }
    /** 鎖定並讀取指定付款紀錄。 */
    public Map<String,Object> lock(Integer id) { // 付款準備與回呼以同一列序列化。
        return jdbc.queryForMap("SELECT * FROM dbo.rental_payment WITH (UPDLOCK,HOLDLOCK) WHERE payment_id=?", id); // 鎖直到外層交易提交。
    }
    /** 依特店交易編號鎖定付款紀錄。 */
    public Map<String,Object> lockTrade(String trade) { // 依特店交易編號取得受鎖保護的付款。
        return jdbc.queryForMap("SELECT * FROM dbo.rental_payment WITH (UPDLOCK,HOLDLOCK) WHERE merchant_trade_no=?", trade); // 唯一索引保證最多一筆。
    }
    /** 為尚未配置交易編號的付款指派編號。 */
    public void assign(Integer id, String trade) { // 只在尚未產生交易編號時配置。
        if (jdbc.update("UPDATE dbo.rental_payment SET merchant_trade_no=? WHERE payment_id=? AND merchant_trade_no IS NULL", trade, id) != 1) { // 防止並行覆蓋既有交易。
            throw new IllegalStateException("付款交易編號已建立"); // 保留原交易以供追查。
        }
    }
    /** 將待付款紀錄條件式更新為已付款。 */
    public int paid(Integer id, String trade, java.time.LocalDateTime at) { // 只從既有中文待付款狀態轉為已付款。
        return jdbc.update("UPDATE dbo.rental_payment SET payment_status=?, payment_method=?, payment_time=?, ecpay_trade_no=? WHERE payment_id=? AND payment_status=?", "已付款", "信用卡", at, trade, id, "待付款"); // 條件更新與唯一索引共同避免重複入帳。
    }
    /** 讀取付款通知信所需的租借與場地資料。 */
    public Map<String,Object> rentalFor(Integer paymentId) { // 信件只取得該筆付款的租借與場地資料。
        return jdbc.queryForMap("SELECT r.*,v.venue_name FROM dbo.rental r JOIN dbo.venue v ON v.venue_id=r.venue_id WHERE r.payment_id=?", paymentId); // 不讀取其他模組付款。
    }
}
