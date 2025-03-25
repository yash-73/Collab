package com.wtl.collab.controller;


import com.wtl.collab.model.ProjectJoinRequest;
import com.wtl.collab.model.User;
import com.wtl.collab.service.NotificationService;
import com.wtl.collab.util.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/notification")
public class NotificationController {

    private AuthUtil authUtil;

    private NotificationService notificationService;

    public NotificationController(AuthUtil authUtil, NotificationService notificationService){
        this.authUtil = authUtil;
        this.notificationService = notificationService;
    }


    @PostMapping("/join-request")
    public ResponseEntity<String> addJoinRequest(@RequestBody ProjectJoinRequest request) {
        User user = authUtil.loggedInUser();
        String response = notificationService.addRequest(request, user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
