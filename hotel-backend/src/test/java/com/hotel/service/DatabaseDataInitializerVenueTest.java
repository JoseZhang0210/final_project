package com.hotel.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tools.jackson.databind.ObjectMapper;

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

@ExtendWith(MockitoExtension.class)
class DatabaseDataInitializerVenueTest {

    @Mock private DepartmentRepository departmentRepository;
    @Mock private PermissionRepository permissionRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private ProfileRepository profileRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private EmployeePermissionRepository employeePermissionRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductRepository productRepository;
    @Mock private RoomTypeRepository roomTypeRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private RoomImageRepository roomImageRepository;
    @Mock private VenueRepository venueRepository;
    @Mock private RentalPaymentJpaRepository rentalPaymentJpaRepository;
    @Mock private RentalRepository rentalRepository;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private RestaurantTimeRepository restaurantTimeRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private CouponRepository couponRepository;
    @Mock private CustomerOrderRepository customerOrderRepository;
    @Mock private OrderDataExchangeService orderDataExchangeService;
    @Mock private BookingRepository bookingRepository;
    @Mock private BookingPaymentRepository bookingPaymentRepository;
    @Mock private RoomTaskRepository roomTaskRepository;
    @Mock private RoomService roomService;
    @Mock private RoomTypeService roomTypeService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private DatabaseDataInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new DatabaseDataInitializer(
                departmentRepository, permissionRepository, accountRepository,
                memberRepository, profileRepository, employeeRepository,
                employeePermissionRepository, categoryRepository, productRepository,
                roomTypeRepository, roomRepository, roomImageRepository,
                venueRepository, rentalPaymentJpaRepository, rentalRepository,
                restaurantRepository, restaurantTimeRepository, reservationRepository,
                couponRepository, customerOrderRepository, orderDataExchangeService,
                bookingRepository, bookingPaymentRepository, roomTaskRepository,
                objectMapper, roomService, roomTypeService
        );
    }

    /** 7. seed 缺 ID 1 時會自動補回 */
    @Test
    void seedVenues_missingVenue1_seedsOnlyVenue1() {
        when(venueRepository.existsById(1)).thenReturn(false);
        when(venueRepository.existsById(2)).thenReturn(true);
        when(venueRepository.existsById(3)).thenReturn(true);
        when(venueRepository.existsById(4)).thenReturn(true);

        initializer.seedVenues();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Venue>> captor = ArgumentCaptor.forClass(List.class);
        verify(venueRepository).saveAll(captor.capture());

        List<Venue> inserted = captor.getValue();
        assertEquals(1, inserted.size());
        assertEquals(1, inserted.get(0).getVenueId());
        assertEquals("國際宴會廳", inserted.get(0).getVenueName());
        assertEquals(200, inserted.get(0).getCapacity());
    }

    /** 8. seed 已有 ID 1 時不覆寫使用中資料 */
    @Test
    void seedVenues_allDefaultVenuesExist_doesNotSaveAnything() {
        when(venueRepository.existsById(1)).thenReturn(true);
        when(venueRepository.existsById(2)).thenReturn(true);
        when(venueRepository.existsById(3)).thenReturn(true);
        when(venueRepository.existsById(4)).thenReturn(true);

        initializer.seedVenues();

        verify(venueRepository, never()).saveAll(anyList());
    }

    /** 9. DB 有其他 Venue 時仍會補缺的預設 Venue (例如缺 ID 2，但 DB 有 ID 1, 3, 4, 5, 6) */
    @Test
    void seedVenues_customVenuesExistButDefaultMissing_seedsMissingDefaultVenue() {
        when(venueRepository.existsById(1)).thenReturn(true);
        when(venueRepository.existsById(2)).thenReturn(false); // 缺 2
        when(venueRepository.existsById(3)).thenReturn(true);
        when(venueRepository.existsById(4)).thenReturn(true);

        initializer.seedVenues();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Venue>> captor = ArgumentCaptor.forClass(List.class);
        verify(venueRepository).saveAll(captor.capture());

        List<Venue> inserted = captor.getValue();
        assertEquals(1, inserted.size());
        assertEquals(2, inserted.get(0).getVenueId());
        assertEquals("璀璨晶漾廳", inserted.get(0).getVenueName());
        assertEquals(150, inserted.get(0).getCapacity());
    }
}
