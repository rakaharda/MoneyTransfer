package com.example.moneytransfer.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("USERS")
data class User(val username: String, @Id val userid: String, var balance: Float, val token: String)
