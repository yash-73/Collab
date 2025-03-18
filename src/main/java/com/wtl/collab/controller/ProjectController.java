package com.wtl.collab.controller;


import com.wtl.collab.model.Project;
import com.wtl.collab.model.User;
import com.wtl.collab.payload.ProjectDTO;
import com.wtl.collab.repository.UserRepository;
import com.wtl.collab.service.ProjectService;
import com.wtl.collab.service.UserService;
import com.wtl.collab.util.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/project")
public class ProjectController {


    private UserService userService;
    private ProjectService projectService;
    private AuthUtil authUtil;

    public ProjectController(ProjectService projectService, AuthUtil authUtil,UserService userService){
        this.authUtil = authUtil;
        this.projectService = projectService;
        this.userService = userService;

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

        @PreAuthorize("hasAuthority('USER')")
        @GetMapping("/createdProjects")
        public ResponseEntity<List<ProjectDTO>> getMyCreatedProjects(){
                User user = authUtil.loggedInUser();
                List<ProjectDTO> myCreatedProjects =  userService.getMyCreatedProjects(user);
                return ResponseEntity.ok(myCreatedProjects);
        }

        @PreAuthorize("hasAuthority('USER')")
        @PutMapping("/{projectId}")
        public ResponseEntity<ProjectDTO> updateMyProject(@RequestBody ProjectDTO projectDTO, @PathVariable Long projectId){
                User user = authUtil.loggedInUser();
                ProjectDTO updatedProject = projectService.updateProject(projectDTO, projectId, user);
                return new ResponseEntity<>(updatedProject, HttpStatus.ACCEPTED);
        }

}
