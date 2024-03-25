package com.example.beautyboutique.Services.User;


import com.example.beautyboutique.DTOs.Requests.User.UserRequest;

import com.example.beautyboutique.Exception.ResourceNotFoundException;

import com.example.beautyboutique.Models.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService {


    User getUserByUsername(String username);

    List<User> findAll();

  User findById(Integer id);


    public Optional<User> getUserByUserName(String userName);

    public Optional<User> getUserById(Integer id) throws ResourceNotFoundException;



    List<User> findByName(String userName);

    User delete(Integer id);


    User saveAfterCheck(User user);

    User update(Integer id, UserRequest userUpdate);


    User updateUser (Integer id , User userUpdate);

    User save(User user);


    UserDetailsService userDetailsService();
}
