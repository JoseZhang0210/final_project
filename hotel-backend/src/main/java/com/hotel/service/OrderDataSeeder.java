package com.hotel.service;

import org.springframework.boot.ApplicationArguments;

import com.hotel.repository.CouponRepository;
import com.hotel.repository.CustomerOrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 原訂單系統資料種子注入器。
 * 
 * 目前已由統一全系統資料初始化器 DatabaseDataInitializer 接管。
 * 本類別予以保留以維護歷史相容性，但移除 @Component 避免重複執行。
 */
@Deprecated
@RequiredArgsConstructor
@Slf4j
public class OrderDataSeeder {

    private final CouponRepository couponRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final OrderDataExchangeService orderDataExchangeService;

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
