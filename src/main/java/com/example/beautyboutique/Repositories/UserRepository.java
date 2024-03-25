package com.example.beautyboutique.Repositories;

import com.example.beautyboutique.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findByUsernameContaining(String username);


    Optional<User> findByUsername(String username);



    Optional<User> findUserById(Integer id);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);


}
