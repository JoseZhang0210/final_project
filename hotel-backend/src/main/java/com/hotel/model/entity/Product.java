package com.hotel.model.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Product")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Product_ID")
    private Integer productId;

    @Column(name = "Product_Name", nullable = false)
    private String productName;

    @ManyToOne
    @JoinColumn(name = "Category_ID", nullable = false)
    private Category category;

    @Column(name = "Description")
    private String description;

    @Column(name = "Price")
    private BigDecimal price;

    @Column(name = "Stock")
    private Integer stock;

    @Column(name = "ImageURL")
    private String imageUrl;

    @Column(name = "Status")
    private String status;
}