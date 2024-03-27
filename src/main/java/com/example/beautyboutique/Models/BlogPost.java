package com.example.beautyboutique.Models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "likeCount", columnDefinition = "int")
    private Integer likeCount;

    @OneToMany(mappedBy = "blogPost", fetch = FetchType.EAGER)
    private List<BlogImage> images;
    @OneToMany(mappedBy = "blogPost", fetch = FetchType.EAGER)
    private List<Comment> comments;
    @OneToMany(mappedBy = "blogPost", fetch = FetchType.EAGER)
    private List<LikeEntity> likeEntities;

    @Column(name = "createDate", columnDefinition = "DATETIME", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createDate;

}
