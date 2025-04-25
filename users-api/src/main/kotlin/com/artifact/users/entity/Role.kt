package com.artifact.users.entity

import jakarta.persistence.*

@Entity
@Table(name = "roles")
open class Role(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long? = null,

    open val name: String,

    open val description: String
)