package com.hotel.dto;

/**
 * 場地 (Venue) DTO
 */
public class VenueDTO {

    private Integer venueId;
    private String venueName;
    private String description;
    private String location;
    private Integer capacity;
    private String status;

    public VenueDTO() {
    }

    public VenueDTO(Integer venueId, String venueName, String description, String location, Integer capacity,
            String status) {
        this.venueId = venueId;
        this.venueName = venueName;
        this.description = description;
        this.location = location;
        this.capacity = capacity;
        this.status = status;
    }

    // Getter & Setter
    public Integer getVenueId() {
        return venueId;
    }

    public void setVenueId(Integer venueId) {
        this.venueId = venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
