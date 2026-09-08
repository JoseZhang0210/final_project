package com.hotel.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.entity.Reservation;
import com.hotel.model.entity.Restaurant;
import com.hotel.model.entity.RestaurantTime;
import com.hotel.repository.MemberRepository;
import com.hotel.service.ReservationService;
import com.hotel.service.RestaurantService;
import com.hotel.service.RestaurantTimeService;

import lombok.Data;
import lombok.NoArgsConstructor;

@RestController
@RequestMapping("/api/restaurant-backup")
public class RestaurantBackupController {

    private final RestaurantService restaurantService;
    private final RestaurantTimeService restaurantTimeService;
    private final ReservationService reservationService;
    private final MemberRepository memberRepository;

    public RestaurantBackupController(
            RestaurantService restaurantService,
            RestaurantTimeService restaurantTimeService,
            ReservationService reservationService,
            MemberRepository memberRepository) {

        this.restaurantService = restaurantService;
        this.restaurantTimeService = restaurantTimeService;
        this.reservationService = reservationService;
        this.memberRepository = memberRepository;
    }

    // GET /api/restaurant-backup/export?startDate=2026-09-01&endDate=2026-09-30
    @GetMapping("/export")
    public BackupData exportData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        BackupData data = new BackupData();
        data.setVersion(1);

        List<Restaurant> restaurants = restaurantService.findAllRestaurants();
        List<RestaurantTime> times = restaurantTimeService.findAllTimes();
        List<Reservation> exportReservations = new ArrayList<>();
        Set<Integer> usedRestaurantIds = new HashSet<>();
        Set<Integer> usedTimeIds = new HashSet<>();
        boolean hasDateFilter = startDate != null || endDate != null;

        for (Reservation reservation : reservationService.findAllReservations()) {
            if (!isWithinDateRange(reservation.getReservationDate(), startDate, endDate)) {
                continue;
            }

            exportReservations.add(reservation);
            usedRestaurantIds.add(reservation.getRestaurantId());
            usedTimeIds.add(reservation.getTimeId());
        }

        for (Restaurant restaurant : restaurants) {
            if (hasDateFilter && !usedRestaurantIds.contains(restaurant.getRestaurantId())) {
                continue;
            }

            RestaurantData item = new RestaurantData();
            item.setRestaurantName(restaurant.getRestaurantName());
            item.setAddress(restaurant.getAddress());
            item.setPhone(restaurant.getPhone());
            item.setCapacity(restaurant.getCapacity());
            item.setDescription(restaurant.getDescription());

            for (RestaurantTime time : times) {
                if (!Objects.equals(time.getRestaurantId(), restaurant.getRestaurantId())) {
                    continue;
                }

                if (hasDateFilter && !usedTimeIds.contains(time.getTimeId())) {
                    continue;
                }

                TimeData timeData = new TimeData();
                timeData.setMealType(time.getMealType());
                timeData.setOpenTime(time.getOpenTime());
                timeData.setCloseTime(time.getCloseTime());
                item.getTimes().add(timeData);
            }

            data.getRestaurants().add(item);
        }

        for (Reservation reservation : exportReservations) {
            Restaurant restaurant = restaurantService.findById(reservation.getRestaurantId());
            RestaurantTime time = restaurantTimeService.findById(reservation.getTimeId());

            // 若舊資料關聯已遺失，略過該筆，避免產生無法再次匯入的 JSON。
            if (restaurant == null || time == null) {
                continue;
            }

            ReservationData item = new ReservationData();
            item.setMemberId(reservation.getMemberId());
            item.setContactName(reservation.getContactName());
            item.setContactPhone(reservation.getContactPhone());
            item.setRestaurantName(restaurant.getRestaurantName());
            item.setMealType(time.getMealType());
            item.setOpenTime(time.getOpenTime());
            item.setCloseTime(time.getCloseTime());
            item.setReservationDate(reservation.getReservationDate());
            item.setPeopleCount(reservation.getPeopleCount());
            item.setStatus(reservation.getStatus());
            data.getReservations().add(item);
        }

        return data;
    }

    // POST /api/restaurant-backup/import
    // 只新增：不更新、不刪除；同名餐廳、相同時段、相同訂位會略過。
    @PostMapping("/import")
    @Transactional
    public ResponseEntity<ImportResult> importData(@RequestBody BackupData data) {
        if (data == null) {
            return ResponseEntity.badRequest().build();
        }

        ImportResult result = new ImportResult();
        List<Restaurant> restaurants = new ArrayList<>(restaurantService.findAllRestaurants());
        List<RestaurantTime> times = new ArrayList<>(restaurantTimeService.findAllTimes());
        List<Reservation> reservations = new ArrayList<>(reservationService.findAllReservations());

        for (RestaurantData source : safeList(data.getRestaurants())) {
            if (isBlank(source.getRestaurantName())) {
                result.setSkippedRestaurants(result.getSkippedRestaurants() + 1);
                continue;
            }

            Restaurant restaurant = findRestaurant(restaurants, source.getRestaurantName());

            if (restaurant == null) {
                restaurant = new Restaurant();
                restaurant.setRestaurantName(source.getRestaurantName().trim());
                restaurant.setAddress(source.getAddress());
                restaurant.setPhone(source.getPhone());
                restaurant.setCapacity(source.getCapacity());
                restaurant.setDescription(source.getDescription());
                restaurant = restaurantService.save(restaurant);
                restaurants.add(restaurant);
                result.setAddedRestaurants(result.getAddedRestaurants() + 1);
            } else {
                result.setSkippedRestaurants(result.getSkippedRestaurants() + 1);
            }

            for (TimeData sourceTime : safeList(source.getTimes())) {
                if (isBlank(sourceTime.getMealType())
                        || sourceTime.getOpenTime() == null
                        || sourceTime.getCloseTime() == null) {
                    result.setSkippedTimes(result.getSkippedTimes() + 1);
                    continue;
                }

                RestaurantTime time = findTime(
                        times,
                        restaurant.getRestaurantId(),
                        sourceTime.getMealType(),
                        sourceTime.getOpenTime(),
                        sourceTime.getCloseTime());

                if (time != null) {
                    result.setSkippedTimes(result.getSkippedTimes() + 1);
                    continue;
                }

                time = new RestaurantTime();
                time.setRestaurantId(restaurant.getRestaurantId());
                time.setMealType(sourceTime.getMealType().trim());
                time.setOpenTime(sourceTime.getOpenTime());
                time.setCloseTime(sourceTime.getCloseTime());
                time = restaurantTimeService.save(time);
                times.add(time);
                result.setAddedTimes(result.getAddedTimes() + 1);
            }
        }

        for (ReservationData source : safeList(data.getReservations())) {
            Restaurant restaurant = findRestaurant(restaurants, source.getRestaurantName());
            RestaurantTime time = restaurant == null ? null
                    : findTime(
                            times,
                            restaurant.getRestaurantId(),
                            source.getMealType(),
                            source.getOpenTime(),
                            source.getCloseTime());

            if (restaurant == null || time == null || source.getReservationDate() == null) {
                result.setSkippedReservations(result.getSkippedReservations() + 1);
                continue;
            }

            Integer memberId = source.getMemberId();

            // 匯入到另一個資料庫時，會員 ID 可能不存在；改為訪客訂位並保留聯絡資料。
            if (memberId != null && !memberRepository.existsById(memberId)) {
                memberId = null;
                result.setMemberConvertedToGuest(result.getMemberConvertedToGuest() + 1);
            }

            if (memberId == null && (isBlank(source.getContactName()) || isBlank(source.getContactPhone()))) {
                result.setSkippedReservations(result.getSkippedReservations() + 1);
                continue;
            }

            Reservation reservation = new Reservation();
            reservation.setMemberId(memberId);
            reservation.setContactName(source.getContactName());
            reservation.setContactPhone(source.getContactPhone());
            reservation.setRestaurantId(restaurant.getRestaurantId());
            reservation.setTimeId(time.getTimeId());
            reservation.setReservationDate(source.getReservationDate());
            reservation.setPeopleCount(source.getPeopleCount());
            reservation.setStatus(isBlank(source.getStatus()) ? "已訂位" : source.getStatus());

            if (hasSameReservation(reservations, reservation)) {
                result.setSkippedReservations(result.getSkippedReservations() + 1);
                continue;
            }

            reservation = reservationService.save(reservation);
            reservations.add(reservation);
            result.setAddedReservations(result.getAddedReservations() + 1);
        }

        return ResponseEntity.ok(result);
    }

    private Restaurant findRestaurant(List<Restaurant> restaurants, String restaurantName) {
        String target = normalized(restaurantName);
        return restaurants.stream()
                .filter(item -> normalized(item.getRestaurantName()).equals(target))
                .findFirst()
                .orElse(null);
    }

    private RestaurantTime findTime(
            List<RestaurantTime> times,
            Integer restaurantId,
            String mealType,
            LocalTime openTime,
            LocalTime closeTime) {

        return times.stream()
                .filter(item -> Objects.equals(item.getRestaurantId(), restaurantId)
                        && normalized(item.getMealType()).equals(normalized(mealType))
                        && Objects.equals(item.getOpenTime(), openTime)
                        && Objects.equals(item.getCloseTime(), closeTime))
                .findFirst()
                .orElse(null);
    }

    private boolean hasSameReservation(List<Reservation> reservations, Reservation target) {
        return reservations.stream().anyMatch(item -> Objects.equals(item.getMemberId(), target.getMemberId())
                && normalized(item.getContactPhone()).equals(normalized(target.getContactPhone()))
                && Objects.equals(item.getRestaurantId(), target.getRestaurantId())
                && Objects.equals(item.getTimeId(), target.getTimeId())
                && Objects.equals(item.getReservationDate(), target.getReservationDate())
                && Objects.equals(item.getPeopleCount(), target.getPeopleCount()));
    }

    private boolean isWithinDateRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return true;
        }

        if (date == null) {
            return false;
        }

        return (startDate == null || !date.isBefore(startDate))
                && (endDate == null || !date.isAfter(endDate));
    }

    private String normalized(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private <T> List<T> safeList(List<T> values) {
        return values == null ? List.of() : values;
    }

    @Data
    @NoArgsConstructor
    public static class BackupData {
        private Integer version = 1;
        private List<RestaurantData> restaurants = new ArrayList<>();
        private List<ReservationData> reservations = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    public static class RestaurantData {
        private String restaurantName;
        private String address;
        private String phone;
        private Integer capacity;
        private String description;
        private List<TimeData> times = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    public static class TimeData {
        private String mealType;
        private LocalTime openTime;
        private LocalTime closeTime;
    }

    @Data
    @NoArgsConstructor
    public static class ReservationData {
        private Integer memberId;
        private String contactName;
        private String contactPhone;
        private String restaurantName;
        private String mealType;
        private LocalTime openTime;
        private LocalTime closeTime;
        private LocalDate reservationDate;
        private Integer peopleCount;
        private String status;
    }

    @Data
    @NoArgsConstructor
    public static class ImportResult {
        private int addedRestaurants;
        private int skippedRestaurants;
        private int addedTimes;
        private int skippedTimes;
        private int addedReservations;
        private int skippedReservations;
        private int memberConvertedToGuest;
    }
}
