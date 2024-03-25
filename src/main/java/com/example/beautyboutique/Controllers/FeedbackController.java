package com.example.beautyboutique.Controllers;
import com.example.beautyboutique.DTOs.Requests.Product.FeedbackRequest;
import com.example.beautyboutique.DTOs.Responses.Feedback.PageFeedback;
import com.example.beautyboutique.Models.*;
import com.example.beautyboutique.Services.Feedback.FeedbackServices;
import com.example.beautyboutique.Services.JWTServiceImpl;
import com.example.beautyboutique.Services.Product.ProductService;
import com.example.beautyboutique.Services.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/product/feedback")
public class FeedbackController {
    @Autowired
    FeedbackServices feedbackServices;
    @Autowired
    UserService userService;
    @Autowired
    ProductService productService;
    @Autowired
    JWTServiceImpl jwtService;

    @GetMapping(value = "/get-feedback-product")
    public ResponseEntity<?> getFeebackByProduct(@RequestParam(value = "productId") Integer productId) {
        try {
            List<Feedback> feedbackList = feedbackServices.getAllFeedbackByProductId(productId);
            return new ResponseEntity<>(new PageFeedback("Get All feedback Successfully", feedbackList), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Retrieving information failed", HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping(value = "/create-feedback", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE
    }, produces = {MediaType.APPLICATION_JSON_VALUE
    })
    @ResponseBody
    ResponseEntity<?> creteFeedback(FeedbackRequest request, HttpServletRequest requestToken) {
        try {
            Integer userId = jwtService.getUserIdByToken(requestToken);
            Optional<User> userFeedback = userService.getUserById(userId);

            Integer productId = request.getProductId();
            Product productFeedback = productService.findById(productId);

            Feedback feedback = new Feedback();
            feedback.setContent(request.getContent());
            feedback.setRating(request.getRating());
            feedback.setProduct(productFeedback);
            feedback.setUser(userFeedback.get());

            Feedback createFeedback = feedbackServices.createFeedback(feedback);
            if (createFeedback != null) {
                return new ResponseEntity<>("Created a feedback successfully", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Failed to create feedback", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Created a failed feedback", HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping(value = "/delete-feedback")
    public ResponseEntity<?> deleteFeedback(@RequestParam(value = "id") Integer id,HttpServletRequest requestToken) {
        try {
            Integer userId = jwtService.getUserIdByToken(requestToken);
            if (id == null || id <= 0 || userId == null || userId <= 0) {
                return ResponseEntity.badRequest().body("Invalid comment ID or user ID");
            }

            Feedback feedback = feedbackServices.getAFeedbackById(id);
            if (feedback == null) {
                return ResponseEntity.badRequest().body("Feedback not found");
            }

            int ownerId = feedback.getUser().getId();
            if (userId.equals(ownerId)) {
                boolean isDelete = feedbackServices.deleteFeedback(id);
                if (isDelete) {
                    return new ResponseEntity<>("Delete feedback successfully!", HttpStatus.OK);
                } else {
                    return ResponseEntity.badRequest().body("Delete feedback failed");
                }
            } else {
                return ResponseEntity.badRequest().body("You are not authorized to delete this feedback");
            }
        } catch (Exception e) {
            // Log exception
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Delete feedback failed");
        }
    }


    @PutMapping(value = "/update-feedback", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE
    }, produces = {MediaType.APPLICATION_JSON_VALUE
    })
    public @ResponseBody ResponseEntity<?> updateFeedback(Feedback content,
                                                         @RequestParam(value = "id") Integer id,HttpServletRequest requestToken) {
        try {
            Integer userId = jwtService.getUserIdByToken(requestToken);
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("Invalid comment ID");
            }
            Feedback feedback = feedbackServices.getAFeedbackById(id);
            int ownerId = feedback.getUser().getId();
            if (userId == ownerId) {
                Feedback updatedCommentResult = feedbackServices.updateFeedback(id, content);
                if (updatedCommentResult != null) {
                    return new ResponseEntity<>("Updated feedback successfully", HttpStatus.OK);
                }
                return new ResponseEntity<>("Failed to update feedback", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return new ResponseEntity<>("Internal server!", HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
