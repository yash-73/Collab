package com.wtl.collab.service;

import com.wtl.collab.model.Tech;
import com.wtl.collab.model.User;
import com.wtl.collab.payload.LoginRequestDTO;
import com.wtl.collab.payload.ProjectDTO;
import com.wtl.collab.payload.SignupRequestDTO;
import com.wtl.collab.payload.SignupResponse;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Set;


public interface UserService {

    SignupResponse register(SignupRequestDTO userDTO);

    String verify(LoginRequestDTO user);

    Set<Tech> addTech(Set<String> techStack, User user);


    @Transactional
    Set<Tech> removeTech(String technology, User user);

    List<ProjectDTO> getMyCreatedProjects(User user);
}
