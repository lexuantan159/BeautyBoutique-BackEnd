package com.example.beautyboutique.Services.User;

import com.example.beautyboutique.DTOs.Requests.User.UserRequest;
import com.example.beautyboutique.Models.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

  User findById(Integer id);

    List<User> findByName(String userName);

    User delete(Integer id);

    User saveAfterCheck (User user);

    User updateUser(Integer id, User userUpdate);

    User update(Integer id, UserRequest userUpdate);


    User save(User user);

    public User getUserByUsername(String username);
    UserDetailsService userDetailsService ();
    Optional<User> getUserById(Integer userId);

}
