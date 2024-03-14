package com.example.beautyboutique.Controllers;


import com.example.beautyboutique.DTOs.UserDTO;
import com.example.beautyboutique.Services.User.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("api/users")

@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/register")
   // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result
    ){
        try {
            if(result.hasErrors()) {
                List<String> errorsMessages=result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorsMessages);
            }
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword())){
                return ResponseEntity.badRequest().body("pass word does not match");
            }
            userService.createUser(userDTO);
            return ResponseEntity.ok("REGISTER OK");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/login")
    public ResponseEntity<String> login
            (@Valid @RequestBody com.example.beautyboutique.DTOs.UserLoginDTO userLoginDTO){
        try {
            String token = userService.login(userLoginDTO.getUsername()
                    ,userLoginDTO.getPassword());
            return ResponseEntity.ok(token);
        }catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }



}
