package com.artifact.users.controller

import com.artifact.users.entity.User
import com.artifact.users.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @GetMapping("/{id}")
    fun findUserById(@PathVariable id: String): User? = userService.findById(id.toLongOrNull())
}