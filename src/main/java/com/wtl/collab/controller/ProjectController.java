package com.wtl.collab.controller;


import com.wtl.collab.model.User;
import com.wtl.collab.payload.ProjectDTO;
import com.wtl.collab.service.ProjectService;
import com.wtl.collab.util.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/project")
public class ProjectController {


    private ProjectService projectService;

    private AuthUtil authUtil;

    public ProjectController(ProjectService projectService, AuthUtil authUtil){
        this.authUtil = authUtil;
        this.projectService = projectService;
    }

        @PostMapping("/create")
        public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO){
                    User user = authUtil.loggedInUser();
                    ProjectDTO newProject = projectService.createNewProject(projectDTO, user);
                    return new ResponseEntity<>(newProject, HttpStatus.CREATED);
        }

        @PostMapping("/see")
        public ResponseEntity<ProjectDTO> seeProject(@RequestBody ProjectDTO projectDTO){
                User user  = authUtil.loggedInUser();
                return new ResponseEntity<>(projectDTO, HttpStatus.OK);

        }

}
