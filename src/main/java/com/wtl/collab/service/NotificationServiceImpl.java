package com.wtl.collab.service;


import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.type.DateTime;
import com.wtl.collab.exception.GeneralException;
import com.wtl.collab.exception.ResourceNotFound;
import com.wtl.collab.model.Project;
import com.wtl.collab.model.ProjectJoinRequest;
import com.wtl.collab.model.User;
import com.wtl.collab.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ExecutionException;

@Service
public class NotificationServiceImpl implements  NotificationService{

    private Firestore firestore;

    private UserService userService;

    private ProjectRepository projectRepository;

    public NotificationServiceImpl(Firestore firestore, UserService userService,  ProjectRepository projectRepository){
        this.userService = userService;
        this.firestore = firestore;
        this.projectRepository = projectRepository;
    }

    @Override
    public String addRequest(ProjectJoinRequest joinRequest, User user) {

        Long projectId = joinRequest.getProjectId();
        if(projectId == null) throw new GeneralException("Project Id is null");

        Project project = projectRepository.findById(projectId)
                    .orElseThrow(()-> new ResourceNotFound("Project not found with projectId: "+ projectId));

        if(project.getMembers().contains(user)) throw new GeneralException("User already part of the project");

        try {
            // Check if user has already sent a "PENDING" request
            Query query = firestore.collection("Project Join Requests")
                    .whereEqualTo("projectId", projectId)
                    .whereEqualTo("userID", user.getId())
                    .whereEqualTo("status", "PENDING");

            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            if (!querySnapshot.get().isEmpty()) {
                return "You have already sent a join request for this project.";
            }

            // If both conditions pass, create a new join request
            joinRequest.setStatus("PENDING");
            joinRequest.setUserID(user.getId());
            joinRequest.setTimeStamp(new Date());

            DocumentReference documentReference = firestore.collection("Project Join Requests").document();
            WriteResult result = documentReference.set(joinRequest).get();

            return "Document saved with updateTime: " + result.getUpdateTime();
        }
        catch (Exception e) {
            System.out.println("Notification service exception: " + e.getMessage());
            throw new GeneralException("Error processing join request");
        }

    }


}
