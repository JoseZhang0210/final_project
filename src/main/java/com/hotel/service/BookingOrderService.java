package com.hotel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.entity.BookingOrder;
import com.hotel.repository.BookingOrderRepository;

@Service
@Transactional
public class BookingOrderService {

    private final BookingOrderRepository bookingOrderRepository;

    public BookingOrderService(BookingOrderRepository bookingOrderRepository) {
        this.bookingOrderRepository = bookingOrderRepository;
    }

    //Create 
    public BookingOrder insert(BookingOrder bookingOrder) {
        return bookingOrderRepository.save(bookingOrder);
    }

    //Read All
    @Transactional(readOnly = true)
    public List<BookingOrder> findAll() {
        return bookingOrderRepository.findAll();
    }

    // Read by ID
    @Transactional(readOnly = true)
    public Optional<BookingOrder> findById(Integer id) {
        return bookingOrderRepository.findById(id);
    }

    //Update 
    public BookingOrder update(Integer id, BookingOrder updatedOrder) {
        return bookingOrderRepository.findById(id)
                .map(order -> {
                    if (updatedOrder.getOrderStatus() != null) {
                        order.setOrderStatus(updatedOrder.getOrderStatus());
                    }
                    if (updatedOrder.getBookingTotalPrice() != null) {
                        order.setBookingTotalPrice(updatedOrder.getBookingTotalPrice());
                    }
                    if (updatedOrder.getPaymentId() != null) {
                        order.setPaymentId(updatedOrder.getPaymentId());
                    }
                    return bookingOrderRepository.save(order);
                })
                .orElseThrow(() -> new RuntimeException("BookingOrder not found with id: " + id));
    }

    //Delete
    public boolean deleteById(Integer id) {
        if (bookingOrderRepository.existsById(id)) {
            bookingOrderRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
