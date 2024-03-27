package com.example.beautyboutique.Repositories;

import com.example.beautyboutique.Models.BlogPost;
import com.example.beautyboutique.Models.Feedback;
import com.example.beautyboutique.Models.LikeEntity;
import com.example.beautyboutique.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity,Integer> {
    @Query("SELECT COUNT(l) FROM LikeEntity l WHERE l.blogPost.id = :blogId")
    int countLikesByBlogId(Integer blogId);

    @Query("SELECT COUNT(l) > 0 FROM LikeEntity l WHERE l.blogPost.id = :blogId AND l.user.id = :userId")
    boolean existsByBlogIdAndUserId(Integer blogId, Integer userId);

    LikeEntity findByUserAndBlogPost(User user, BlogPost blogPost);

    List<LikeEntity> findAllByBlogPost_Id(Integer blogId);
}

