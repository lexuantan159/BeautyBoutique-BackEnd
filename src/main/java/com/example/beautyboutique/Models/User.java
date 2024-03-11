package com.example.beautyboutique.Models;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "id", columnDefinition = "int", nullable = false)
    private Integer id;

    @Column(name = "userName", columnDefinition = "nvarchar(50)", nullable = false)
    private String userName;

    @Column(name = "password", columnDefinition = "varchar(255)", nullable = false)
    private Integer password;

    @Column(name = "address", columnDefinition = "nvarchar(255)")
    private String address;

    @Column(name = "isActive", columnDefinition = "int")
    private Integer isActive;

    @Column(name = "dateOfBirth", columnDefinition = "date")
    private Integer dateOfBirth;

    @Column(name = "imageId", columnDefinition = "varchar(255)")
    private String imageId;

    @Column(name = "imageURL", columnDefinition = "TEXT")
    private String imageURL;

    @Column(name = "facebookAccountId", columnDefinition = "int")
    private Integer facebookAccountId;

    @Column(name = "googleAccountId", columnDefinition = "int")
    private Integer googleAccountId;

    @ManyToOne
    @JoinColumn(name = "roleId")
    private Role role;


}
