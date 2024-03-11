package com.example.beautyboutique.Repositories;

import com.example.beautyboutique.Models.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Integer> {
    List<Comment> findCommentsByBlogPost_Id(Integer blogId, Sort sortByCreateDate);
}
