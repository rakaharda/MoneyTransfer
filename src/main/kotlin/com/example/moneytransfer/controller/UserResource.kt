package com.example.moneytransfer.controller

import com.example.moneytransfer.model.User
import com.example.moneytransfer.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserResource(val service: UserService) {
    @GetMapping
    fun index(): List<User> = service.findUsers()
}