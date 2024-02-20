package com.example.beautyboutique.Services.Blog;

import com.example.beautyboutique.Models.BlogImage;
import com.example.beautyboutique.Models.BlogPost;
import com.example.beautyboutique.Models.Comment;
import com.example.beautyboutique.Repositories.BlogImageRepository;
import com.example.beautyboutique.Repositories.BlogRepository;
import com.example.beautyboutique.Repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BlogServices implements  BlogService {
    @Autowired
    BlogRepository blogRepository;

    @Autowired
    BlogImageRepository blogImageRepository;
    @Autowired
    CommentRepository commentRepository;
    @Override
    public List<BlogPost> getAllBlog() {
        try {
            List<BlogPost> blogPostList = blogRepository.findAll();
                return blogPostList;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public BlogPost updateBlog(Integer id, BlogPost updatedBlogPost) {
        try {
            Optional<BlogPost> optionalBlogPost = blogRepository.findById(id);
            if (optionalBlogPost.isPresent()) {
                BlogPost existingBlogPost = optionalBlogPost.get();

                if (updatedBlogPost.getTitle() != null) {
                    existingBlogPost.setTitle(updatedBlogPost.getTitle());
                }
                if (updatedBlogPost.getContent() != null) {
                    existingBlogPost.setContent(updatedBlogPost.getContent());
                }
                if (updatedBlogPost.getLikeCount() != null) {
                    existingBlogPost.setLikeCount(updatedBlogPost.getLikeCount());
                }

                return blogRepository.save(existingBlogPost);
            } else {
                System.out.println("BlogPost not found with id: " + id);
            }
        } catch (Exception e) {
            System.out.println("An error occurred while updating the blog post: " + e.getMessage());
        }
        return null; // Indicate failure
    }

    @Override
    public boolean deleteBlog(Integer id) {
        try {
            Optional<BlogPost> optionalBlog = blogRepository.findById(id);
            if (optionalBlog.isPresent()) {
                BlogPost blogPost = optionalBlog.get();
                List<BlogImage> images = blogPost.getImages();
                if (images != null && !images.isEmpty()) {
                    blogImageRepository.deleteAll(images);
                }
                List<Comment> comments = blogPost.getComments();
                if (comments != null && !comments.isEmpty()) {
                    commentRepository.deleteAll(comments);
                }
                blogRepository.deleteById(id);
                System.out.println("BlogPost with id " + id + " and its associated images have been deleted.");
                return true;
            } else {
                System.out.println("BlogPost not found with id: " + id);
                return false;
            }
        } catch (Exception e) {
            System.out.println("An error occurred while deleting the blog post: " + e.getMessage());
            return false;
        }
    }


    public BlogPost createBlog(BlogPost blog) {
        return blogRepository.save(blog);
    }

    @Override
    public BlogPost getABlogById(Integer id) {
        try {
            Optional<BlogPost> optionalBlogPost = blogRepository.findById(id);
            if (optionalBlogPost.isPresent()) {
                return optionalBlogPost.get();
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new BlogPost();
        }
    }

    public BlogImage createBlogImage(BlogImage image){
        return blogImageRepository.save(image);
    }



}
