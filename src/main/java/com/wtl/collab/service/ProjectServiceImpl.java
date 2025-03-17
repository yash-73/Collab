package com.wtl.collab.service;

import com.wtl.collab.model.*;
import com.wtl.collab.payload.ProjectDTO;
import com.wtl.collab.repository.ProjectRepository;
import com.wtl.collab.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class ProjectServiceImpl implements  ProjectService{

    private ProjectRepository projectRepository;

    private ModelMapper modelMapper;


    public ProjectServiceImpl(ProjectRepository projectRepository, AuthUtil authUtil, ModelMapper modelMapper){
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;

    }


    @Override
    public ProjectDTO createNewProject(ProjectDTO projectDTO, User user) {

        System.out.println(projectDTO.toString());
        Project project = modelMapper.map(projectDTO, Project.class);


        project.setCreator(user); //creator
        project.setProjectName(projectDTO.getProjectName()); //projectName
        project.setDescription(projectDTO.getDescription()); //description
        project.setProjectStatus(ProjectStatus.OPEN);
        project.setCreatedAt(new Date()); //createdDate
        Set<User> members = new HashSet<>();
        members.add(user);
        project.setMembers(members); //Members
        project.setGithubRepository(projectDTO.getGithubRepository()); //githubRepository

        Set<Technology> techStack = projectDTO.getTechStack();


        Project savedProject = projectRepository.save(project);
       return modelMapper.map(savedProject, ProjectDTO.class);
    }


}
