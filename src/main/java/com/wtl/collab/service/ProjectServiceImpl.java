package com.wtl.collab.service;

import com.wtl.collab.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements  ProjectService{

    @Autowired
    private ProjectRepository projectRepository;


}
