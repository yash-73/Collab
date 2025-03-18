package com.wtl.collab.controller;


import com.wtl.collab.model.Tech;
import com.wtl.collab.model.User;
import com.wtl.collab.payload.APIResponse;
import com.wtl.collab.payload.LoginRequestDTO;
import com.wtl.collab.payload.SignupRequestDTO;
import com.wtl.collab.payload.SignupResponse;
import com.wtl.collab.repository.UserRepository;
import com.wtl.collab.service.UserService;
import com.wtl.collab.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private UserService userService;

    private  UserRepository userRepository;

    private BCryptPasswordEncoder encoder;

    private AuthUtil authUtil;

    public UserController(UserService userService, UserRepository userRepository, BCryptPasswordEncoder encoder, AuthUtil authUtil) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.authUtil = authUtil;
    }

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

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/tech/add")
    public ResponseEntity<Set<Tech>> addTechStack(@RequestBody Set<String> technologies){
        User user = authUtil.loggedInUser();
        Set<Tech> techStack = userService.addTech(technologies, user);
        return new ResponseEntity<>(techStack, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping("/tech/remove")
    public ResponseEntity<Set<Tech>> removeTech(@RequestBody String technology){
        User user = authUtil.loggedInUser();
        Set<Tech> techStack = userService.removeTech(technology, user);
        return new ResponseEntity<>(techStack, HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/tech/see")
    public ResponseEntity<Object> seeTech(){
        User user = authUtil.loggedInUser();
        Set<Tech> techStack = user.getTechStack();
        return techStack.isEmpty() ? ResponseEntity.ok("Tech Stack is empty") : ResponseEntity.ok(techStack);
    }





}
