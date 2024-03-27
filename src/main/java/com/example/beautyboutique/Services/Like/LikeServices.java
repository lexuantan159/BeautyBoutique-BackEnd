package com.example.beautyboutique.Services.Like;

import com.example.beautyboutique.Models.BlogPost;
import com.example.beautyboutique.Models.Comment;
import com.example.beautyboutique.Models.LikeEntity;
import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Repositories.BlogRepository;
import com.example.beautyboutique.Repositories.LikeRepository;
import com.example.beautyboutique.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LikeServices implements LikeService {
    @Autowired
    LikeRepository likeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BlogRepository blogRepository;

    @Override
    public boolean hasUserLikedBlog(Integer userId, Integer blogId) {
        return likeRepository.existsByBlogIdAndUserId(blogId, userId);
    }

    @Override
    public boolean addLike(User user, BlogPost blog) {
        LikeEntity like = new LikeEntity();
        like.setUser(user);
        like.setBlogPost(blog);
        if (like != null) {
            likeRepository.save(like);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<LikeEntity> getAllLikeByBlog(Integer blogId) {
        try {
            List<LikeEntity> likeList = likeRepository.findAllByBlogPost_Id(blogId);
            return likeList;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public boolean removeLike(User user, BlogPost blog) {
        LikeEntity like = likeRepository.findByUserAndBlogPost(user, blog);
        if (like != null) {
            likeRepository.delete(like);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getLikesCount(Integer blogId) {
        return likeRepository.countLikesByBlogId(blogId);
    }
}
