package com.example.moneytransfer.datasource

import com.example.moneytransfer.model.User
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UsersRepository : CrudRepository<User, String>
{
    @Query("select * from users")
    fun findUsers(): List<User>

    @Query("select * from users as u where u.userid = :userid")
    fun findUser(@Param("userid")userid: UUID) : User?

    @Modifying
    @Query("update users set balance = balance + :amount where userid = :userid")
    fun updateBalance(@Param("userid") userid: UUID, @Param("amount") amount: Float)

    @Modifying
    @Query("insert into users (userid, username, balance, token) values (:userid, :username, :balance, :token)")
    fun addUser(@Param("userid") userid: UUID, @Param("username") username: String, @Param("balance") balance: Float, @Param("token") token: UUID)
    @Modifying
    @Query("insert into users (username, balance) values (:userid, :username, :balance, :token)")
    fun addUserAuto(@Param("username") username: String, @Param("balance") balance: Float)
}