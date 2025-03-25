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
import com.wtl.collab.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class NotificationServiceImpl implements  NotificationService{

    private Firestore firestore;

    private UserRepository userRepository;

    private ProjectRepository projectRepository;

    public NotificationServiceImpl(Firestore firestore, UserRepository userRepository,  ProjectRepository projectRepository){
        this.userRepository = userRepository;
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
            Query query = firestore.collection("ProjectJoinRequests")
                    .whereEqualTo("projectId", projectId)
                    .whereEqualTo("userId", user.getId())
                    .whereEqualTo("status", "PENDING");

            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            if (!querySnapshot.get().isEmpty()) {
                return "You have already sent a join request for this project.";
            }

            // If both conditions pass, create a new join request
            joinRequest.setStatus("PENDING");
            joinRequest.setUserId(user.getId());
            joinRequest.setTimeStamp(new Date());

            String docId = user.getId() + "_" + projectId;

            DocumentReference documentReference = firestore.collection("ProjectJoinRequests").document(docId);
            WriteResult result = documentReference.set(joinRequest).get();

            return "Document saved with updateTime: " + result.getUpdateTime() + " customId: "+docId;
        }
        catch (Exception e) {
            System.out.println("Notification service exception: " + e.getMessage());
            throw new GeneralException("Error processing join request");
        }

    }

    @Override
    @Transactional
    public String acceptRequest(ProjectJoinRequest joinRequest, User creator) {
        Long projectId = joinRequest.getProjectId();
        if (projectId == null) throw new GeneralException("Project Id is null");

        System.out.println("Fetching project with ID: " + projectId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found with projectId " + projectId));

        System.out.println("Checking if creator is valid");
        if (!project.getCreator().getId().equals(creator.getId()))
            return "You are not the creator of the project";

        System.out.println("Fetching user with ID: " + joinRequest.getUserId());
        User user = userRepository.findById(joinRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFound("User not found with userId: " + joinRequest.getUserId()));

        System.out.println("Checking if user is already in the project");
        if (project.getMembers().contains(user))
            return "User is already a member of the project";

        System.out.println("Adding user to project members");
        project.getMembers().add(user);
        projectRepository.save(project); // ✅ Save the project

        user.getProjects().add(project); // ✅ If necessary

        System.out.println("Updating Firestore...");
        try {
            String docId = joinRequest.getUserId() + "_" + projectId;
            DocumentReference docRef = firestore.collection("ProjectJoinRequests").document(docId);
            docRef.set(Map.of("status", "ACCEPTED"), SetOptions.merge()).get();
            System.out.println("Firestore update successful!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("Failed to update Firestore: " + e.getMessage());
        }

        return "User successfully added to project";
    }




}
