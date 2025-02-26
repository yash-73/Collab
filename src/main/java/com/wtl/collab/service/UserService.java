package com.wtl.collab.service;

import com.wtl.collab.model.User;
import com.wtl.collab.payload.LoginRequesetDTO;
import com.wtl.collab.payload.SignupRequestDTO;
import com.wtl.collab.payload.SignupResponse;


public interface UserService {

    SignupResponse register(SignupRequestDTO userDTO);

    String verify(LoginRequesetDTO user);
}
