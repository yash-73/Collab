package com.wtl.collab.service;


import com.wtl.collab.model.User;
import com.wtl.collab.payload.ProjectDTO;
import jakarta.transaction.Transactional;

public interface ProjectService {

    @Transactional
    ProjectDTO createNewProject(ProjectDTO projectDTO, User user);


    ProjectDTO updateProject(ProjectDTO projectDTO,Long projectId, User user);
}
