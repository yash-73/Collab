package com.wtl.collab.model;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotBlank
    @Column(name = "username" , unique = true , nullable = false)
    private String username;

    @NotBlank
    @Email
    @Column(name = "email" , unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(name = "password" , nullable = false)
    private String password;

    @ManyToMany( cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE} , fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_tech_stack",
            joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "tech_id"))
    private Set<Tech> techStack = new HashSet<>();


    @JsonManagedReference
    @OneToMany(mappedBy = "creator" , cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<Project> createdProjects = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "members")
    private Set<Project> projects = new HashSet<>();



    public User(String username, String email, String password){
        this.username = username;
        this.password = password;
        this.email = email;
    }


}
