package com.artifact.users.repository

import com.artifact.users.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long>