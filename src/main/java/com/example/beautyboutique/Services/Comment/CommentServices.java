package com.example.beautyboutique.Services.Comment;
import com.example.beautyboutique.Models.Comment;
import com.example.beautyboutique.Repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServices implements CommentService{
    @Autowired
    CommentRepository commentRepository;
    @Override
    public List<Comment> getAllComment() {
        try {
            List<Comment> commentList = commentRepository.findAll();
            return commentList;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }
    public List<Comment> getAllCommentByBlogId(Integer blogId) {
        try {
            Sort sortByCreateDate = Sort.by(Sort.Direction.DESC, "createdAt");
            List<Comment> commentList = commentRepository.findCommentsByBlogPost_Id(blogId,sortByCreateDate);
            return commentList;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Comment getACommentById(Integer id) {
        try {
            Optional<Comment> optionalComment = commentRepository.findById(id);
            if (optionalComment.isPresent()) {
                return optionalComment.get();
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Comment();
        }
    }

    @Override
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(Integer id, Comment updateComment) {
        try {
            Optional<Comment> optionalComment = commentRepository.findById(id);
            if (optionalComment.isPresent()) {
                Comment existingComment = optionalComment.get();

                if (updateComment.getContent() != null) {
                    existingComment.setContent(updateComment.getContent());
                }
                if (updateComment.getUser() != null) {
                    existingComment.setUser(updateComment.getUser());
                }
                if (updateComment.getBlogPost() != null) {
                    existingComment.setBlogPost(updateComment.getBlogPost());
                }

                return commentRepository.save(existingComment);
            } else {
                System.out.println("Comment not found with id: " + id);
            }
        } catch (Exception e) {
            System.out.println("An error occurred while updating the Comment: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean deleteComment(Integer id) {
        try {
            Optional<Comment> optionalComment = commentRepository.findById(id);
            if (optionalComment.isPresent()) {
                commentRepository.deleteById(id);
                System.out.println("Comment with id " + id + " have been deleted.");
                return true;
            } else {
                System.out.println("Comment not found with id: " + id);
                return false;
            }
        } catch (Exception e) {
            System.out.println("An error occurred while deleting the comment: " + e.getMessage());
            return false;
        }
    }
}
