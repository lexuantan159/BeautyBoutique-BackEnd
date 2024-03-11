package com.example.beautyboutique.Services.User;

import com.example.beautyboutique.Models.Product;
import com.example.beautyboutique.Models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    public Optional<User> getUserByUserName(String userName);

    List<User> findAll();

  User findById(Integer id);

    List<User> findByName(String userName);

    User delete(Integer id);

    User saveafftercheck (User user);


    User save(User user);
}
