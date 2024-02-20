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
public class ProductImage {
    @Id
    @Column(name = "imageId", columnDefinition = "varchar(255)", nullable = false)
    private String id;

    @Column(name = "imageUrl", columnDefinition = "varchar(255)")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "productId", columnDefinition = "int")
    @JsonIgnore
    private Product product;
}
