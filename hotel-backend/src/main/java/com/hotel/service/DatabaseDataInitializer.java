package com.hotel.service;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import com.hotel.model.dto.ProductJsonDTO;
import com.hotel.model.entity.Account;
import com.hotel.model.entity.Booking;
import com.hotel.model.entity.BookingPayment;
import com.hotel.model.entity.Category;
import com.hotel.model.entity.Department;
import com.hotel.model.entity.Employee;
import com.hotel.model.entity.EmployeePermission;
import com.hotel.model.entity.Member;
import com.hotel.model.entity.Permission;
import com.hotel.model.entity.Product;
import com.hotel.model.entity.Profile;
import com.hotel.model.entity.Rental;
import com.hotel.model.entity.RentalPayment;
import com.hotel.model.entity.Reservation;
import com.hotel.model.entity.Restaurant;
import com.hotel.model.entity.RestaurantTime;
import com.hotel.model.entity.Room;
import com.hotel.model.entity.RoomImage;
import com.hotel.model.entity.RoomTask;
import com.hotel.model.entity.RoomType;
import com.hotel.model.entity.Venue;
import com.hotel.repository.AccountRepository;
import com.hotel.repository.BookingPaymentRepository;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.CategoryRepository;
import com.hotel.repository.CouponRepository;
import com.hotel.repository.CustomerOrderRepository;
import com.hotel.repository.DepartmentRepository;
import com.hotel.repository.EmployeePermissionRepository;
import com.hotel.repository.EmployeeRepository;
import com.hotel.repository.MemberRepository;
import com.hotel.repository.PermissionRepository;
import com.hotel.repository.ProductRepository;
import com.hotel.repository.ProfileRepository;
import com.hotel.repository.RentalPaymentJpaRepository;
import com.hotel.repository.RentalRepository;
import com.hotel.repository.ReservationRepository;
import com.hotel.repository.RestaurantRepository;
import com.hotel.repository.RestaurantTimeRepository;
import com.hotel.repository.RoomImageRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.RoomTaskRepository;
import com.hotel.repository.RoomTypeRepository;
import com.hotel.repository.VenueRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 全系統資料庫種子資料自動初始化器 (Universal Database Data Initializer)。
 * 
 * 在 Spring Boot 啟動且 Hibernate 完成 DDL 更新 (spring.jpa.hibernate.ddl-auto=update) 後執行。
 * 檢查各模組資料表是否為空，若為空則依序讀取 resources/data/seed/ 下的模組化 JSON 檔案自動初始化資料。
 * 每個步驟具備冪等性 (Idempotent)，若資料表已有資料則自動跳過，避免重複插入。
 */
@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DatabaseDataInitializer implements ApplicationRunner {

    private final DepartmentRepository departmentRepository;
    private final PermissionRepository permissionRepository;
    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeePermissionRepository employeePermissionRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final RoomImageRepository roomImageRepository;
    private final VenueRepository venueRepository;
    private final RentalPaymentJpaRepository rentalPaymentJpaRepository;
    private final RentalRepository rentalRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantTimeRepository restaurantTimeRepository;
    private final ReservationRepository reservationRepository;
    private final CouponRepository couponRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final OrderDataExchangeService orderDataExchangeService;
    private final BookingRepository bookingRepository;
    private final BookingPaymentRepository bookingPaymentRepository;
    private final RoomTaskRepository roomTaskRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) {
        log.info("【資料庫初始化器】開始檢查並載入各模組 JSON 種子資料...");

        try {
            seedDepartments();
            seedPermissions();
            seedAccounts();
            seedMembers();
            seedProfiles();
            seedEmployees();
            seedEmployeePermissions();
            seedCategories();
            seedProducts();
            seedRoomTypes();
            seedRooms();
            seedRoomImages();
            seedVenues();
            seedRentalPayments();
            seedRentals();
            seedRestaurants();
            seedRestaurantTimes();
            seedReservations();
            seedOrders();
            seedBookings();
            seedRoomTasks();

            log.info("【資料庫初始化器】全系統資料庫初始化檢查與作業完成！");
        } catch (Exception e) {
            log.error("【資料庫初始化器】初始化過程中發生未預期錯誤: {}", e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------------------
    // 1. 部門 (Department)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedDepartments() {
        if (departmentRepository.count() == 0) {
            List<Department> list = loadListFromClasspath("data/seed/01_departments.json", Department.class);
            for (Department d : list) {
                d.setDepartmentId(null);
                departmentRepository.save(d);
            }
            log.info("【部門模組】已成功初始化 {} 筆部門資料。", list.size());
        }
    }

    // -------------------------------------------------------------------------
    // 2. 權限 (Permission)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedPermissions() {
        if (permissionRepository.count() == 0) {
            List<Permission> list = loadListFromClasspath("data/seed/02_permissions.json", Permission.class);
            for (Permission p : list) {
                p.setPermissionId(null);
                permissionRepository.save(p);
            }
            log.info("【權限模組】已成功初始化 {} 筆權限資料。", list.size());
        }
    }

    // -------------------------------------------------------------------------
    // 3. 帳號 (Account)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedAccounts() {
        if (accountRepository.count() == 0) {
            List<Account> list = loadListFromClasspath("data/seed/03_accounts.json", Account.class);
            for (Account acc : list) {
                acc.setAccountId(null);
                accountRepository.save(acc);
            }
            log.info("【帳號模組】已成功初始化 {} 筆帳號資料。", list.size());
        }
    }

    // -------------------------------------------------------------------------
    // 4. 會員 (Member)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedMembers() {
        if (memberRepository.count() == 0) {
            List<Member> list = loadListFromClasspath("data/seed/04_members.json", Member.class);
            for (Member m : list) {
                m.setMemberId(null);
                memberRepository.save(m);
            }
            log.info("【會員模組】已成功初始化 {} 筆會員資料。", list.size());
        }
    }

    // -------------------------------------------------------------------------
    // 5. 個人檔案 (Profile)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedProfiles() {
        if (profileRepository.count() == 0) {
            List<Profile> list = loadListFromClasspath("data/seed/05_profiles.json", Profile.class);
            for (Profile p : list) {
                p.setProfileId(null);
                profileRepository.save(p);
            }
            log.info("【檔案模組】已成功初始化 {} 筆個人檔案資料。", list.size());
        }
    }

    // -------------------------------------------------------------------------
    // 6. 員工 (Employee)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedEmployees() {
        if (employeeRepository.count() == 0) {
            List<Employee> list = loadListFromClasspath("data/seed/06_employees.json", Employee.class);
            for (Employee emp : list) {
                emp.setEmployeeId(null);
                employeeRepository.save(emp);
            }
            log.info("【員工模組】已成功初始化 {} 筆員工資料。", list.size());
        }
    }

    // -------------------------------------------------------------------------
    // 7. 員工權限 (EmployeePermission)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedEmployeePermissions() {
        if (employeePermissionRepository.count() == 0) {
            List<EmployeePermission> list = loadListFromClasspath("data/seed/07_employee_permissions.json", EmployeePermission.class);
            employeePermissionRepository.saveAll(list);
            log.info("【員工權限】已成功初始化 {} 筆員工權限對應資料。", list.size());
        }
    }

    // -------------------------------------------------------------------------
    // 8. 商品分類 (Category)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedCategories() {
        if (categoryRepository.count() == 0) {
            List<Category> list = loadListFromClasspath("data/seed/08_categories.json", Category.class);
            for (Category c : list) {
                c.setCategoryId(null);
                categoryRepository.save(c);
            }
            log.info("【商品分類】已成功初始化 {} 筆分類資料。", list.size());
        }
    }

    // -------------------------------------------------------------------------
    // 9. 商品 (Product)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedProducts() {
        if (productRepository.count() == 0) {
            List<ProductJsonDTO> list = loadListFromClasspath("data/seed/09_products.json", ProductJsonDTO.class);
            Map<Integer, Category> categoryMap = categoryRepository.findAll().stream()
                    .collect(Collectors.toMap(Category::getCategoryId, c -> c));

            int count = 0;
            for (ProductJsonDTO dto : list) {
                Category cat = categoryMap.get(dto.getCategoryId());
                if (cat == null) {
                    continue;
                }
                Product product = new Product();
                product.setProductName(dto.getProductName());
                product.setCategory(cat);
                product.setDescription(dto.getDescription());
                product.setPrice(dto.getPrice() != null ? dto.getPrice() : 0);
                product.setStock(dto.getStock() != null ? dto.getStock() : 0);
                product.setImageUrl(dto.getImageUrl());
                product.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
                productRepository.save(product);
                count++;
            }
            log.info("【商城商品】已成功初始化 {} 筆商品資料。", count);
        }
    }

    // -------------------------------------------------------------------------
    // 10. 房型 (RoomType)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedRoomTypes() {
        if (roomTypeRepository.count() == 0) {
            List<RoomType> list = loadListFromClasspath("data/seed/10_room_types.json", RoomType.class);
            for (RoomType rt : list) {
                rt.setRoomTypeId(null);
                roomTypeRepository.save(rt);
            }
            log.info("【房型模組】已成功初始化 {} 筆房型資料。", list.size());
        }
    }

    // -------------------------------------------------------------------------
    // 11. 房間 (Room)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedRooms() {
        if (roomRepository.count() == 0) {
            List<Room> list = loadListFromClasspath("data/seed/11_rooms.json", Room.class);
            for (Room r : list) {
                r.setRoomId(null);
                roomRepository.save(r);
            }
            log.info("【房間模組】已成功初始化 {} 筆房間資料。", list.size());
        }
    }

    // -------------------------------------------------------------------------
    // 12. 房型圖片 (RoomImage)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedRoomImages() {
        if (roomImageRepository.count() == 0) {
            List<RoomImage> list = loadListFromClasspath("data/seed/12_room_images.json", RoomImage.class);
            for (RoomImage img : list) {
                img.setImageId(null);
                roomImageRepository.save(img);
            }
            log.info("【房型圖片】已成功初始化 {} 筆圖片資料。", list.size());
        }
    }

    // -------------------------------------------------------------------------
    // 13. 場地 (Venue - 非自增主鍵)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedVenues() {
        if (venueRepository.count() == 0) {
            List<Venue> list = loadListFromClasspath("data/seed/13_venues.json", Venue.class);
            venueRepository.saveAll(list);
            log.info("【場地模組】已成功初始化 {} 筆場地資料。", list.size());
        }
    }

    // -------------------------------------------------------------------------
    // 14. 場地租借付款 (RentalPayment)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedRentalPayments() {
        if (rentalPaymentJpaRepository.count() == 0) {
            List<RentalPayment> list = loadListFromClasspath("data/seed/14_rental_payments.json", RentalPayment.class);
            for (RentalPayment rp : list) {
                rp.setPaymentId(null);
                rentalPaymentJpaRepository.save(rp);
            }
            log.info("【場地付款】已成功初始化 {} 筆租借付款紀錄。", list.size());
        }
    }

    // -------------------------------------------------------------------------
    // 15. 場地租借預訂 (Rental - 非自增主鍵)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedRentals() {
        if (rentalRepository.count() == 0) {
            List<Rental> list = loadListFromClasspath("data/seed/15_rentals.json", Rental.class);
            rentalRepository.saveAll(list);
            log.info("【場地租借】已成功初始化 {} 筆場地預訂資料。", list.size());
        }
    }

    // -------------------------------------------------------------------------
    // 16. 餐廳 (Restaurant)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedRestaurants() {
        if (restaurantRepository.count() == 0) {
            List<Restaurant> list = loadListFromClasspath("data/seed/16_restaurants.json", Restaurant.class);
            for (Restaurant r : list) {
                r.setRestaurantId(null);
                restaurantRepository.save(r);
            }
            log.info("【餐廳模組】已成功初始化 {} 筆餐廳資料。", list.size());
        }
    }

    // -------------------------------------------------------------------------
    // 17. 餐廳時段 (RestaurantTime)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedRestaurantTimes() {
        if (restaurantTimeRepository.count() == 0) {
            List<RestaurantTime> list = loadListFromClasspath("data/seed/17_restaurant_times.json", RestaurantTime.class);
            for (RestaurantTime rt : list) {
                rt.setTimeId(null);
                restaurantTimeRepository.save(rt);
            }
            log.info("【餐廳時段】已成功初始化 {} 筆餐廳供餐時段資料。", list.size());
        }
    }

    // -------------------------------------------------------------------------
    // 18. 餐廳訂位 (Reservation)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedReservations() {
        if (reservationRepository.count() == 0) {
            List<Reservation> list = loadListFromClasspath("data/seed/18_reservations.json", Reservation.class);
            for (Reservation res : list) {
                res.setReservationId(null);
                reservationRepository.save(res);
            }
            log.info("【餐廳訂位】已成功初始化 {} 筆訂位資料。", list.size());
        }
    }

    // -------------------------------------------------------------------------
    // 19. 訂單系統 (Coupon, Payment, CustomerOrder, OrderItem)
    // -------------------------------------------------------------------------
    public void seedOrders() {
        long couponCount = couponRepository.count();
        long orderCount = customerOrderRepository.count();

        if (couponCount == 0 && orderCount == 0) {
            log.info("【訂單系統】偵測到訂單資料表為空，啟動種子資料自動初始化...");
            int imported = orderDataExchangeService.importFromClasspathSeed();
            log.info("【訂單系統】已成功初始化 {} 筆訂單及相關關聯資料。", imported);
        }
    }

    // -------------------------------------------------------------------------
    // 20. 訂房與付款 (Booking & BookingPayment)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedBookings() {
        if (bookingRepository.count() == 0) {
            try {
                ClassPathResource resource = new ClassPathResource("data/seed/20_bookings.json");
                if (resource.exists()) {
                    try (InputStream is = resource.getInputStream()) {
                        BookingSeedDTO dto = objectMapper.readValue(is, BookingSeedDTO.class);
                        if (dto.getBookings() != null) {
                            for (Booking b : dto.getBookings()) {
                                b.setBookingId(null);
                                bookingRepository.save(b);
                            }
                            log.info("【訂房模組】已成功初始化 {} 筆訂房資料。", dto.getBookings().size());
                        }
                        if (dto.getPayments() != null && bookingPaymentRepository.count() == 0) {
                            for (BookingPayment bp : dto.getPayments()) {
                                bp.setPaymentId(null);
                                bookingPaymentRepository.save(bp);
                            }
                            log.info("【訂房付款】已成功初始化 {} 筆訂房付款資料。", dto.getPayments().size());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("【訂房模組】匯入種子資料失敗: {}", e.getMessage(), e);
            }
        }
    }

    // -------------------------------------------------------------------------
    // 21. 房務任務 (RoomTask)
    // -------------------------------------------------------------------------
    @Transactional
    public void seedRoomTasks() {
        if (roomTaskRepository.count() == 0) {
            List<RoomTask> list = loadListFromClasspath("data/seed/21_room_tasks.json", RoomTask.class);
            for (RoomTask rt : list) {
                rt.setTaskId(null);
                roomTaskRepository.save(rt);
            }
            log.info("【房務任務】已成功初始化 {} 筆房務清潔與維修任務資料。", list.size());
        }
    }

    // -------------------------------------------------------------------------
    // 通用輔助方法：由 ClassPath 讀取 List<T>
    // -------------------------------------------------------------------------
    private <T> List<T> loadListFromClasspath(String path, Class<T> clazz) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            if (!resource.exists()) {
                log.warn("種子資料檔案 {} 不存在，跳過注入。", path);
                return Collections.emptyList();
            }
            try (InputStream is = resource.getInputStream()) {
                JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
                return objectMapper.readValue(is, type);
            }
        } catch (Exception e) {
            log.error("讀取種子資料檔案 {} 失敗: {}", path, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Data
    private static class BookingSeedDTO {
        private List<Booking> bookings;
        private List<BookingPayment> payments;
    }
}

