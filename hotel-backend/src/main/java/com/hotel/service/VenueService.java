package com.hotel.service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.entity.Venue;
import com.hotel.repository.RentalRepository;
import com.hotel.repository.VenueRepository;

@Service
@Transactional
public class VenueService {

    private static final List<HallDefinition> FIXED_HALLS =
            List.of(
                    new HallDefinition(1, "A廳", 50, 5000),
                    new HallDefinition(2, "B廳", 100, 10000),
                    new HallDefinition(3, "C廳", 200, 20000),
                    new HallDefinition(4, "D廳", 300, 30000));

    private final VenueRepository venueRepository;
    private final RentalRepository rentalRepository;

    public VenueService(
            VenueRepository venueRepository,
            RentalRepository rentalRepository) {

        this.venueRepository = venueRepository;
        this.rentalRepository = rentalRepository;
    }

    public Venue save(Venue venue) {

        if (venue == null || venue.getVenueId() == null) {
            throw new IllegalArgumentException(
                    "場地 ID 不可空白");
        }

        return updateStatus(
                venue.getVenueId(),
                venue.getVenueStatus());
    }

    public List<Venue> findAll() {

        ensureFixedHalls();

        return venueRepository
                .findAllById(List.of(1, 2, 3, 4))
                .stream()
                .sorted(Comparator.comparing(Venue::getVenueId))
                .toList();
    }

    public Optional<Venue> findById(Integer id) {

        HallDefinition definition = findDefinition(id);

        if (definition == null) {
            return Optional.empty();
        }

        return Optional.of(
                ensureFixedHall(definition));
    }

    public boolean existsById(Integer id) {
        return findDefinition(id) != null;
    }

    public boolean isFixedId(Integer id) {
        return findDefinition(id) != null;
    }

    public Venue updateStatus(
            Integer id,
            String venueStatus) {

        HallDefinition definition = findDefinition(id);

        if (definition == null) {
            throw new IllegalArgumentException(
                    "場地 ID 只能是 1、2、3、4");
        }

        Venue venue = venueRepository
                .findById(id)
                .orElseGet(Venue::new);

        applyDefinition(venue, definition);

        venue.setVenueStatus(
                normalizeStatus(venueStatus));

        return venueRepository.save(venue);
    }

    public boolean deleteById(Integer id) {

        if (!isFixedId(id)) {
            return false;
        }

        if (rentalRepository.existsByVenueId(id)) {
            throw new IllegalStateException(
                    "固定場地已有租借紀錄，且 A～D 四廳不可刪除");
        }

        throw new IllegalStateException(
                "A～D 四個固定場地不可刪除，只能修改狀態");
    }

    public void ensureFixedHalls() {

        for (HallDefinition definition : FIXED_HALLS) {
            ensureFixedHall(definition);
        }

        venueRepository.flush();
    }

    private Venue ensureFixedHall(
            HallDefinition definition) {

        Venue venue = venueRepository
                .findById(definition.id())
                .orElseGet(Venue::new);

        String status =
                normalizeExistingStatus(
                        venue.getVenueStatus());

        applyDefinition(venue, definition);
        venue.setVenueStatus(status);

        return venueRepository.save(venue);
    }

    private void applyDefinition(
            Venue venue,
            HallDefinition definition) {

        venue.setVenueId(definition.id());
        venue.setVenueName(definition.name());
        venue.setCapacity(definition.capacity());
        venue.setPricePerDay(definition.pricePerDay());
    }

    private HallDefinition findDefinition(Integer id) {

        if (id == null) {
            return null;
        }

        return FIXED_HALLS.stream()
                .filter(definition ->
                        definition.id() == id)
                .findFirst()
                .orElse(null);
    }

    private String normalizeExistingStatus(
            String status) {

        if (status == null || status.isBlank()) {
            return "AVAILABLE";
        }

        String value =
                status.trim().toUpperCase(Locale.ROOT);

        return switch (value) {
            case "AVAILABLE", "可預約" ->
                    "AVAILABLE";
            case "MAINTENANCE", "維護中", "維修中" ->
                    "MAINTENANCE";
            case "DISABLED", "停用" ->
                    "DISABLED";
            default ->
                    "AVAILABLE";
        };
    }

    private String normalizeStatus(
            String status) {

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "場地狀態不可空白");
        }

        String value =
                status.trim().toUpperCase(Locale.ROOT);

        return switch (value) {
            case "AVAILABLE", "可預約" ->
                    "AVAILABLE";
            case "MAINTENANCE", "維護中", "維修中" ->
                    "MAINTENANCE";
            case "DISABLED", "停用" ->
                    "DISABLED";
            default ->
                    throw new IllegalArgumentException(
                            "場地狀態只能是 AVAILABLE、MAINTENANCE 或 DISABLED");
        };
    }

    private record HallDefinition(
            int id,
            String name,
            int capacity,
            int pricePerDay) {
    }
}