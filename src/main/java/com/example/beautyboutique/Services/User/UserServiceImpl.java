package com.example.beautyboutique.Services.User;

import com.example.beautyboutique.Exception.ResourceNotFoundException;
import com.example.beautyboutique.Models.Product;
import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements  UserService {

    @Autowired
    UserRepository userRepository;
    @Override
    public Optional<User> getUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id"+ id +"not found"));
    }

    @Override
    public List<User> findByName(String userName) {
        List<User> users = userRepository.findByUserNameContaining(userName);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("User with name " + userName + " not found");
        }
        return users;
    }


    @Override
    public User delete(Integer id) {
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userRepository.delete(userToDelete);
        return userToDelete;
    }


    @Override
    public User saveafftercheck(User user) {
        if(userRepository.existsByUserName(user.getUserName())) {
            System.out.println("UserName Have Exits");
            return  null;
        }
            return userRepository.save(user);
    }
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findUserById(id);
    }

}
