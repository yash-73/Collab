package com.wtl.collab.service;

import com.wtl.collab.exception.ResourceNotFound;
import com.wtl.collab.model.*;
import com.wtl.collab.payload.APIResponse;
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



    public ProjectServiceImpl(ProjectRepository projectRepository, AuthUtil authUtil,
                              ModelMapper modelMapper, TechRepository techRepository){
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
        this.techRepository = techRepository;


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

        user.getCreatedProjects().add(savedProject); //bi-directional mapping
        user.getProjects().add(savedProject); //bi-directional mapping

        ProjectDTO savedProjectDTO = modelMapper.map(savedProject, ProjectDTO.class);
        savedProjectDTO.setTechStack(projectDTO.getTechStack());
        return savedProjectDTO;

    }

    @Override
    public ProjectDTO updateProject(ProjectDTO projectDTO, Long projectId, User user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new ResourceNotFound("Project not found with projectId "+ projectId));

        if(project.getCreator().getId().equals(user.getId())){
            project.setProjectName(projectDTO.getProjectName()); //projectName
            project.setDescription(projectDTO.getDescription()); // description
            Set<Tech> stack = new HashSet<>();
            projectDTO.getTechStack().forEach(
                    tech -> {
                        Tech technology = techRepository.findByTechName(tech);
                        if (technology == null) throw new ResourceNotFound("Tech not found " + tech);
                        else stack.add(technology);
                    }
            );
            project.setTechStack(stack); //techStack
            project.setGithubRepository(projectDTO.getGithubRepository());

            projectRepository.save(project);
            return projectDTO;
            }

        else {
            throw new RuntimeException("User with userId "+ user.getId() +
                    " is not the creator of the project with projectId "+ projectId);
        }
    }


}
