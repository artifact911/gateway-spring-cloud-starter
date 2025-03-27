package com.artifact.mapper;

import com.artifact.constants.Roles;
import com.artifact.dto.UserResponse;
import com.artifact.entity.Role;
import com.artifact.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponse toUserResponse(User user) {
        Set<Roles> roles = user.getRoles().stream()
                .map(Role::getName)
                .map(Roles::valueOf)
                .collect(Collectors.toSet());

        return new UserResponse(roles);
    }
}
