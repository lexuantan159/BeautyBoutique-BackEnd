package com.example.beautyboutique.Controllers;

import com.example.beautyboutique.DTOs.Requests.User.UserRequest;
import com.example.beautyboutique.Models.Product;
import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Services.JWTService;
import com.example.beautyboutique.Services.User.UserService;
import com.example.beautyboutique.Services.User.UserServiceImpl;
import com.example.beautyboutique.Utils.ZaloAlgorithem.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    UserServiceImpl userService;

    @Autowired
    private JWTService jwtService;

    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getUser")
    public ResponseEntity<?> getUserById(HttpServletRequest request) {
        try {
            Integer userId = jwtService.getUserIdByToken(request);
            User user = userService.findById(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            User userDelete = userService.delete(id);
            return ResponseEntity.ok("User  deleted successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateUser( HttpServletRequest request,@RequestBody UserRequest userUpdate) {
        try {
            Integer userId = jwtService.getUserIdByToken(request);
            User updatedUser = userService.update(userId, userUpdate);
            return ResponseEntity.ok(updatedUser);
        } catch (DataIntegrityViolationException e) {
            System.out.println("Eros"+e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            System.out.println("Eros"+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<List<User>> findByUserName(@PathVariable String username) {
        try {
            List<User> users = userService.findByName(username);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
