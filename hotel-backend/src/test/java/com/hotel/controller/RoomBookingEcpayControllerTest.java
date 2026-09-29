package com.hotel.controller;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import com.hotel.event.BookingEvents.BookingPaidEvent;
import com.hotel.model.dto.BookingDTO;
import com.hotel.model.entity.BookingPayment;
import com.hotel.repository.BookingPaymentRepository;
import com.hotel.service.BookingPaymentService;
import com.hotel.service.BookingService;
import com.hotel.service.RoomBookingEcpayService;
import com.hotel.service.impl.BookingPaymentServiceImpl;
import com.hotel.util.MailUtil;
import org.springframework.context.ApplicationEventPublisher;

class RoomBookingEcpayControllerTest {

    private RoomBookingEcpayService ecpayService;
    private BookingService bookingService;
    private BookingPaymentService bookingPaymentService;
    private BookingPaymentRepository bookingPaymentRepository;
    private ApplicationEventPublisher eventPublisher;
    private MailUtil mailUtil;

    private RoomBookingEcpayController controller;

    @BeforeEach
    void setUp() {
        ecpayService = mock(RoomBookingEcpayService.class);
        bookingService = mock(BookingService.class);
        bookingPaymentRepository = mock(BookingPaymentRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        mailUtil = mock(MailUtil.class);

        // 使用真實的 BookingPaymentServiceImpl 驗證冪等性與事件發布邏輯
        bookingPaymentService = new BookingPaymentServiceImpl(bookingPaymentRepository, eventPublisher);

        controller = new RoomBookingEcpayController(ecpayService, bookingService, bookingPaymentService);
    }

    @Test
    @DisplayName("Callback CheckMacValue 合法 + RtnCode=1 才能付款成功，並發布 BookingPaidEvent")
    void handleCallbackSuccess() {
        Map<String, String> params = new HashMap<>();
        params.put("MerchantTradeNo", "HOTEL100T1234");
        params.put("TradeNo", "ECPAY999");
        params.put("TradeAmt", "3000");
        params.put("RtnCode", "1");
        params.put("CheckMacValue", "VALID_MAC");

        when(ecpayService.verifyCheckMacValue(params)).thenReturn(true);
        when(bookingPaymentRepository.findByBookingId(100)).thenReturn(Optional.empty());

        ResponseEntity<String> response = controller.handleCallback(params);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("1|OK", response.getBody());

        // 驗證 EventPublisher 有收到 BookingPaidEvent
        verify(eventPublisher, times(1)).publishEvent(any(BookingPaidEvent.class));
        verify(bookingPaymentRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("非法 CheckMacValue 不可更新付款，亦不發布事件")
    void handleCallbackInvalidMac() {
        Map<String, String> params = new HashMap<>();
        params.put("CheckMacValue", "INVALID_MAC");

        when(ecpayService.verifyCheckMacValue(params)).thenReturn(false);

        ResponseEntity<String> response = controller.handleCallback(params);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("CheckMacValue Error"));

        verifyNoInteractions(eventPublisher);
        verify(bookingPaymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Client-Return 僅執行 HTTP 重導向，不可自行將待付款改為已付款")
    void handleClientReturnDoesNotPay() {
        ResponseEntity<Void> response = controller.handleClientReturn(100, "false");

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        verifyNoInteractions(eventPublisher);
        verify(bookingPaymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Client-Confirm 僅查詢付款狀態，若原本為待付款回傳仍必須為待付款，不可自行改已付款")
    void handleClientConfirmDoesNotPay() {
        BookingDTO booking = new BookingDTO();
        booking.setBookingId(100);
        booking.setBookingPrice(3000);

        BookingPayment pendingPayment = new BookingPayment();
        pendingPayment.setBookingId(100);
        pendingPayment.setPaymentStatus("待付款");

        when(bookingService.findById(100)).thenReturn(Optional.of(booking));
        when(bookingPaymentRepository.findByBookingId(100)).thenReturn(Optional.of(pendingPayment));

        ResponseEntity<Map<String, Object>> response = controller.confirmPaymentSuccess(100);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("待付款", response.getBody().get("paymentStatus"));

        verifyNoInteractions(eventPublisher);
        verify(bookingPaymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("重複 callback 具備冪等性，不重複發布 BookingPaidEvent")
    void duplicateCallbackIdempotency() {
        Map<String, String> params = new HashMap<>();
        params.put("MerchantTradeNo", "HOTEL100T1234");
        params.put("TradeNo", "ECPAY999");
        params.put("TradeAmt", "3000");
        params.put("RtnCode", "1");
        params.put("CheckMacValue", "VALID_MAC");

        when(ecpayService.verifyCheckMacValue(params)).thenReturn(true);

        // 模擬第一次呼叫前為待付款，第一次呼叫後變更為已付款
        BookingPayment paymentEntity = new BookingPayment();
        paymentEntity.setBookingId(100);
        paymentEntity.setPaymentStatus("待付款");

        when(bookingPaymentRepository.findByBookingId(100)).thenReturn(Optional.of(paymentEntity));

        // 第一次 Callback
        controller.handleCallback(params);
        paymentEntity.setPaymentStatus("已付款"); // 第一次處理後狀態變成已付款

        // 第二次 Callback (重送)
        controller.handleCallback(params);

        // 驗證只發布過一次事件，避免重複寄信
        verify(eventPublisher, times(1)).publishEvent(any(BookingPaidEvent.class));
    }

    @Test
    @DisplayName("正式 Controller 不得包含 /mock-pay 或 /mock-fail 端點")
    void testNoMockEndpointsInProductionController() {
        for (Method method : RoomBookingEcpayController.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostMapping.class)) {
                PostMapping mapping = method.getAnnotation(PostMapping.class);
                for (String path : mapping.value()) {
                    assertFalse(path.contains("mock-pay"), "控制器不應包含 /mock-pay 端點");
                    assertFalse(path.contains("mock-fail"), "控制器不應包含 /mock-fail 端點");
                }
            }
        }
    }
}
