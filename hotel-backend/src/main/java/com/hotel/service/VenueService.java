package com.hotel.service;

import java.util.List;
import java.util.Map;
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

    /* 四個既有宴會廳的核定容量上限，後台不得提高後繞過租借人數限制。 */
    private static final Map<Integer, Integer> FIXED_VENUE_CAPACITY_LIMITS =
            Map.of(
                    1, 50,
                    2, 100,
                    3, 200,
                    4, 300);

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
        validateFixedVenueCapacity(venue); // 新增 API 也不可用既有 ID 寫入超額容量。
        venue.setImageUrl(
                normalizeImageUrl(
                        venue.getImageUrl())); // 新增與修改共用同一套圖片網址規則。
        return venueRepository.save(venue);
    }

    /**
     * 查詢全部場地。
     */
    /**
     * 修改既有場地。
     *
     * 不直接 merge 前端傳入的 detached Entity。
     * 先取得目前交易內的 managed Venue，
     * 再只更新允許修改的場地欄位。
     */
    public Venue updateExisting(
            Venue incoming) {

        if (incoming == null
                || incoming.getVenueId() == null) {

            throw new IllegalArgumentException(
                    "場地 ID 不可空白");
        }

        validateFixedVenueCapacity(incoming); // 後端強制固定場地容量上限，不能只依賴畫面 max。

        Venue managed =
                venueRepository
                        .findById(
                                incoming.getVenueId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "找不到場地 ID："
                                                + incoming
                                                        .getVenueId()));

        /*
         * 只更新 Venue 自己的欄位。
         * 不碰 Rental、Member、Payment。
         */
        managed.setVenueName(
                incoming.getVenueName());

        managed.setCapacity(
                incoming.getCapacity());

        managed.setPricePerDay(
                incoming.getPricePerDay());

        managed.setVenueStatus(
                incoming.getVenueStatus());

        managed.setImageUrl(
                normalizeImageUrl(
                        incoming.getImageUrl())); // 保留合法本機圖片，並避免空 scheme 造成例外。

        /*
         * managed Entity 在 Transaction 結束時
         * 由 Hibernate dirty checking 自動寫回資料庫。
         */
        return managed;
    }

    /**
     * 驗證四個固定場地的核定容量上限。
     */
    private void validateFixedVenueCapacity(
            Venue venue) {

        if (venue == null) {
            throw new IllegalArgumentException(
                    "場地資料不可空白");
        }

        Integer capacityLimit =
                FIXED_VENUE_CAPACITY_LIMITS.get(
                        venue.getVenueId());

        if (capacityLimit == null
                || venue.getCapacity() == null
                || venue.getCapacity() <= capacityLimit) {
            return; // 非固定場地或未超額時交由既有欄位驗證繼續處理。
        }

        String venueLabel =
                venue.getVenueName() == null
                        || venue.getVenueName().isBlank()
                        ? "場地 ID " + venue.getVenueId()
                        : "場地「" + venue.getVenueName().trim() + "」";

        throw new IllegalArgumentException(
                venueLabel
                        + "容量不可超過 "
                        + capacityLimit
                        + " 人");
    }

    /**
     * 正規化場地主圖網址。
     *
     * 本機上傳與專案圖片使用站內相對路徑，
     * 外部圖片才要求完整 HTTP 或 HTTPS 網址。
     */
    private String normalizeImageUrl(
            String imageUrl) {

        if (imageUrl == null
                || imageUrl.isBlank()) {
            return null; // 空值交由前端顯示本地預設圖。
        }

        String normalized = imageUrl.trim();

        if (normalized.length() > 1000) {
            throw new IllegalArgumentException(
                    "圖片網址不可超過一千字元");
        }

        if (normalized.startsWith(
                    "/uploads/venues/")
                || normalized.startsWith(
                    "/images/venues/")) {
            return normalized; // 接受系統已建立的場地圖片相對路徑。
        }

        java.net.URI image;

        try {
            image = java.net.URI.create(
                    normalized); // 使用結構化網址解析外部圖片。
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "圖片網址需為有效的 HTTP 或 HTTPS 網址");
        }

        String scheme = image.getScheme();

        if (!("http".equalsIgnoreCase(scheme)
                || "https".equalsIgnoreCase(scheme))
                || image.getHost() == null
                || image.getUserInfo() != null) {
            throw new IllegalArgumentException(
                    "圖片網址需為有效的 HTTP 或 HTTPS 網址");
        }

        return image.toString(); // 保存解析完成的完整外部網址。
    }

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
