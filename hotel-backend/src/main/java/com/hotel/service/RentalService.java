package com.hotel.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.dto.RentalCreateRequest;
import com.hotel.model.entity.Rental;
import com.hotel.model.entity.Venue;
import com.hotel.repository.RentalRepository;
import com.hotel.repository.VenueRepository;

/**
 * 場地租借 Service。
 *
 * V2.0：
 * 1. memberId 由登入帳號取得。
 * 2. rentalId 由後端產生。
 * 3. rental_payment 由後端建立。
 * 4. paymentId 自動回填 Rental。
 * 5. 新 Rental 狀態固定 PENDING。
 */
@Service
@Transactional
public class RentalService {

    private static final Map<String, String> STATUS_ALIASES =
            Map.ofEntries(
                    Map.entry("PENDING", "PENDING"),
                    Map.entry("CONFIRMED", "CONFIRMED"),
                    Map.entry("CANCELLED", "CANCELLED"),
                    Map.entry("COMPLETED", "COMPLETED"),
                    Map.entry("待確認", "PENDING"),
                    Map.entry("待付款", "PENDING"),
                    Map.entry("已確認", "CONFIRMED"),
                    Map.entry("已取消", "CANCELLED"),
                    Map.entry("已完成", "COMPLETED"));

    private final RentalRepository rentalRepository;
    private final VenueRepository venueRepository;
    private final JdbcTemplate jdbcTemplate;

    public RentalService(
            RentalRepository rentalRepository,
            VenueRepository venueRepository,
            JdbcTemplate jdbcTemplate) {

        this.rentalRepository = rentalRepository;
        this.venueRepository = venueRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 會員建立 Rental。
     *
     * 前端只需要送：
     * venueId / eventName / rentalDate / guestCount
     */
    public Rental createForCurrentUser(
            RentalCreateRequest request,
            String username) {

        validateCreateRequest(request);

        Integer memberId = resolveMemberId(username);

        Venue venue = getVenue(request.venueId());

        Rental candidate = new Rental();
        candidate.setVenueId(request.venueId());
        candidate.setMemberId(memberId);
        candidate.setEventName(request.eventName().trim());
        candidate.setRentalDate(request.rentalDate());
        candidate.setGuestCount(request.guestCount());
        candidate.setRentalStatus("PENDING");

        validateVenueAndGuestCount(candidate, venue);

        if (rentalRepository.countActiveCollisions(
                candidate.getVenueId(),
                candidate.getRentalDate()) > 0) {

            throw new IllegalArgumentException(
                    "此場地在該時間已經有租借紀錄");
        }

        /*
         * rental_payment 是共享資料表。
         *
         * 目前 main 的 Java Entity 將 payment_id 視為 IDENTITY，
         * 但 SQL createTable.sql 仍可能是一般 int PK。
         *
         * 因此此處不修改共享 rental_payment Entity，
         * 而是先檢查實際 DB schema，再採取相容寫法。
         */
        Integer paymentId = createPendingPayment(
                memberId,
                venue.getPricePerDay());

        Integer rentalId = insertRental(
                candidate,
                paymentId);

        return rentalRepository.findById(rentalId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "租借已建立，但無法重新讀取 Rental ID：" + rentalId));
    }

    /**
     * 查詢目前登入會員自己的 Rental。
     */
    @Transactional(readOnly = true)
    public List<Rental> findMine(String username) {

        Integer memberId = resolveMemberId(username);

        return rentalRepository
                .findByMemberIdOrderByRentalDateDesc(memberId);
    }

    /**
     * 舊有管理功能：修改 Rental。
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

        if (!"CANCELLED".equals(rental.getRentalStatus())
                && rentalRepository
                        .countActiveCollisionsExcludingRental(
                                rental.getVenueId(),
                                rental.getRentalDate(),
                                rental.getRentalId()) > 0) {

            throw new IllegalArgumentException(
                    "此場地在該時間已經有其他租借紀錄");
        }

        return rentalRepository.save(rental);
    }

    @Transactional(readOnly = true)
    public List<Rental> findAll() {
        return rentalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Rental> findById(Integer id) {
        return rentalRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Rental> findByVenueId(Integer venueId) {
        return rentalRepository.findByVenueId(venueId);
    }

    public boolean deleteById(Integer id) {

        if (!rentalRepository.existsById(id)) {
            return false;
        }

        rentalRepository.deleteById(id);

        return true;
    }

    /**
     * username -> account.account_id -> member.member_id
     *
     * 不使用 ROLE_MEMBER 當成會員資料存在的依據。
     */
    private Integer resolveMemberId(String username) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "無法取得目前登入帳號");
        }

        List<Integer> memberIds = jdbcTemplate.query(
                """
                SELECT m.member_id
                FROM dbo.member m
                INNER JOIN dbo.account a
                    ON a.account_id = m.account_id
                WHERE a.username = ?
                """,
                (rs, rowNum) -> rs.getInt("member_id"),
                username.trim());

        if (memberIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "此登入帳號尚未建立會員資料，無法建立場地租借");
        }

        return memberIds.get(0);
    }

    /**
     * 建立待付款 Payment。
     *
     * 若 payment_id 是 IDENTITY：
     * 交給 SQL Server 自動產生。
     *
     * 若不是 IDENTITY：
     * 在交易內使用 TABLOCKX + HOLDLOCK，
     * 取得 MAX(payment_id)+1 並立即 INSERT，
     * 避免 Rental 端自行要求使用者輸入 Payment ID。
     */
    private Integer createPendingPayment(
            Integer memberId,
            Integer totalPrice) {

        if (isIdentityColumn(
                "dbo.rental_payment",
                "payment_id")) {

            return jdbcTemplate.queryForObject(
                    """
                    INSERT INTO dbo.rental_payment
                        (payment_method,
                         payment_time,
                         total_price,
                         payment_status,
                         member_id)
                    OUTPUT INSERTED.payment_id
                    VALUES (NULL, NULL, ?, ?, ?)
                    """,
                    Integer.class,
                    totalPrice,
                    "待付款",
                    memberId);
        }

        return jdbcTemplate.queryForObject(
                """
                INSERT INTO dbo.rental_payment
                    (payment_id,
                     payment_method,
                     payment_time,
                     total_price,
                     payment_status,
                     member_id)
                OUTPUT INSERTED.payment_id
                SELECT
                    ISNULL(MAX(payment_id), 0) + 1,
                    NULL,
                    NULL,
                    ?,
                    ?,
                    ?
                FROM dbo.rental_payment WITH (TABLOCKX, HOLDLOCK)
                """,
                Integer.class,
                totalPrice,
                "待付款",
                memberId);
    }

    /**
     * 建立 Rental 並取得後端產生的 rental_id。
     *
     * 同樣相容「目前不是 IDENTITY」以及未來可能改成 IDENTITY
     * 的資料庫版本。
     */
    private Integer insertRental(
            Rental rental,
            Integer paymentId) {

        if (isIdentityColumn(
                "dbo.rental",
                "rental_id")) {

            return jdbcTemplate.queryForObject(
                    """
                    INSERT INTO dbo.rental
                        (venue_id,
                         member_id,
                         event_name,
                         rental_date,
                         guest_count,
                         payment_id,
                         rental_status)
                    OUTPUT INSERTED.rental_id
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """,
                    Integer.class,
                    rental.getVenueId(),
                    rental.getMemberId(),
                    rental.getEventName(),
                    rental.getRentalDate(),
                    rental.getGuestCount(),
                    paymentId,
                    "PENDING");
        }

        return jdbcTemplate.queryForObject(
                """
                INSERT INTO dbo.rental
                    (rental_id,
                     venue_id,
                     member_id,
                     event_name,
                     rental_date,
                     guest_count,
                     payment_id,
                     rental_status)
                OUTPUT INSERTED.rental_id
                SELECT
                    ISNULL(MAX(rental_id), 0) + 1,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?
                FROM dbo.rental WITH (TABLOCKX, HOLDLOCK)
                """,
                Integer.class,
                rental.getVenueId(),
                rental.getMemberId(),
                rental.getEventName(),
                rental.getRentalDate(),
                rental.getGuestCount(),
                paymentId,
                "PENDING");
    }

    /**
     * 直接詢問 SQL Server 欄位是否為 IDENTITY。
     */
    private boolean isIdentityColumn(
            String tableName,
            String columnName) {

        Integer result = jdbcTemplate.queryForObject(
                """
                SELECT COLUMNPROPERTY(
                    OBJECT_ID(?),
                    ?,
                    'IsIdentity')
                """,
                Integer.class,
                tableName,
                columnName);

        return result != null && result == 1;
    }

    private Venue getVenue(Integer venueId) {

        return venueRepository.findById(venueId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "找不到場地 ID：" + venueId));
    }

    private void validateCreateRequest(
            RentalCreateRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "租借資料不可空白");
        }

        if (request.venueId() == null
                || request.venueId() <= 0) {
            throw new IllegalArgumentException(
                    "請選擇場地");
        }

        if (request.eventName() == null
                || request.eventName().isBlank()) {
            throw new IllegalArgumentException(
                    "活動名稱不可空白");
        }

        if (request.eventName().trim().length() > 50) {
            throw new IllegalArgumentException(
                    "活動名稱不可超過 50 個字元");
        }

        if (request.rentalDate() == null) {
            throw new IllegalArgumentException(
                    "租借日期不可空白");
        }

        if (request.guestCount() == null
                || request.guestCount() <= 0) {
            throw new IllegalArgumentException(
                    "參加人數必須大於 0");
        }
    }

    /**
     * 管理端 update 使用的完整欄位驗證。
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

    private void validateVenueAndGuestCount(
            Rental rental,
            Venue venue) {

        if (!"CANCELLED".equals(rental.getRentalStatus())
                && !isVenueAvailable(venue.getVenueStatus())) {

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
     * 同時相容：
     * AVAILABLE / 可預約
     */
    private boolean isVenueAvailable(String venueStatus) {

        if (venueStatus == null) {
            return false;
        }

        String status = venueStatus.trim();

        return "AVAILABLE".equalsIgnoreCase(status)
                || "可預約".equals(status);
    }

    private void normalizeRentalStatus(Rental rental) {

        if (rental.getRentalStatus() == null
                || rental.getRentalStatus().isBlank()) {

            rental.setRentalStatus("PENDING");
            return;
        }

        String key = rental.getRentalStatus()
                .trim()
                .toUpperCase(Locale.ROOT);

        String normalized = STATUS_ALIASES.get(key);

        if (normalized == null) {
            throw new IllegalArgumentException(
                    "租借狀態只能是 PENDING、CONFIRMED、CANCELLED、COMPLETED");
        }

        rental.setRentalStatus(normalized);
    }
}