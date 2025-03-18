package com.artifact.hello.controller

import com.artifact.hello.controller.dto.Hello
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/hello")
class HelloController {

    @GetMapping
    fun hello() = Hello("Hello World!")
}