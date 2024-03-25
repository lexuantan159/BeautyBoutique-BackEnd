package com.example.beautyboutique.DTOs.Requests.Blog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentBlogRequest {
    private Integer blogId;
    private String content;
}
