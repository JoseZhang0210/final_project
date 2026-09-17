package com.hotel.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDemographicsDTO {

    private Long totalMembers;
    private Long profileCount;
    private Double profileCompletionRate;

    private String topCity;
    private Double topCityPercentage;

    private String topAgeGroup;
    private Double topAgeGroupPercentage;

    private List<CityStatDTO> cityDistribution;
    private List<AgeGroupStatDTO> ageDistribution;
    private List<GenderStatDTO> genderDistribution;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CityStatDTO {
        private String city;
        private Long count;
        private Double percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AgeGroupStatDTO {
        private String groupName;
        private Integer minAge;
        private Integer maxAge;
        private Long count;
        private Double percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GenderStatDTO {
        private String gender;
        private String label;
        private Long count;
        private Double percentage;
    }
}

