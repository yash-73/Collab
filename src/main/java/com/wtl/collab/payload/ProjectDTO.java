package com.wtl.collab.payload;


import com.wtl.collab.model.Tech;
import com.wtl.collab.model.Technology;
import com.wtl.collab.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {

    private String projectName;
    private String description;
    private Set<Technology> techStack = new HashSet<>();
    private String githubRepository;

    @Override
    public String toString() {
        return "ProjectDTO{" +
                "projectName='" + projectName + '\'' +
                ", description='" + description + '\'' +
                ", techStack=" + techStack +
                ", githubRepository='" + githubRepository + '\'' +
                '}';
    }
}
