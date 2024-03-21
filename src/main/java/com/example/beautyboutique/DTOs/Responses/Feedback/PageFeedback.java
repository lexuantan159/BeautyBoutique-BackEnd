package com.example.beautyboutique.DTOs.Responses.Feedback;

import com.example.beautyboutique.Models.Comment;
import com.example.beautyboutique.Models.Feedback;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PageFeedback {
    private String message;
    private List<Feedback> feedbackList;
}
