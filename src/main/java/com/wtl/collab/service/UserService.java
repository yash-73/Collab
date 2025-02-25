package com.wtl.collab.service;

import com.wtl.collab.model.User;
import org.springframework.stereotype.Service;


public interface UserService {

    User register(User user);

    String verify(User user);
}
