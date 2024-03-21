package com.example.beautyboutique.Services.Feedback;
import com.example.beautyboutique.Models.Feedback;
import com.example.beautyboutique.Repositories.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackServices implements FeedbackService {
    @Autowired
    FeedbackRepository feedbackRepository;
    @Override
    public List<Feedback> getAllFeedback() {
        try {
            List<Feedback> feedbacktList = feedbackRepository.findAll();
            return feedbacktList;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Feedback getAFeedbackById(Integer id) {
        try {
            Optional<Feedback> optionalFeedback = feedbackRepository.findById(id);
            if (optionalFeedback.isPresent()) {
                return optionalFeedback.get();
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Feedback();
        }
    }

    @Override
    public Feedback createFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    @Override
    public Feedback updateFeedback(Integer id, Feedback updateFeedback) {
        try {
            Optional<Feedback> optionalFeedback = feedbackRepository.findById(id);
            if (optionalFeedback.isPresent()) {
                Feedback existingFeedback = optionalFeedback.get();

                if (updateFeedback.getContent() != null) {
                    existingFeedback.setContent(updateFeedback.getContent());
                }

                return feedbackRepository.save(existingFeedback);
            } else {
                System.out.println("Feedback not found with id: " + id);
            }
        } catch (Exception e) {
            System.out.println("An error occurred while updating the Feedback: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean deleteFeedback(Integer id) {
        try {
            Optional<Feedback> optionalFeedback = feedbackRepository.findById(id);
            if (optionalFeedback.isPresent()) {
                feedbackRepository.deleteById(id);
                System.out.println("Feedback with id " + id + " have been deleted.");
                return true;
            } else {
                System.out.println("FeedBack not found with id: " + id);
                return false;
            }
        } catch (Exception e) {
            System.out.println("An error occurred while deleting the Feedback: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Feedback> getAllFeedbackByProductId(Integer productId) {
        try {
            Sort sortByCreateDate = Sort.by(Sort.Direction.DESC, "createdAt");
            List<Feedback> feedbackList = feedbackRepository.findFeedbacksByProduct_Id(productId,sortByCreateDate);
            return feedbackList;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }
}
