package com.hotel.service; // 場地付款通知獨立於其他模組。
import java.util.Map; // 接收已提交付款的信件資料。
import com.hotel.repository.MemberRepository; // 只讀取既有會員關聯。
import com.hotel.repository.ProfileRepository; // 只讀取既有姓名與信箱。
import org.springframework.mail.SimpleMailMessage; // 第一版使用純文字信避免注入 HTML。
import org.springframework.mail.javamail.JavaMailSender; // 沿用現有郵件依賴。
import org.springframework.core.env.Environment; // 沿用既有寄件人設定。
import org.springframework.stereotype.Service; // 管理場地專用通知服務。
import org.slf4j.LoggerFactory; // 記錄失敗但不輸出信箱與秘密。
@Service // 不建立其他會員或郵件欄位。
public class RentalMailService { // 郵件失敗不傳回付款交易。
    private final JavaMailSender sender; // 注入既有郵件傳送器。
    private final MemberRepository members; // 只讀取既有會員。
    private final ProfileRepository profiles; // 只讀取既有個人資料。
    private final Environment environment; // 取得寄件人設定而不複製秘密。
    /** 建立使用指定郵件與會員資料來源的通知服務。 */
    public RentalMailService(JavaMailSender sender, MemberRepository members, ProfileRepository profiles, Environment environment) { // 注入四個既有依賴。
        this.sender=sender; this.members=members; this.profiles=profiles; this.environment=environment; // 保存依賴供提交後寄信。
    }
    /** 寄送場地租借付款成功通知。 */
    public void send(Map<String,Object> rental, int amount, String trade) { // 此方法只在付款成功提交後呼叫。
        try { // 所有寄信與查詢錯誤均不得影響付款回呼。
            var member=members.findById(((Number)rental.get("member_id")).intValue()).orElseThrow(); // 沿用會員到帳號關聯。
            var profile=profiles.findByAccountId(member.getAccountId()).orElseThrow(); // 姓名與信箱只來自既有個人資料。
            if (profile.getEmail()==null || profile.getEmail().isBlank()) { throw new IllegalStateException("未設定信箱"); } // 沒有地址只記錄失敗。
            var message=new SimpleMailMessage(); // 使用純文字信件。
            message.setFrom(environment.getRequiredProperty("spring.mail.username")); // 沿用既有寄件人，不硬寫秘密。
            message.setTo(profile.getEmail()); // 只寄給該租借會員。
            message.setSubject("【星澄飯店】場地租借申請成功"); // 使用指定郵件標題。
            message.setText("您好，"+profile.getName()+"：\n租借編號："+rental.get("rental_id")+"\n場地："+rental.get("venue_name")+"\n活動："+rental.get("event_name")+"\n日期："+rental.get("rental_date")+"\n人數："+rental.get("guest_count")+"\n付款金額：NT$"+amount+"\n租借狀態："+rental.get("rental_status")+"\n付款狀態：已付款\n綠界交易編號："+trade); // 只包含使用者可見資訊及歷史金額。
            sender.send(message); // 付款已提交，因此失敗不會回滾付款。
        } catch (Exception exception) { // 含郵件失敗及缺少個人資料皆不向外拋出。
            LoggerFactory.getLogger(getClass()).error("場地付款通知寄送失敗，租借編號：{}，錯誤類型：{}", rental.get("rental_id"), exception.getClass().getSimpleName()); // 不記錄秘密、完整郵件或個人資料。
        }
    }
}
