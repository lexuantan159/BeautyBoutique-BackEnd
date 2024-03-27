package com.example.beautyboutique.Services.Like;

import com.example.beautyboutique.Models.BlogPost;
import com.example.beautyboutique.Models.Comment;
import com.example.beautyboutique.Models.LikeEntity;
import com.example.beautyboutique.Models.User;

import java.util.List;

public interface LikeService {
    // Kiểm tra xem một người dùng đã like một blog hay chưa
    boolean hasUserLikedBlog(Integer userId, Integer blogId);

    // Thêm mới một like
    public boolean addLike(User user, BlogPost blog);
    public List<LikeEntity> getAllLikeByBlog(Integer blogId);
    // Xóa một like
    public boolean removeLike(User user, BlogPost blog);
    int getLikesCount(Integer blogId);
}
