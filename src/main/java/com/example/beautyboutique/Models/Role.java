package com.example.beautyboutique.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @Column(name = "role_id", columnDefinition = "int", nullable = false)
    private Integer roleId;

    @Column(name = "role_name", columnDefinition = "nvarchar(50)")
    private String roleName;
    public static String ADMIN = "ADMIN";
    public static String USER = "USER";

}