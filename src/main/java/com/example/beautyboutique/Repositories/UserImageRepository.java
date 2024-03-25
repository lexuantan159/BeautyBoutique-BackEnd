package com.example.beautyboutique.Repositories;

import com.example.beautyboutique.Models.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage,Integer> {

}
