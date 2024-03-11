package com.example.beautyboutique.DTOs.Responses.Comment;

import com.example.beautyboutique.Models.BlogPost;
import com.example.beautyboutique.Models.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PageComment {
    private String message;
    private List<Comment> commentList;
}
