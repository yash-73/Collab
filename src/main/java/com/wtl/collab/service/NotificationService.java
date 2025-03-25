package com.wtl.collab.service;


import com.wtl.collab.model.ProjectJoinRequest;
import com.wtl.collab.model.User;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;


public interface NotificationService {

    public String addRequest(ProjectJoinRequest joinRequest, User user);

    public String acceptRequest(ProjectJoinRequest joinRequest, User creator);
}
