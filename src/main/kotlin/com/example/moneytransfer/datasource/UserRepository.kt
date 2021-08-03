package com.example.moneytransfer.datasource

import com.example.moneytransfer.model.User
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface UsersRepository : CrudRepository<User, String>
{
    @Query("select * from users")
    fun findUsers(): List<User>

    @Query("select * from users as u where u.userid = :userid")
    fun findUser(@Param("userid")userid: String) : User?

    @Modifying
    @Query("update users as u set u.balance = u.balance + :amount where u.userid = :userid")
    fun updateBalance(@Param("userid") userid: String, @Param("amount") amount: Float)
}