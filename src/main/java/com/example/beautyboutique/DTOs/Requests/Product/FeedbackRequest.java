package com.example.beautyboutique.DTOs.Requests.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FeedbackRequest {
    private String content;
    private Integer rating;
    private Integer productId;
    private Integer userId;
}
