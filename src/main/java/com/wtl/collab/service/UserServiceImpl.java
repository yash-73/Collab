package com.wtl.collab.service;

import com.wtl.collab.model.User;
import com.wtl.collab.payload.LoginRequesetDTO;
import com.wtl.collab.payload.SignupRequestDTO;
import com.wtl.collab.payload.SignupResponse;
import com.wtl.collab.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements  UserService{

    private UserRepository userRepository;

    private AuthenticationManager authenticationManager;

    private JwtService jwtService;

    private ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager,
                           JwtService jwtService, ModelMapper modelMapper){
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.modelMapper = modelMapper;
    }



    public SignupResponse register(SignupRequestDTO userDTO){
        User user = modelMapper.map(userDTO, User.class);
        userRepository.save(user);
        return new SignupResponse("User registered successfully");
    }

    @Override
    public String verify(LoginRequesetDTO user) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

      if(authentication.isAuthenticated()) return jwtService.generateToken(user.getUsername());
      else return "Failure";
    }
}
