package com.hotel.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.entity.Rental;
import com.hotel.entity.Venue;
import com.hotel.repository.RentalRepository;
import com.hotel.repository.VenueRepository;

/**
 * 場地租借 Service。
 *
 * 負責 Rental CRUD 與基本商業邏輯驗證。
 */
@Service
@Transactional
public class RentalService {

    private static final Set<String> VALID_STATUSES =
            Set.of(
                    "PENDING",
                    "CONFIRMED",
                    "CANCELLED",
                    "COMPLETED");

    private final RentalRepository rentalRepository;
    private final VenueRepository venueRepository;

    public RentalService(
            RentalRepository rentalRepository,
            VenueRepository venueRepository) {

        this.rentalRepository = rentalRepository;
        this.venueRepository = venueRepository;
    }

    /**
     * 新增 Rental。
     */
    public Rental create(Rental rental) {

        validateRequiredFields(rental);

        if (rentalRepository.existsById(rental.getRentalId())) {
            throw new IllegalArgumentException(
                    "租借 ID 已存在：" + rental.getRentalId());
        }

        Venue venue = getVenue(rental.getVenueId());

        normalizeRentalStatus(rental);

        validateVenueAndGuestCount(rental, venue);

        // CANCELLED 不佔用場地，因此不進行撞期檢查。
        if (!"CANCELLED".equals(rental.getRentalStatus())) {

            boolean duplicated =
                    rentalRepository
                            .existsByVenueIdAndRentalDateAndRentalStatusNot(
                                    rental.getVenueId(),
                                    rental.getRentalDate(),
                                    "CANCELLED");

            if (duplicated) {
                throw new IllegalArgumentException(
                        "此場地在該時間已經有租借紀錄");
            }
        }

        return rentalRepository.save(rental);
    }

    /**
     * 修改 Rental。
     */
    public Rental update(Rental rental) {

        if (rental.getRentalId() == null) {
            throw new IllegalArgumentException(
                    "租借 ID 不可空白");
        }

        if (!rentalRepository.existsById(rental.getRentalId())) {
            throw new IllegalArgumentException(
                    "找不到租借 ID：" + rental.getRentalId());
        }

        validateRequiredFields(rental);

        Venue venue = getVenue(rental.getVenueId());

        normalizeRentalStatus(rental);

        validateVenueAndGuestCount(rental, venue);

        // 修改時必須排除自己，否則一定會查到目前這筆 Rental。
        if (!"CANCELLED".equals(rental.getRentalStatus())) {

            boolean duplicated =
                    rentalRepository
                            .existsByVenueIdAndRentalDateAndRentalStatusNotAndRentalIdNot(
                                    rental.getVenueId(),
                                    rental.getRentalDate(),
                                    "CANCELLED",
                                    rental.getRentalId());

            if (duplicated) {
                throw new IllegalArgumentException(
                        "此場地在該時間已經有其他租借紀錄");
            }
        }

        return rentalRepository.save(rental);
    }

    /**
     * 查詢全部。
     */
    @Transactional(readOnly = true)
    public List<Rental> findAll() {
        return rentalRepository.findAll();
    }

    /**
     * 查詢單筆。
     */
    @Transactional(readOnly = true)
    public Optional<Rental> findById(Integer id) {
        return rentalRepository.findById(id);
    }

    /**
     * 查詢指定 Venue 的 Rental。
     */
    @Transactional(readOnly = true)
    public List<Rental> findByVenueId(Integer venueId) {
        return rentalRepository.findByVenueId(venueId);
    }

    /**
     * 刪除。
     */
    public boolean deleteById(Integer id) {

        if (!rentalRepository.existsById(id)) {
            return false;
        }

        rentalRepository.deleteById(id);

        return true;
    }

    /**
     * 取得 Venue。
     */
    private Venue getVenue(Integer venueId) {

        return venueRepository.findById(venueId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "找不到場地 ID：" + venueId));
    }

    /**
     * 檢查 Rental 必填欄位。
     */
    private void validateRequiredFields(Rental rental) {

        if (rental.getRentalId() == null) {
            throw new IllegalArgumentException(
                    "租借 ID 不可空白");
        }

        if (rental.getVenueId() == null) {
            throw new IllegalArgumentException(
                    "場地 ID 不可空白");
        }

        if (rental.getMemberId() == null) {
            throw new IllegalArgumentException(
                    "會員 ID 不可空白");
        }

        if (rental.getPaymentId() == null) {
            throw new IllegalArgumentException(
                    "付款 ID 不可空白");
        }

        if (rental.getEventName() == null
                || rental.getEventName().isBlank()) {
            throw new IllegalArgumentException(
                    "活動名稱不可空白");
        }

        if (rental.getEventName().trim().length() > 50) {
            throw new IllegalArgumentException(
                    "活動名稱不可超過 50 個字元");
        }

        rental.setEventName(
                rental.getEventName().trim());

        if (rental.getRentalDate() == null) {
            throw new IllegalArgumentException(
                    "租借日期不可空白");
        }

        if (rental.getGuestCount() == null
                || rental.getGuestCount() <= 0) {
            throw new IllegalArgumentException(
                    "參加人數必須大於 0");
        }
    }

    /**
     * 驗證 Venue 狀態與人數。
     */
    private void validateVenueAndGuestCount(
            Rental rental,
            Venue venue) {

        /*
         * CANCELLED 是取消紀錄，
         * 不需要要求 Venue 目前仍為 AVAILABLE。
         */
        if (!"CANCELLED".equals(rental.getRentalStatus())
                && !"AVAILABLE".equalsIgnoreCase(
                        venue.getVenueStatus())) {

            throw new IllegalArgumentException(
                    "此場地目前不是可租借狀態");
        }

        if (rental.getGuestCount() > venue.getCapacity()) {

            throw new IllegalArgumentException(
                    "參加人數不可超過場地容量："
                            + venue.getCapacity());
        }
    }

    /**
     * Rental 狀態正規化與驗證。
     */
    private void normalizeRentalStatus(Rental rental) {

        if (rental.getRentalStatus() == null
                || rental.getRentalStatus().isBlank()) {

            rental.setRentalStatus("PENDING");
            return;
        }

        String status =
                rental.getRentalStatus()
                        .trim()
                        .toUpperCase();

        if (!VALID_STATUSES.contains(status)) {

            throw new IllegalArgumentException(
                    "租借狀態只能是 PENDING、CONFIRMED、CANCELLED 或 COMPLETED");
        }

        rental.setRentalStatus(status);
    }
}
