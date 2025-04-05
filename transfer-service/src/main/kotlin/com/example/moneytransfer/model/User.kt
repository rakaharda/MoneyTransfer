package com.example.moneytransfer.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("users")
data class User(val username: String, @Id val userid: UUID, var balance: Float, val token: UUID)
