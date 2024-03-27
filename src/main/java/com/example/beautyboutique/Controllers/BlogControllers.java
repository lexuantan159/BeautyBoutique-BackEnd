package com.example.beautyboutique.Controllers;

import com.example.beautyboutique.DTOs.Requests.Blog.BlogRequest;
import com.example.beautyboutique.DTOs.Responses.Blog.PageBlog;
import com.example.beautyboutique.DTOs.Responses.Cart.CartResponse;
import com.example.beautyboutique.Models.BlogImage;
import com.example.beautyboutique.Models.BlogPost;
import com.example.beautyboutique.Models.Comment;
import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Services.Blog.BlogServices;
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
@RequestMapping("/api/blog")
@CrossOrigin(origins = "http://localhost:3000")

public class BlogControllers {
    @Autowired
    BlogServices blogServices;
    @Autowired
    UserService userService;
    @Autowired
    JWTServiceImpl jwtService;

    @GetMapping(value = "/get-all-blog")
    public ResponseEntity<?> getAllBlog() {
        try {
            List<BlogPost> blogPostList = blogServices.getAllBlog();
            return new ResponseEntity<>(new PageBlog("Get All Blog Successfully", blogPostList), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Retrieving information failed", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/create-blog", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE
    }, produces = {MediaType.APPLICATION_JSON_VALUE
    })
    public @ResponseBody ResponseEntity<?> createBlog(BlogRequest request , HttpServletRequest requestToken) {
        try {
            System.out.printf("token --------------" + requestToken);
            String[] imageIds = request.getImageIds();
            String[] imageUrls = request.getImageUrls();

            if (imageIds == null || imageUrls == null || imageIds.length != imageUrls.length) {
                return new ResponseEntity<>("Invalid imageIds or imageUrls", HttpStatus.BAD_REQUEST);
            }
            Integer userId = jwtService.getUserIdByToken(requestToken);
            Optional<User> userBlog = userService.getUserById(userId);
            BlogPost blog = new BlogPost();
            blog.setUser(userBlog.get());
            blog.setTitle(request.getTitle());
            blog.setContent(request.getContent());
            blog.setLikeCount(request.getLikeCount());
            BlogPost createdBlog = blogServices.createBlog(blog);
            if (createdBlog != null) {
                IntStream.range(0, imageIds.length).forEach(index -> {
                    String imageId = imageIds[index];
                    String imageUrl = imageUrls[index];
                    BlogImage image = new BlogImage();
                    image.setId(imageId);
                    image.setImageUrl(imageUrl);
                    image.setBlogPost(createdBlog);
                    blogServices.createBlogImage(image);
                });
                return new ResponseEntity<>("Created a successful blog post", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Failed to create blog post", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Failed to create blog post", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/delete-blog/")
    public ResponseEntity<?> deleteBlog(@RequestParam(value = "id") Integer id,HttpServletRequest requestToken) {
        try {

            Integer userId = jwtService.getUserIdByToken(requestToken);
            boolean isAdmin = jwtService.isAdmin(requestToken);

            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("Invalid blog ID");
            }
            BlogPost blog = blogServices.getABlogById(id);
            int ownerId = blog.getUser().getId();
            if (userId == ownerId || isAdmin) {
                boolean isDelete = blogServices.deleteBlog(id);
                if (isDelete)
                    return new ResponseEntity<>("Delete blog successfully!", HttpStatus.OK);
            }
            return new ResponseEntity<>("Delete blog fail!", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Delete blog fail!", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/delete-image-blog-id/")
    public ResponseEntity<?> deleteAImage(@RequestParam(value = "id") String id) {
        try {
            if (id == null || id.length() <= 0) {
                return ResponseEntity.badRequest().body("Invalid blog ID");
            }
            boolean isDelete = blogServices.deleteAImageBlog(id);
            if (isDelete)
                return new ResponseEntity<>("Delete blog successfully!", HttpStatus.OK);

            return new ResponseEntity<>("Delete blog fail!", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Delete blog fail!", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/update-blog", consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE
    }, produces = {MediaType.APPLICATION_JSON_VALUE
    })
    public @ResponseBody ResponseEntity<?> updateBlog(BlogRequest request,
                                                      @RequestParam(value = "id") Integer id,HttpServletRequest requestToken) {
        try {
            Integer userId = jwtService.getUserIdByToken(requestToken);
            String[] imageIds = request.getImageIds();
            String[] imageUrls = request.getImageUrls();
            Optional<User> userBlog = userService.getUserById(userId);
            BlogPost blog = new BlogPost();
            blog.setUser(userBlog.get());
            blog.setTitle(request.getTitle());
            blog.setContent(request.getContent());
            BlogPost updateBlog = blogServices.updateBlog(id, blog);
            if (updateBlog != null) {
                if (imageIds == null && imageUrls == null) {
                    return new ResponseEntity<>("Update a successful blog post", HttpStatus.CREATED);
                }
                IntStream.range(0, imageIds.length).forEach(index -> {
                    String imageId = imageIds[index];
                    String imageUrl = imageUrls[index];
                    BlogImage image = new BlogImage();
                    image.setId(imageId);
                    image.setImageUrl(imageUrl);
                    image.setBlogPost(updateBlog);
                    blogServices.createBlogImage(image);
                });
                return new ResponseEntity<>("Update a successful blog post", HttpStatus.CREATED);

            } else {
                return new ResponseEntity<>("Failed to update blog post: Blog post not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Failed to Update blog post", HttpStatus.BAD_REQUEST);
        }
    }

}
