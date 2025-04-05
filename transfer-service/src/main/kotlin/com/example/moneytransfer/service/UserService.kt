package com.example.moneytransfer.service

import com.example.moneytransfer.datasource.UsersRepository
import com.example.moneytransfer.model.User
import org.springframework.stereotype.Service

@Service
class UserService(val db: UsersRepository){

    fun findUsers(): List<User> = db.findUsers()
    fun addUser(user: User): User {
        db.addUser(user.userid, user.username, user.balance, user.token)
        return user
    }

    fun addUserAuto(name: String, balance: Float): User {
        val user = User(name, java.util.UUID.randomUUID(), balance, java.util.UUID.randomUUID())
        db.addUserAuto(user.username, user.balance)
        return user
    }

}