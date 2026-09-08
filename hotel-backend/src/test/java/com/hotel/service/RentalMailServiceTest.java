package com.hotel.service; // 場地付款通知失敗策略測試。
import org.junit.jupiter.api.Test; // 使用既有測試框架。
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
}
