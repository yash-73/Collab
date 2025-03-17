package com.wtl.collab.service;

import com.wtl.collab.exception.ResourceNotFound;
import com.wtl.collab.model.AppRole;
import com.wtl.collab.model.Role;
import com.wtl.collab.model.User;
import com.wtl.collab.payload.LoginRequestDTO;
import com.wtl.collab.payload.SignupRequestDTO;
import com.wtl.collab.payload.SignupResponse;
import com.wtl.collab.repository.RoleRepository;
import com.wtl.collab.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements  UserService{

    private UserRepository userRepository;

    private AuthenticationManager authenticationManager;

    private JwtService jwtService;

    private ModelMapper modelMapper;

    private RoleRepository roleRepository;


    private BCryptPasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager,
                           JwtService jwtService, ModelMapper modelMapper, BCryptPasswordEncoder encoder, RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.modelMapper = modelMapper;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
    }



    public SignupResponse register(SignupRequestDTO userDTO){
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(encoder.encode(user.getPassword()));


        //Convert AppRole hashset to Role hashset

        if(userDTO.getRoles().isEmpty()) {
            Role role =  roleRepository.findByRoleName(AppRole.USER).orElseThrow(()->
                 new ResourceNotFound("Role USER not found")
            );

            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
        }

        else{
            user.setRoles(
                    userDTO.getRoles().stream().map(appRole -> {
                        return roleRepository.findByRoleName(appRole)
                                .orElseThrow( () -> new RuntimeException("Role not found"));

                    }).collect(Collectors.toSet())
            );
        }

        userRepository.save(user);
        return new SignupResponse("User registered successfully");
    }

    @Override
    public String verify(LoginRequestDTO user) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

      if(authentication.isAuthenticated()) return jwtService.generateToken(user.getUsername());
      else return "Failure";
    }
}
