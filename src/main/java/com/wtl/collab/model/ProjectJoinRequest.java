package com.wtl.collab.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectJoinRequest {

    private Long projectId;
    private String status;
    private Long userID;
    private Date timeStamp;

    public ProjectJoinRequest(Long projectId){
        this.projectId = projectId;
    }

}
