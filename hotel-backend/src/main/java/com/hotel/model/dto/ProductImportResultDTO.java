package com.hotel.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductImportResultDTO {

    private int totalCount;

    private int createdCount;

    private int updatedCount;

    private int failedCount;

    private List<String> errors;
}
