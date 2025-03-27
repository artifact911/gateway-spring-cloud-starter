package com.artifact.controller;

import com.artifact.constants.Status;
import com.artifact.dto.UserResponse;
import com.artifact.dto.signup.SignUpRequest;
import com.artifact.dto.signup.SignUpResponse;
import com.artifact.exception.UserException;
import com.artifact.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserById(@RequestParam("id") Long id) {
        return userService.findById(id);
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) {
        try {
            userService.save(signUpRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new SignUpResponse(Status.SUCCESS));
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new SignUpResponse(Status.FAILURE, e.getMessage()));
        }
    }
}
