package com.wtl.collab.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "tech_stack")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Tech {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tech_id")
    private Integer id;

//  @ToString.Exclude
    @Enumerated(EnumType.STRING)
    @Column(name = "tech_name")
    private Technology technology;


    public Tech(Technology technology){
        this.technology = technology;
    }


}
