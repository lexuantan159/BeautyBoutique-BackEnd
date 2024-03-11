package com.example.beautyboutique.Repositories;

import com.example.beautyboutique.Models.BlogPost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<BlogPost, Integer> {
    List<BlogPost> findAll();

}
