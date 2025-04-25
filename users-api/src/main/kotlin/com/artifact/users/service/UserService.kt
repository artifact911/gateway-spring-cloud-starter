package com.artifact.users.service

import com.artifact.users.entity.User
import com.artifact.users.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun findById(id: Long?): User? = id?.let { userRepository.findById(it).orElse(null) }
}