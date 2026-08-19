package com.hotel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.entity.Rental;
import com.hotel.entity.Venue;
import com.hotel.repository.RentalRepository;
import com.hotel.repository.VenueRepository;

/**
 * 場地租借 Service。
 *
 * 除了 CRUD，也負責最基本的租借檢查。
 */
@Service
@Transactional
public class RentalService {

    private final RentalRepository rentalRepository;
    private final VenueRepository venueRepository;

    public RentalService(
            RentalRepository rentalRepository,
            VenueRepository venueRepository) {

        this.rentalRepository = rentalRepository;
        this.venueRepository = venueRepository;
    }

    /**
     * 新增租借。
     *
     * 檢查項目：
     * 1. 場地必須存在。
     * 2. 場地狀態必須為 AVAILABLE。
     * 3. 人數不可大於場地容量。
     * 4. 同一場地、同一時間不可重複預約。
     */
    public Rental create(Rental rental) {

        Venue venue = venueRepository.findById(rental.getVenueId())
                .orElseThrow(() ->
                        new IllegalArgumentException("找不到場地 ID：" + rental.getVenueId()));

        if (!"AVAILABLE".equalsIgnoreCase(venue.getVenueStatus())) {
            throw new IllegalArgumentException("此場地目前不是可租借狀態");
        }

        if (rental.getGuestCount() == null || rental.getGuestCount() <= 0) {
            throw new IllegalArgumentException("參加人數必須大於 0");
        }

        if (rental.getGuestCount() > venue.getCapacity()) {
            throw new IllegalArgumentException(
                    "參加人數不可超過場地容量：" + venue.getCapacity());
        }

        boolean duplicated =
                rentalRepository.existsByVenueIdAndRentalDateAndRentalStatusNot(
                        rental.getVenueId(),
                        rental.getRentalDate(),
                        "CANCELLED");

        if (duplicated) {
            throw new IllegalArgumentException("此場地在該時間已經有租借紀錄");
        }

        // 新增時若沒有指定狀態，預設為待確認。
        if (rental.getRentalStatus() == null
                || rental.getRentalStatus().isBlank()) {
            rental.setRentalStatus("PENDING");
        }

        return rentalRepository.save(rental);
    }

    /**
     * 修改既有租借資料。
     *
     * 這裡採用簡單 CRUD 寫法，
     * 保留 rental_id 不變，再更新其他欄位。
     */
    public Rental update(Rental rental) {

        if (!rentalRepository.existsById(rental.getRentalId())) {
            throw new IllegalArgumentException(
                    "找不到租借 ID：" + rental.getRentalId());
        }

        Venue venue = venueRepository.findById(rental.getVenueId())
                .orElseThrow(() ->
                        new IllegalArgumentException("找不到場地 ID：" + rental.getVenueId()));

        if (rental.getGuestCount() == null || rental.getGuestCount() <= 0) {
            throw new IllegalArgumentException("參加人數必須大於 0");
        }

        if (rental.getGuestCount() > venue.getCapacity()) {
            throw new IllegalArgumentException(
                    "參加人數不可超過場地容量：" + venue.getCapacity());
        }

        return rentalRepository.save(rental);
    }

    /**
     * 查詢全部租借。
     */
    @Transactional(readOnly = true)
    public List<Rental> findAll() {
        return rentalRepository.findAll();
    }

    /**
     * 依租借 ID 查詢。
     */
    @Transactional(readOnly = true)
    public Optional<Rental> findById(Integer id) {
        return rentalRepository.findById(id);
    }

    /**
     * 查詢指定場地的所有租借。
     */
    @Transactional(readOnly = true)
    public List<Rental> findByVenueId(Integer venueId) {
        return rentalRepository.findByVenueId(venueId);
    }

    /**
     * 刪除租借。
     */
    public boolean deleteById(Integer id) {
        if (!rentalRepository.existsById(id)) {
            return false;
        }

        rentalRepository.deleteById(id);
        return true;
    }
}
