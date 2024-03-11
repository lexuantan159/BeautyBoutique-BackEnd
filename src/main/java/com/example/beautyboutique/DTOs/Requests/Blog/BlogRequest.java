package com.example.beautyboutique.DTOs.Requests.Blog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BlogRequest {
    private Integer userId;
    private String title;
    private String content;
    private Integer likeCount;
    private String[] imageIds;
    private String[] imageUrls;
}
