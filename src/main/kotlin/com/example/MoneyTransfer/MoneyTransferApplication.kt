package com.example.MoneyTransfer

import kotlinx.coroutines.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.sql.*

@SpringBootApplication
class MoneyTransferApplication


fun main(args: Array<String>) {
	runApplication<MoneyTransferApplication>(*args)
}

@RestController
class TransferResource(val service: TransferService) {
	@GetMapping("/transfer")
	fun index(): List<Transfer> = service.findTransfers()

	@PostMapping("/transfer")
	fun post(@RequestBody transfer: Transfer) {
		transfer.transferdate = Timestamp(System.currentTimeMillis())
		service.post(transfer)
	}
}

@Service
class TransferService(val db: TransfersRepository){

	fun findTransfers(): List<Transfer> = db.findTransfers()

	fun post(transfer: Transfer){
		db.save(transfer)
	}
}

interface TransfersRepository : CrudRepository<Transfer, String>{
	@Query("select * from transfers")
	fun findTransfers(): List<Transfer>
}

@Table("MESSAGES")
data class Message(@Id val id: String?, val text: String)

@Table("USERS")
data class User(val username: String, @Id val userid: String?, var balance: Float, val token: String?)

@Table("TRANSFERS")
data class Transfer(@Id val transferid: String?, var transferdate: Timestamp?, val senderid: String, val recipientid: String, val amount: Float)