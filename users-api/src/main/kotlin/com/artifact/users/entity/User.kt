package com.artifact.users.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
open class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long? = null,

    open val login: String,

    open val password: String,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    open var roles: MutableSet<Role> = mutableSetOf()
)
