package com.wtl.collab.payload;

import com.wtl.collab.model.AppRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDTO {
    private String username;
    private String email;
    private String password;
    private Set<AppRole> roles = new HashSet<>();
}
