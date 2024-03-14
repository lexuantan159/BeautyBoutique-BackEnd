package com.example.beautyboutique.Services.User;


import com.example.beautyboutique.DTOs.UserDTO;
import com.example.beautyboutique.Exceptions.DataNotFoundException;
import com.example.beautyboutique.Models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    public Optional<User> getUserByUsername(String username);


    User createUser(UserDTO userDTO) throws DataNotFoundException;
    String login(String username, String password) throws Exception ;

    Optional<User> getUserById(Integer userId);
}
