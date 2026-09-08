package com.hotel.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.hotel.repository.CouponRepository;
import com.hotel.repository.CustomerOrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 訂單系統資料種子注入器 (Data Seeder)。
 * 
 * 在 Spring Boot 啟動且 Hibernate 完成 DDL 更新後自動執行。
 * 若檢測到訂單系統資料表為空，自動從 resources/data/order-seed-data.json 注入初始資料，
 * 徹底解決「不跑 createTable / insertAll 導致空表無資料」的問題。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDataSeeder implements ApplicationRunner {

    private final CouponRepository couponRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final OrderDataExchangeService orderDataExchangeService;

    @Override
    public void run(ApplicationArguments args) {
        long couponCount = couponRepository.count();
        long orderCount = customerOrderRepository.count();

        if (couponCount == 0 && orderCount == 0) {
            log.info("【訂單系統】偵測到資料庫為空，啟動 JSON 種子資料自動初始化...");
            int imported = orderDataExchangeService.importFromClasspathSeed();
            log.info("【訂單系統】種子資料初始化完成，共建立 {} 筆訂單及相關關聯資料。", imported);
        } else {
            log.info("【訂單系統】目前已有 {} 筆優惠券、{} 筆訂單，跳過自動初始化。", couponCount, orderCount);
        }
    }
}
