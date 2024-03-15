package com.example.beautyboutique.Services.User;

import com.example.beautyboutique.Exception.ResourceNotFoundException;
import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public Optional<User> getUserByUserName(String userName) throws ResourceNotFoundException {
        Optional<User> user = userRepository.findByUserName(userName);
        if (!user.isPresent()) {
            throw new ResourceNotFoundException("User with username " + userName + " not found");
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findByName(String userName) throws ResourceNotFoundException {
        List<User> users = userRepository.findByUserNameContaining(userName);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("User with name " + userName + " not found");
        }
        return users;
    }

    @Override
    public User delete(Integer id) throws ResourceNotFoundException {
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userRepository.delete(userToDelete);
        return userToDelete;
    }

    @Override
    public User saveAfterCheck(User user) throws IllegalArgumentException {
        if (userRepository.existsByUserName(user.getUserName())) {
            throw new IllegalArgumentException("Username already exists");
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Integer id, User userUpdate) throws ResourceNotFoundException {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (!userOptional.isPresent()) {
                throw new ResourceNotFoundException("User not found");
            }
            User user = userOptional.get();
            user.setUserName(userUpdate.getUserName());
            user.setAddress(userUpdate.getAddress());
            user.setDateOfBirth(userUpdate.getDateOfBirth());
            user.setImageId(userUpdate.getImageId());
            user.setImageURL(userUpdate.getImageURL());
            return userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Integer id) throws ResourceNotFoundException {
        Optional<User> user = userRepository.findUserById(id);
        if (!user.isPresent()) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        return user;
    }
}
