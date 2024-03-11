package com.example.beautyboutique.Services.Blog;

import com.example.beautyboutique.Models.BlogImage;
import com.example.beautyboutique.Models.BlogPost;

import java.util.List;
import java.util.Optional;

public interface BlogService {
    public List<BlogPost> getAllBlog();
    public BlogPost updateBlog(Integer id, BlogPost updateBlog);

    public boolean deleteBlog(Integer id);
    public boolean deleteAImageBlog(String id);
    public BlogPost createBlog(BlogPost blog);
    public BlogPost getABlogById(Integer id);


}
