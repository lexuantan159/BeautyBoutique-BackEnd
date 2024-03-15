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
public class BlogImage {
    @Id
    @Column(name = "imageId", columnDefinition = "varchar(255)", nullable = false)
    private String id;

    @Column(name = "imageUrl", columnDefinition = "text")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "blogId", columnDefinition = "int")
    @JsonIgnore
    private BlogPost blogPost;
}
