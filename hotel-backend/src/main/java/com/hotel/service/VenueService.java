package com.hotel.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.entity.Venue;
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

    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
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
     * 如果場地已經被 rental 使用，
     * SQL Server 外來鍵可能會阻止刪除，
     * 這是正常的資料完整性保護。
     */
    public boolean deleteById(Integer id) {
        if (!venueRepository.existsById(id)) {
            return false;
        }

        venueRepository.deleteById(id);
        return true;
    }
}
