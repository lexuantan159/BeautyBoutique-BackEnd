package com.example.beautyboutique.Controllers;
import com.example.beautyboutique.Services.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private UserService userService;

}
