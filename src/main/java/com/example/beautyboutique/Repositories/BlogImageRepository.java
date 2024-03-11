package com.example.beautyboutique.Repositories;

import com.example.beautyboutique.Models.BlogImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogImageRepository extends JpaRepository<BlogImage,String> {

}
