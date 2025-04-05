package com.example.moneytransfer.controller

import com.example.moneytransfer.model.User
import com.example.moneytransfer.model.UserCreationRequest
import com.example.moneytransfer.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping



@RestController
@RequestMapping("/user")
class UserResource(val service: UserService) {
    @GetMapping
    fun index(): List<User> = service.findUsers()

    @PostMapping("/adduser")
    fun post(@RequestBody user: User) = service.addUser(user)

    @PostMapping("/adduserauto")
    fun post(@RequestBody user: UserCreationRequest) = service.addUserAuto(user.username, user.balance)
}