package com.hotel.service.impl;

import java.time.LocalDate;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.context.ApplicationEventPublisher;

import com.hotel.event.BookingEvents.BookingPaidEvent;
import com.hotel.model.dto.BookingDTO;
import com.hotel.model.entity.Booking;
import com.hotel.model.entity.Room;
import com.hotel.model.entity.RoomType;
import com.hotel.repository.BookingPaymentRepository;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.RoomTypeRepository;
import com.hotel.service.RoomTaskService;

class BookingServiceImplTest {

    private BookingRepository bookingRepository;
    private RoomRepository roomRepository;
    private RoomTypeRepository roomTypeRepository;
    private BookingPaymentRepository bookingPaymentRepository;
    private RoomTaskService roomTaskService;
    private ApplicationEventPublisher eventPublisher;

    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        roomRepository = mock(RoomRepository.class);
        roomTypeRepository = mock(RoomTypeRepository.class);
        bookingPaymentRepository = mock(BookingPaymentRepository.class);
        roomTaskService = mock(RoomTaskService.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        bookingService = new BookingServiceImpl(
                bookingRepository,
                roomRepository,
                roomTypeRepository,
                bookingPaymentRepository,
                roomTaskService,
                eventPublisher
        );
    }

    @Test
    @DisplayName("建立 Booking 成功時，不得發布 BookingPaidEvent 寄送付款確認信")
    void testInsertBookingDoesNotPublishPaidEvent() {
        BookingDTO dto = new BookingDTO();
        dto.setMemberId(1);
        dto.setRoomTypeId(10);
        dto.setCheckInDate(LocalDate.now().plusDays(1));
        dto.setCheckOutDate(LocalDate.now().plusDays(3));

        Room room = new Room();
        room.setRoomId(101);
        room.setRoomStatus("可預訂");

        RoomType roomType = new RoomType();
        roomType.setRoomTypeId(10);
        roomType.setPricePerNight(2000);

        when(bookingRepository.findBookedRoomIds(any(), any(), any())).thenReturn(Collections.emptyList());
        when(roomRepository.findByRoomTypeId(10)).thenReturn(Collections.singletonList(room));
        when(roomTypeRepository.findById(10)).thenReturn(java.util.Optional.of(roomType));

        Booking savedBooking = new Booking();
        savedBooking.setBookingId(888);
        savedBooking.setMemberId(1);
        savedBooking.setRoomTypeId(10);
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        BookingDTO result = bookingService.insert(dto);

        assertNotNull(result);
        // 驗證建立 Booking 時完全不觸發 BookingPaidEvent
        verify(eventPublisher, never()).publishEvent(any(BookingPaidEvent.class));
    }
}
