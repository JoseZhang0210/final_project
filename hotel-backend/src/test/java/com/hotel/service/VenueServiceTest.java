package com.hotel.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotel.model.entity.Venue;
import com.hotel.repository.RentalRepository;
import com.hotel.repository.VenueRepository;

/**
 * 場地更新的回歸測試。
 */
@ExtendWith(MockitoExtension.class)
class VenueServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private RentalRepository rentalRepository;

    private VenueService venueService;

    /** 為每個測試建立乾淨的場地服務。 */
    @BeforeEach
    void setUp() {
        venueService = new VenueService(
                venueRepository,
                rentalRepository); // 每個測試使用乾淨的場地服務。
    }

    /** 驗證本機上傳路徑可正常保存。 */
    @Test
    void saveAcceptsLocalUploadPathWithoutNullSchemeFailure() {
        Venue venue = venue(
                1,
                50,
                "/uploads/venues/venue-1.jpg");

        when(venueRepository.save(venue))
                .thenReturn(venue); // 模擬新增流程寫入場地。

        Venue saved = assertDoesNotThrow(
                () -> venueService.save(venue)); // 相對路徑不應再觸發空 scheme 例外。

        assertEquals(
                "/uploads/venues/venue-1.jpg",
                saved.getImageUrl());
        verify(venueRepository).save(venue);
    }

    /** 驗證更新僅複製允許修改的場地欄位。 */
    @Test
    void updateExistingCopiesAllowedFieldsToManagedVenue() {
        Venue managed = venue(
                1,
                50,
                "/uploads/venues/original.jpg");
        Venue incoming = venue(
                1,
                49,
                "/uploads/venues/replacement.jpg");

        when(venueRepository.findById(1))
                .thenReturn(Optional.of(managed)); // 回傳目前交易中的既有場地。

        Venue updated = venueService.updateExisting(
                incoming); // 只更新允許修改的 Venue 欄位。

        assertEquals(1, updated.getVenueId());
        assertEquals(49, updated.getCapacity());
        assertEquals(
                "/uploads/venues/replacement.jpg",
                updated.getImageUrl());
        verify(venueRepository).findById(1);
    }

    /** 驗證固定場地拒絕超過核定上限的容量。 */
    @Test
    void fixedVenueCapacitiesRejectValuesAboveConfiguredMaximum() {
        int[][] boundaryCases = {
                {1, 50},
                {2, 100},
                {3, 200},
                {4, 300}
        }; // 每個固定場地都以自己的核定容量作為硬上限。

        for (int[] boundaryCase : boundaryCases) {
            int venueId = boundaryCase[0];
            int capacityLimit = boundaryCase[1];
            Venue incoming = venue(
                    venueId,
                    capacityLimit + 1,
                    "/images/venues/venue-" + venueId + ".jpg");

            IllegalArgumentException exception =
                    assertThrows(
                            IllegalArgumentException.class,
                            () -> venueService.updateExisting(incoming)); // 超過一人也必須在寫入前拒絕。

            assertEquals(
                    "場地「宴會廳甲」容量不可超過 "
                            + capacityLimit
                            + " 人",
                    exception.getMessage());
        }
    }

    /**
     * 建立測試用場地資料。
     */
    private Venue venue(
            int venueId,
            int capacity,
            String imageUrl) {

        Venue venue = new Venue();
        venue.setVenueId(venueId);
        venue.setVenueName("宴會廳甲");
        venue.setCapacity(capacity);
        venue.setPricePerDay(5000);
        venue.setVenueStatus("AVAILABLE");
        venue.setImageUrl(imageUrl);
        return venue;
    }
}
