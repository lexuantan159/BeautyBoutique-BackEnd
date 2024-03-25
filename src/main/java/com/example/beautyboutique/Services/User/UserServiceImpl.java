package com.example.beautyboutique.Services.User;


import com.example.beautyboutique.DTOs.Requests.User.UserRequest;
import com.example.beautyboutique.Exception.ResourceNotFoundException;
import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Models.UserImage;
import com.example.beautyboutique.Repositories.UserImageRepository;
import com.example.beautyboutique.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }

    @Override
    public List<User> findByName(String username) {
        List<User> users = userRepository.findByUsernameContaining(username);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("User with name " + username + " not found");
        }
        return users;
    }

    @Override
    public User delete(Integer id) {
        User userToDelete = findById(id);
        userRepository.delete(userToDelete);
        return userToDelete;
    }

    @Override
    public User saveAfterCheck(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Integer id, User userUpdate) {
        return null;
    }

    @Override
    public User update(Integer id, UserRequest userUpdate) {
        User user = findById(id);
        if (!user.getEmail().equals(userUpdate.getEmail()) && userRepository.existsByEmail(userUpdate.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }
        user.setFullName(userUpdate.getFullName());
        user.setEmail(userUpdate.getEmail());
        user.setDateOfBirth(userUpdate.getDateOfBirth());
        user.setAddress(userUpdate.getAddress());
        if (userUpdate.getImageIds() != null && userUpdate.getImageUrls() != null) {
            IntStream.range(0, userUpdate.getImageIds().length)
                    .forEach(index -> {
                        String imageId = userUpdate.getImageIds()[index];
                        String imageUrl = userUpdate.getImageUrls()[index];
                        UserImage image = new UserImage();
                        image.setId(imageId);
                        image.setImageUrl(imageUrl);
                        image.setUser(user);
                        userImageRepository.save(image);
                    });
        }
        return userRepository.save(user);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }



    @Override
    public Optional<User> getUserById(Integer userId) {
        return userRepository.findUserById(userId);
    }

    public UserDetailsService userDetailsService(){
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username){
                return userRepository.findByUsername(username)
                        .orElseThrow(()->new UsernameNotFoundException("username not found"));
            }
        };
    }
}
