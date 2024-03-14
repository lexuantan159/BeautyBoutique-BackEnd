package com.example.beautyboutique.Services.User;



import com.example.beautyboutique.Component.JwtTokenUtil;
import com.example.beautyboutique.DTOs.UserDTO;
import com.example.beautyboutique.Exceptions.DataNotFoundException;
import com.example.beautyboutique.Models.Role;
import com.example.beautyboutique.Models.User;
import com.example.beautyboutique.Repositories.RoleRepository;
import com.example.beautyboutique.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService {
    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;


    @Override
    public User createUser(UserDTO userDTO) throws DataNotFoundException {
        String username = userDTO.getUsername();
        //ktra sdt co chua
        if(userRepository.existsByUsername(username)){
            throw new DataIntegrityViolationException("sdt already exists");
        }
        User newUser= User.builder()
                .fullName(userDTO.getFullName())
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId()).
                build();
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() ->new DataNotFoundException("Role not found"));
        newUser.setRole(role);
        //kiem tra account id =, khong yeu cau password
        if(userDTO.getFacebookAccountId()==0 && userDTO.getGoogleAccountId()==0)
        {
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        return userRepository.save(newUser);
    }
    @Override
    public  String login (String username, String password) throws Exception{
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty()) {
            throw new DataNotFoundException("INVALID PHONE OR PASS");
        }
        User existingUser = optionalUser.get();
        if (existingUser.getFacebookAccountId() == 0
                && existingUser.getGoogleAccountId() == 0) {
            //KIEMTRA MK TRUNG VOI MK MA HOA KHONG
            if(!passwordEncoder.matches(password, existingUser.getPassword())){
                throw  new BadCredentialsException("WRONG PHONE OR PASS");
            }
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username,password,
                existingUser.getAuthorities()
        );
        authenticationManager.authenticate(authenticationToken);
            return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public Optional<User> getUserById(Integer userId) {
        return Optional.empty();
    }

}
