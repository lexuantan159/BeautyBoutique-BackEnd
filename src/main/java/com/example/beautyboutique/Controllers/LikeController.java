package com.example.beautyboutique.Controllers;
import com.example.beautyboutique.DTOs.Requests.Blog.BlogRequest;
import com.example.beautyboutique.DTOs.Requests.Blog.LikeRequest;
import com.example.beautyboutique.Models.BlogPost;
import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Services.Blog.BlogServices;
import com.example.beautyboutique.Services.JWTServiceImpl;
import com.example.beautyboutique.Services.Like.LikeService;
import com.example.beautyboutique.Services.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/blog/like")
public class LikeController {
    @Autowired
    LikeService likeService;
    @Autowired
    JWTServiceImpl jwtService;
    @Autowired
    BlogServices blogServices;
    @Autowired
    UserService userService;

    @GetMapping(value = "/is-like")
    public ResponseEntity<?> userIsLike(@RequestParam(value = "blogId") Integer blogId , HttpServletRequest requestToken) {
        try {
            Integer userId = jwtService.getUserIdByToken(requestToken);
            boolean isLike = likeService.hasUserLikedBlog(userId,blogId);
            return ResponseEntity.ok(isLike);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(false);
        }
    }
    @PostMapping(value = "/add-like", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE
    }, produces = {MediaType.APPLICATION_JSON_VALUE
    })
    public @ResponseBody ResponseEntity<?> addLike(@RequestBody LikeRequest likeRequest , HttpServletRequest requestToken) {
        try {
            Integer userId = jwtService.getUserIdByToken(requestToken);
            Optional<User> userBlog = userService.getUserById(userId);
            BlogPost blogPost = blogServices.getABlogById(likeRequest.getBlogId());
            if (userBlog.isPresent() && blogPost != null) {
                likeService.addLike(userBlog.get(), blogPost);
                return new ResponseEntity<>("Like Successfully",HttpStatus.OK);
            } else {
                return ResponseEntity.badRequest().body("User or blog not found");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Adding like failed");
        }
    }
    @DeleteMapping("/delete-like")
    public ResponseEntity<?> removeLike(@RequestParam(value = "blogId") Integer blogId, HttpServletRequest requestToken) {
        try {
            Integer userId = jwtService.getUserIdByToken(requestToken);
            Optional<User> userBlog = userService.getUserById(userId);
            BlogPost blogPost = blogServices.getABlogById(blogId);
            if (userBlog.isPresent() && blogPost != null) {
                likeService.removeLike(userBlog.get(), blogPost);
                return new ResponseEntity<>("DisLike Successffuly",HttpStatus.OK);
            } else {
                return ResponseEntity.badRequest().body("User or blog not found");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Removing like failed");
        }
    }
    @GetMapping("/count")
    public ResponseEntity<?> getLikeCount(@RequestParam(value = "blogId") Integer blogId) {
        try {
            int likeCount = likeService.getLikesCount(blogId);
            return new ResponseEntity<>(likeCount, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
