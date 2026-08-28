package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.CustomerOrder;

public interface CustomerOrderRepository
        extends JpaRepository<CustomerOrder, Integer> {

    List<CustomerOrder> findAllByOrderByOrderDateDesc();

    List<CustomerOrder> findByMemberIdOrderByOrderDateDesc(
            Integer memberId);
}