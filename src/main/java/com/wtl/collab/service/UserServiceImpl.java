package com.wtl.collab.service;

import com.wtl.collab.exception.ResourceNotFound;
import com.wtl.collab.model.*;
import com.wtl.collab.payload.LoginRequestDTO;
import com.wtl.collab.payload.ProjectDTO;
import com.wtl.collab.payload.SignupRequestDTO;
import com.wtl.collab.payload.SignupResponse;
import com.wtl.collab.repository.RoleRepository;
import com.wtl.collab.repository.TechRepository;
import com.wtl.collab.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements  UserService{

    private UserRepository userRepository;

    private AuthenticationManager authenticationManager;

    private JwtService jwtService;

    private ModelMapper modelMapper;

    private RoleRepository roleRepository;

    private TechRepository techRepository;

    private BCryptPasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager,
                           JwtService jwtService, ModelMapper modelMapper, BCryptPasswordEncoder encoder,
                           RoleRepository roleRepository, TechRepository techRepository){
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.modelMapper = modelMapper;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
        this.techRepository = techRepository;
    }


    @Override
    public SignupResponse register(SignupRequestDTO userDTO){
        User user = modelMapper.map(userDTO, User.class);

        //Encode password before saving
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

        //Save techStack if provided
        //Convert Technology enum set to Tech set
        if(!userDTO.getTechStack().isEmpty()){
            user.setTechStack(
                    userDTO.getTechStack().stream()
                            .map(tech -> {
                                Tech technology = techRepository.findByTechName(tech);
                                if(technology == null) throw new RuntimeException("Tech not found");
                                else return technology;
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


    @Override
    public Set<Tech> addTech(Set<String> techStack, User user) {
        Set<Tech> existingTechStack = user.getTechStack();
           techStack.forEach(
                        tech -> {
                            Tech foundTech = techRepository.findByTechName(tech);
                            if(foundTech == null) throw new ResourceNotFound("Tech not found" + tech);
                            else existingTechStack.add(foundTech);
                        }
                );
           user.setTechStack(existingTechStack);
           userRepository.save(user);
           return existingTechStack;
    }

    @Override
    @Transactional
    public Set<Tech> removeTech(String technology, User user) {

        Set<Tech> existingTechStack = user.getTechStack();
        Tech techToRemove = techRepository.findByTechName(technology);
        if(techToRemove == null) throw new ResourceNotFound("Tech not found " +  technology);
        else existingTechStack.remove(techToRemove);
        user.setTechStack(existingTechStack);
        return existingTechStack;
    }

    @Override
    public List<ProjectDTO> getMyCreatedProjects(User user) {

        Set<Project> createdProjects = user.getCreatedProjects();

        return createdProjects.stream().map(
                project -> {
                    ProjectDTO projectDTO = modelMapper.map(project, ProjectDTO.class);
                    projectDTO.setTechStack(
                            project.getTechStack().stream().map(
                                    Tech::getTechName
                            ).collect(Collectors.toSet())
                    );

                    return projectDTO;
                }
        ).toList();

    }
}
