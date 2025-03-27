package com.artifact.dto;

import com.artifact.constants.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserResponse {

    private Set<Roles> roles;
}
