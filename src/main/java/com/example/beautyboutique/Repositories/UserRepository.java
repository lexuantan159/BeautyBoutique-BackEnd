package com.example.beautyboutique.Repositories;

import com.example.beautyboutique.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userName);
<<<<<<< HEAD
=======

>>>>>>> 007f10876fe2225d402cdf711daebec16fa72bc1
    boolean existsByUserName(String userName);
    List<User> findByUserNameContaining(String userName);
<<<<<<< HEAD
    Optional<User> findUserById(Integer id);
=======

    Optional<User> findUserById(Integer id);

>>>>>>> 007f10876fe2225d402cdf711daebec16fa72bc1
}
