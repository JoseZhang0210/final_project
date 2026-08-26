package com.hotel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.entity.Venue;
import com.hotel.repository.RentalRepository;
import com.hotel.repository.VenueRepository;

/**
 * 場地 Service。
 *
 * Controller 不直接操作 Repository，
 * 而是透過 Service 處理商業邏輯。
 */
@Service
@Transactional
public class VenueService {

    private final VenueRepository venueRepository;
    private final RentalRepository rentalRepository;

    public VenueService(
            VenueRepository venueRepository,
            RentalRepository rentalRepository) {

        this.venueRepository = venueRepository;
        this.rentalRepository = rentalRepository;
    }

    /**
     * 新增或修改場地。
     */
    public Venue save(Venue venue) {
        return venueRepository.save(venue);
    }

    /**
     * 查詢全部場地。
     */
    @Transactional(readOnly = true)
    public List<Venue> findAll() {
        return venueRepository.findAll();
    }

    /**
     * 依場地 ID 查詢。
     */
    @Transactional(readOnly = true)
    public Optional<Venue> findById(Integer id) {
        return venueRepository.findById(id);
    }

    /**
     * 判斷場地是否存在。
     */
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return venueRepository.existsById(id);
    }

    /**
     * 刪除場地。
     *
     * 先由應用程式主動檢查 Rental，
     * 即使某一台開發電腦的 DB FK 尚未同步，
     * 也不允許刪掉已被租借紀錄使用的 Venue。
     */
    public boolean deleteById(Integer id) {

        if (!venueRepository.existsById(id)) {
            return false;
        }

        if (rentalRepository.existsByVenueId(id)) {
            throw new IllegalStateException(
                    "此場地已有租借紀錄，無法刪除");
        }

        venueRepository.deleteById(id);

        /*
         * 強制 SQL 在此交易內真正執行。
         * 若還有其他 FK 阻擋，可以立即取得例外。
         */
        venueRepository.flush();

        return true;
    }
}