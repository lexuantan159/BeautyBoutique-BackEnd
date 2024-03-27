package com.example.beautyboutique.Services.Feedback;

import com.example.beautyboutique.Models.Feedback;

import java.util.List;

public interface FeedbackService {
    List<Feedback> getAllFeedback();
    Feedback getAFeedbackById(Integer id);
    Feedback createFeedback(Feedback feedback);
    Feedback updateFeedback(Integer id, Feedback updateFeedback);
    boolean deleteFeedback(Integer id);
    List<Feedback> getAllFeedbackByProductId(Integer productId);
    boolean isOrderedAndDelivered(Integer userId , Integer productId);
}
