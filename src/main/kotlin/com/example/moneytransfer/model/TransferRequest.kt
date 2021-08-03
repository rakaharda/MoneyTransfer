package com.example.moneytransfer.model

import io.swagger.v3.oas.annotations.media.Schema

data class TransferRequest(
    @Schema(description = "UUID of the sender", format="uuid", example = "\"2e250f45-0884-4b75-8fc3-4a645197fd30\"")
    val senderid: String,
    @Schema(description = "Token of the sender", example = "5bb3c7f4-e9fa-490c-b6e6-e46e681838f6")
    val token: String,
    @Schema(description = "UUID of the recipient", format="uuid", example = "\"5717ed3f-a95a-4c38-9e5e-484916e6786e\"")
    val recipientid: String,
    @Schema(description = "Amount of money to transfer", example = "23.5")
    var amount: Float)