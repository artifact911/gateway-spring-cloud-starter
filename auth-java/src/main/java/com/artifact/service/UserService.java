package com.artifact.service;

import com.artifact.constants.Roles;
import com.artifact.dto.UserResponse;
import com.artifact.dto.signup.SignUpRequest;
import com.artifact.entity.Role;
import com.artifact.entity.User;
import com.artifact.exception.UserDBException;
import com.artifact.mapper.UserMapper;
import com.artifact.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    public UserResponse findById(Long id) {
        return UserMapper.toUserResponse(
                userRepository.findById(id)
                        .orElseThrow(() -> new UserDBException("User not found")));
    }

    public List<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public UserResponse save(SignUpRequest signUpRequest) {
        List<User> usersByLogin = findByLogin(signUpRequest.getLogin());

        if (usersByLogin.isEmpty()) {
            Role userRole = roleService.findByName(Roles.USER.name());

            User saved = userRepository.save(User.builder()
                    .login(signUpRequest.getLogin())
                    .password(signUpRequest.getPassword())
                    .roles(Set.of(userRole))
                    .build());
            return UserMapper.toUserResponse(saved);
        } else {
            throw new UserDBException(String.format("Пользователь %s уже зарегистрирован", signUpRequest.getLogin()));
        }
    }
}
