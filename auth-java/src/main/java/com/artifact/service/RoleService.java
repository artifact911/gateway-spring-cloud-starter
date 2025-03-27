package com.artifact.service;

import com.artifact.entity.Role;
import com.artifact.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public Role findByName(String name) {
       return roleRepository.findByName(name).stream().findFirst().orElseThrow(RuntimeException::new);
    }
}
