package com.example.beautyboutique.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserImage {
    @Id
    @Column(name = "imageId", columnDefinition = "varchar(255)", nullable = false)
    private String id;

    @Column(name = "imageUrl", columnDefinition = "varchar(255)")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "userId", columnDefinition = "int")
    @JsonIgnore
    private User user;
}
