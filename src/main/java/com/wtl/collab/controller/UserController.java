package com.wtl.collab.controller;


import com.wtl.collab.model.User;
import com.wtl.collab.payload.LoginRequestDTO;
import com.wtl.collab.payload.SignupRequestDTO;
import com.wtl.collab.payload.SignupResponse;
import com.wtl.collab.repository.UserRepository;
import com.wtl.collab.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @PostMapping("/register")
    public ResponseEntity<SignupResponse> createUser(@RequestBody SignupRequestDTO userDTO){
        SignupResponse response = userService.register(userDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO user){
        String token =  userService.verify(user);
        ResponseCookie cookie = ResponseCookie.from("jwtCookie", token)
                .path("/")
                .maxAge(60 * 60 *  24 * 7)
                .httpOnly(true)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwtCookie", "")
                .path("/")
                .maxAge(0)  // Expire the cookie immediately
                .httpOnly(true)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged out successfully");
    }

    @GetMapping("/getusers")
    public List<User> getUsers(){
        return userRepository.findAll();
    }

//    @DeleteMapping("/removeUser/{id}")
//    public User deleteUser(@PathVariable Long id){
//        User deleted = userRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found"))     ;
//        userRepository.deleteById(id);
//        return deleted;
//    }

    @GetMapping("/token")
    public CsrfToken getToken(HttpServletRequest request){
        return (CsrfToken) request.getAttribute("_csrf");
    }


}
