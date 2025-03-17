package com.wtl.collab.repository;

import com.wtl.collab.model.Tech;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechRepository extends JpaRepository<Tech, Integer> {
}
