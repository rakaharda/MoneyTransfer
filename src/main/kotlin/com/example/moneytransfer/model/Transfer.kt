package com.example.moneytransfer.model

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.sql.Timestamp

@Table("TRANSFERS")
data class Transfer(
    @Schema(description = "UUID of the transfer", format="uuid", example = "\"1c864c0a-9a19-41e7-b014-c328be86e9d7\"")
    @Id val transferid: String?,
    @Schema(description = "Timestamp of the transfer, GMT", example = "2021-07-26T07:12:27.721+00:00")
    var transferdate: Timestamp?,
    @Schema(description = "UUID of the sender", format="uuid", example = "\"b9c998b8-6943-4c7d-8bcd-72a100198186\"")
    var senderid: String,
    @Schema(description = "UUID of the recipient", format="uuid", example = "\"066ed1db-f2d5-43d1-b0a1-d602b217aab4\"")
    var recipientid: String,
    @Schema(description = "Transferred amount of money", example = "5.45")
    var amount: Float)