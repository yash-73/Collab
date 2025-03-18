package com.wtl.collab.model;



import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
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

    @JsonIgnore
    @NotBlank
    @Column(name = "password" , nullable = false)
    private String password;

    @ManyToMany( cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE} , fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_tech_stack",
            joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "tech_id"))
    private Set<Tech> techStack = new HashSet<>();


    @JsonIgnore
    @OneToMany(mappedBy = "creator" , cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<Project> createdProjects = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "members")
    private Set<Project> projects = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();



    public User(String username, String email, String password){
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", techStack=" + techStack +
                ", roles=" + roles +
                '}';
    }
}
