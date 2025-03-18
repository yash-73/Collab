package com.wtl.collab.service;

import com.wtl.collab.exception.ResourceNotFound;
import com.wtl.collab.model.*;
import com.wtl.collab.payload.ProjectDTO;
import com.wtl.collab.repository.ProjectRepository;
import com.wtl.collab.repository.TechRepository;
import com.wtl.collab.repository.UserRepository;
import com.wtl.collab.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements  ProjectService{

    private ProjectRepository projectRepository;

    private ModelMapper modelMapper;

    private TechRepository techRepository;

    private UserRepository userRepository;


    public ProjectServiceImpl(ProjectRepository projectRepository, AuthUtil authUtil,
                              ModelMapper modelMapper, TechRepository techRepository,
                              UserRepository userRepository){
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
        this.techRepository = techRepository;
        this.userRepository = userRepository;

    }


    @Override
    @Transactional
    public ProjectDTO createNewProject(ProjectDTO projectDTO, User user) {

        Project project  = new Project();
        project.setProjectName(projectDTO.getProjectName()); //projectName
        project.setCreator(user);//creator
        project.setProjectStatus(ProjectStatus.OPEN); //projectStatus
        project.setDescription(projectDTO.getDescription()); //description
        project.setGithubRepository(projectDTO.getGithubRepository()); //githubRepository

        Set<User> members = new HashSet<>();
        members.add(user);
        project.setMembers(members); //members


        Set<Tech> techStack = new HashSet<>();
        projectDTO.getTechStack().forEach(
                tech -> {
                    Tech foundTech = techRepository.findByTechName(tech);
                    if (foundTech == null) throw new ResourceNotFound("Tech not found: "+tech);
                    else techStack.add(foundTech);
                }
        );
        project.setTechStack(techStack); //techStack


        Project savedProject = projectRepository.save(project); //Save Project

        user.getCreatedProjects().add(savedProject);
        user.getProjects().add(savedProject);
//        userRepository.save(user);
        ProjectDTO savedProjectDTO = modelMapper.map(savedProject, ProjectDTO.class);
        savedProjectDTO.setTechStack(projectDTO.getTechStack());
        return savedProjectDTO;

    }


}
