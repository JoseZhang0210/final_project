package com.hotel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permission")
@Data
@NoArgsConstructor
public class Permission {

    @Id
    @Column(name = "permission_id")
    private Integer id;

    @Column(name = "permission_code", nullable = false, length = 50)
    private String permissionCode;

    @Column(name = "permission_name", nullable = false, length = 50)
    private String permissionName;

    public Permission(Integer id, String permissionCode, String permissionName) {
        this.id = id;
        this.permissionCode = permissionCode;
        this.permissionName = permissionName;
    }
}
