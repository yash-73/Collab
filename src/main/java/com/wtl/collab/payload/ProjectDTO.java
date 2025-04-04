package com.wtl.collab.payload;


import com.wtl.collab.model.Tech;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {
    private Long projectId;
    private String projectName;
    private String description;
    private Set<String> techStack = new HashSet<>();
    private String githubRepository;

    @Override
    public String toString() {
        return "ProjectDTO{" +
                "projectId='" + projectId + '\''+
                ", projectName='" + projectName + '\'' +
                ", description='" + description + '\'' +
                ", techStack=" + techStack +
                ", githubRepository='" + githubRepository + '\'' +
                '}';
    }

    public ProjectDTO(String projectName, String description, Set<String> techStack, String githubRepository){
        this.projectName = projectName;
        this.githubRepository = githubRepository;
        this.description = description;
        this.techStack = techStack;
    }
}
