package com.wtl.collab.service;


import com.wtl.collab.model.User;
import com.wtl.collab.payload.ProjectDTO;

public interface ProjectService {
    ProjectDTO createNewProject(ProjectDTO projectDTO, User user);
}
