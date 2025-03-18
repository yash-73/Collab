package com.wtl.collab.service;

import com.wtl.collab.model.Tech;
import com.wtl.collab.repository.TechRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TechServiceImpl implements  TechService{

    private TechRepository techRepository;


    public TechServiceImpl(TechRepository techRepository){
        this.techRepository = techRepository;
    }

    @Override
    public Set<String> addTech(Set<String> technologies) {
                    Set<Tech> techSet = technologies.stream().map(
                            technology -> {
                                Tech tech = techRepository.findByTechName(technology);
                                if(tech == null){
                                    Tech newTech = new Tech(technology);
                                    techRepository.save(newTech);
                                    tech = techRepository.findByTechName(technology);
                                }
                                return tech;
                            }
                    ).collect(Collectors.toSet());

                    return techSet.stream().map(Tech::getTechName).collect(Collectors.toSet());

    }
}
