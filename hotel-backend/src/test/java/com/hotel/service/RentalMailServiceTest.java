package com.hotel.service; // 場地付款通知失敗策略測試。
import org.junit.jupiter.api.Test; // 使用既有測試框架。
import org.mockito.ArgumentCaptor;
import static org.junit.jupiter.api.Assertions.*; // 驗證寄信錯誤不向外拋出。
import static org.mockito.Mockito.*; // 不寄送實際郵件或修改會員資料。
import java.util.*; // 提供付款通知中的使用者可見資料。
import com.hotel.repository.MemberRepository; // 模擬既有會員讀取。
import com.hotel.repository.ProfileRepository; // 模擬既有信箱讀取。
import com.hotel.model.entity.Member; // 使用既有會員型別。
import com.hotel.model.entity.Profile; // 使用既有個人資料型別。
import org.springframework.mock.env.MockEnvironment; // 不讀取實際郵件秘密。
import org.springframework.mail.javamail.JavaMailSender; // 模擬郵件傳送失敗。
import org.springframework.mail.SimpleMailMessage; // 驗證純文字信件傳送入口。

class RentalMailServiceTest { // 郵件錯誤不可讓已提交的付款回滾。

    /** 驗證正式 ECPay 付款成功 Email 標題與內容格式正確。 */
    @Test void formalPaymentEmailContentCorrect() {
        var members = mock(MemberRepository.class);
        var profiles = mock(ProfileRepository.class);
        var sender = mock(JavaMailSender.class);

        var member = new Member(); member.setMemberId(1); member.setAccountId(46);
        var profile = new Profile(); profile.setName("張小明"); profile.setEmail("customer@example.com");

        when(members.findById(1)).thenReturn(Optional.of(member));
        when(profiles.findByAccountId(46)).thenReturn(Optional.of(profile));

        var service = new RentalMailService(sender, members, profiles, new MockEnvironment().withProperty("spring.mail.username", "sender@example.com"));

        Map<String, Object> rental = Map.of(
            "rental_id", 101,
            "member_id", 1,
            "venue_name", "豪華會議廳",
            "event_name", "產品發表會",
            "rental_date", "2026-10-15",
            "guest_count", 100
        );

        service.send(rental, 12000, "ECPAY20260905001");

        var captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();

        assertEquals("sender@example.com", msg.getFrom());
        assertEquals("customer@example.com", msg.getTo()[0]);
        assertEquals("【星澄飯店】場地租借付款成功", msg.getSubject());

        String text = msg.getText();
        assertTrue(text.contains("張小明"));
        assertTrue(text.contains("租借編號：101"));
        assertTrue(text.contains("場地：豪華會議廳"));
        assertTrue(text.contains("活動：產品發表會"));
        assertTrue(text.contains("日期：2026-10-15"));
        assertTrue(text.contains("人數：100"));
        assertTrue(text.contains("付款金額：NT$12000"));
        assertTrue(text.contains("付款方式：信用卡 / 綠界 Stage"));
        assertTrue(text.contains("付款狀態：已付款"));
        assertTrue(text.contains("租借狀態：已確認"));
        assertTrue(text.contains("綠界交易編號：ECPAY20260905001"));

        verify(members, never()).save(any());
        verify(profiles, never()).save(any());
    }

    /** 驗證展示用模擬付款 Email 標題與內容格式正確，且編號標示為展示交易編號。 */
    @Test void demoPaymentEmailContentCorrect() {
        var members = mock(MemberRepository.class);
        var profiles = mock(ProfileRepository.class);
        var sender = mock(JavaMailSender.class);

        var member = new Member(); member.setMemberId(1); member.setAccountId(46);
        var profile = new Profile(); profile.setName("李大華"); profile.setEmail("demo@example.com");

        when(members.findById(1)).thenReturn(Optional.of(member));
        when(profiles.findByAccountId(46)).thenReturn(Optional.of(profile));

        var service = new RentalMailService(sender, members, profiles, new MockEnvironment().withProperty("spring.mail.username", "sender@example.com"));

        Map<String, Object> rental = Map.of(
            "rental_id", 202,
            "member_id", 1,
            "venue_name", "多功能宴會廳",
            "event_name", "公司年會",
            "rental_date", "2026-12-25",
            "guest_count", 200
        );

        service.sendDemo(rental, 25000, "DEMO1234567890ABCDEF");

        var captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(captor.capture());
        SimpleMailMessage msg = captor.getValue();

        assertEquals("【星澄飯店】場地租借付款成功", msg.getSubject());

        String text = msg.getText();
        assertTrue(text.contains("李大華"));
        assertTrue(text.contains("租借編號：202"));
        assertTrue(text.contains("付款金額：NT$25000"));
        assertTrue(text.contains("付款方式：展示用模擬付款"));
        assertTrue(text.contains("付款狀態：已付款"));
        assertTrue(text.contains("租借狀態：已確認"));
        assertTrue(text.contains("展示交易編號：DEMO1234567890ABCDEF"));
        assertFalse(text.contains("綠界交易編號"));

        verify(members, never()).save(any());
        verify(profiles, never()).save(any());
    }

    /** 驗證郵件傳送失敗不會回拋至付款流程。 */
    @Test void deliveryFailureDoesNotEscape() { // 信箱存在但 SMTP 失敗的情境。
        var members=mock(MemberRepository.class); var profiles=mock(ProfileRepository.class); var sender=mock(JavaMailSender.class); // 所有資料都只在記憶體中。
        var member=new Member(); member.setMemberId(1); member.setAccountId(46); // 沿用初始化會員關聯編號。
        var profile=new Profile(); profile.setName("會員"); profile.setEmail("customer@example.invalid"); // 無法投遞的測試地址，不是真實新會員。
        when(members.findById(1)).thenReturn(Optional.of(member)); when(profiles.findByAccountId(46)).thenReturn(Optional.of(profile)); // 模擬既有 Repository 的唯讀回應。
        doThrow(new org.springframework.mail.MailSendException("test failure")).when(sender).send(any(SimpleMailMessage.class)); // 模擬 SMTP 傳送例外。
        var service=new RentalMailService(sender,members,profiles,new MockEnvironment().withProperty("spring.mail.username","sender@example.invalid")); // 不使用任何真實秘密。
        assertDoesNotThrow(()->service.send(Map.of("rental_id",1,"member_id",1),5000,"260905000001")); // 例外不得傳回付款回呼。
        verify(members,never()).save(any()); verify(profiles,never()).save(any()); // 寄信服務不得修改會員資料。
    }

    /** 驗證缺少 Email 或 Profile 資訊時不向外拋出例外且不修改會員。 */
    @Test void missingProfileOrEmailDoesNotEscape() {
        var members = mock(MemberRepository.class);
        var profiles = mock(ProfileRepository.class);
        var sender = mock(JavaMailSender.class);

        var member = new Member(); member.setMemberId(1); member.setAccountId(46);
        var profile = new Profile(); profile.setName("無信箱會員"); profile.setEmail("");

        when(members.findById(1)).thenReturn(Optional.of(member));
        when(profiles.findByAccountId(46)).thenReturn(Optional.of(profile));

        var service = new RentalMailService(sender, members, profiles, new MockEnvironment().withProperty("spring.mail.username", "sender@example.com"));

        assertDoesNotThrow(() -> service.sendDemo(Map.of("rental_id", 1, "member_id", 1), 5000, "DEMO123"));
        verify(sender, never()).send(any(SimpleMailMessage.class));
        verify(members, never()).save(any());
        verify(profiles, never()).save(any());
    }
}
