package com.wtl.collab.controller;


import com.wtl.collab.model.User;
import com.wtl.collab.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public User createUser(@RequestBody User user){
        return userRepository.save(user);
    }

    @GetMapping("/getusers")
    public List<User> getUsers(){
        return userRepository.findAll();
    }
}
