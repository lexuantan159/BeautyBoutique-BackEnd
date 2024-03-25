package com.example.beautyboutique.Controllers;

import com.example.beautyboutique.DTOs.Requests.Blog.CommentBlogRequest;
import com.example.beautyboutique.DTOs.Responses.Blog.PageBlog;
import com.example.beautyboutique.DTOs.Responses.Comment.PageComment;
import com.example.beautyboutique.Models.BlogImage;
import com.example.beautyboutique.Models.BlogPost;
import com.example.beautyboutique.Models.Comment;
import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Services.Blog.BlogServices;
import com.example.beautyboutique.Services.Comment.CommentServices;
import com.example.beautyboutique.Services.JWTServiceImpl;
import com.example.beautyboutique.Services.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/blog/comment")
@CrossOrigin(origins = "http://localhost:3000")
public class CommentBlogControllers {
    @Autowired
    CommentServices commentServices;
    @Autowired
    UserService userService;

    @Autowired
    BlogServices blogService;

    @Autowired
    JWTServiceImpl jwtService;

    @PostMapping(value = "/create-comment", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE
    }, produces = {MediaType.APPLICATION_JSON_VALUE
    })
    @ResponseBody
    ResponseEntity<?> createComment(CommentBlogRequest request , HttpServletRequest requestToken) {
        try {
           Integer userId = jwtService.getUserIdByToken(requestToken);
           Optional<User> userComment = userService.getUserById(userId);

           Integer blogId = request.getBlogId();
           BlogPost blogComment = blogService.getABlogById(blogId);

           Comment comment = new Comment();
           comment.setBlogPost(blogComment);
           comment.setContent(request.getContent());
           comment.setUser(userComment.get());

            Comment createdComment = commentServices.createComment(comment);
            if (createdComment != null) {
                return new ResponseEntity<>("Created a comment successfully", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Failed to create comment", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Created a failed comment", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/delete-comment")
    public ResponseEntity<?> deleteComment(@RequestParam(value = "id") Integer id,HttpServletRequest requestToken) {
        try {
            Integer userId = jwtService.getUserIdByToken(requestToken);
            boolean isAdmin = jwtService.isAdmin(requestToken);
            System.out.printf("isAdmin" + isAdmin);
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("Invalid comment ID");
            }
            Comment comment = commentServices.getACommentById(id);
            int ownerId = comment.getUser().getId();
            if (userId == ownerId || isAdmin) {
                boolean isDelete = commentServices.deleteComment(id);
                if (isDelete) {
                    return new ResponseEntity<>("Delete comment successfully!", HttpStatus.OK);
                }
            }
            return new ResponseEntity<>("Delete comment fail!", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Delete comment fail!", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/update-comment", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE
    }, produces = {MediaType.APPLICATION_JSON_VALUE
    })
    public @ResponseBody ResponseEntity<?> updateComment(Comment content,
                                                        @RequestParam(value = "id") Integer id,HttpServletRequest requestToken) {
        try {
            Integer userId = jwtService.getUserIdByToken(requestToken);
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("Invalid comment ID");
            }
            Comment comment = commentServices.getACommentById(id);
            int ownerId = comment.getUser().getId();
            if (userId == ownerId) {
                Comment updatedCommentResult = commentServices.updateComment(id, content);
                if (updatedCommentResult != null) {
                    return new ResponseEntity<>("Updated comment successfully", HttpStatus.OK);
                }
                return new ResponseEntity<>("Failed to update comment", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return new ResponseEntity<>("Internal server!", HttpStatus.BAD_REQUEST);
        }
        return null;
    }
    @GetMapping(value = "/get-comment-blog")
    public ResponseEntity<?> getCommentBlog( @RequestParam(value = "blogId") Integer blogid) {
        try {
            List<Comment> commentsList = commentServices.getAllCommentByBlogId(blogid);
            return new ResponseEntity<>(new PageComment("Get All Blog Successfully", commentsList), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Retrieving information failed", HttpStatus.BAD_REQUEST);
        }
    }

}
