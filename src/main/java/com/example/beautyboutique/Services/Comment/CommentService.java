package com.example.beautyboutique.Services.Comment;

import com.example.beautyboutique.Models.BlogPost;
import com.example.beautyboutique.Models.Comment;

import java.util.List;

public interface CommentService {
    public List<Comment> getAllComment();
    public  Comment getACommentById(Integer id);
    public Comment createComment(Comment comment);
    public Comment updateComment(Integer id, Comment updateComment);
    public boolean deleteComment(Integer id);
    public List<Comment> getAllCommentByBlogId(Integer blogId);
}
