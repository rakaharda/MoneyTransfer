package com.example.MoneyTransfer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
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
	fun post(@RequestBody transferRequest: TransferRequest){
		service.post(transferRequest)
	}
}

@RestController
class UserResource(val service: UserService) {
	@GetMapping()
	fun index(): List<User> = service.findUsers()
	@PostMapping()
	fun post(@RequestBody user: User){
		service.post(user)
	}
}

@Service
class TransferService(val db: TransfersRepository, val udb: UsersRepository){

	fun findTransfers(): List<Transfer> = db.findTransfers()

	fun post(transferRequest: TransferRequest){
		val sender = udb.findUser(transferRequest.senderid)
			?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong sender ID")
		if(sender.token != transferRequest.token)
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong token")
		udb.findUser(transferRequest.recipientid)
			?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't find recipient ID")
		if(transferRequest.recipientid == transferRequest.senderid)
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't transfer money to your own account")
		if(sender.balance < transferRequest.amount || transferRequest.amount <= 0)
			throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds")
		udb.updateBalance(transferRequest.senderid, transferRequest.amount * -1)
		udb.updateBalance(transferRequest.recipientid, transferRequest.amount)
		val transfer: Transfer = Transfer(
			null,
			Timestamp(System.currentTimeMillis()),
			transferRequest.senderid,
			transferRequest.recipientid,
			transferRequest.amount)

			db.save(transfer)
	}
}

@Service
class UserService(val db: UsersRepository){

	fun findUsers(): List<User> = db.findUsers()

	fun post(user: User){
		db.save(user)
	}
}

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

interface TransfersRepository : CrudRepository<Transfer, String>{
	@Query("select * from transfers")
	fun findTransfers(): List<Transfer>
}

@Table("USERS")
data class User(val username: String, @Id val userid: String?, var balance: Float, val token: String?)

@Table("TRANSFERS")
data class Transfer(@Id val transferid: String?, var transferdate: Timestamp?, var senderid: String, var recipientid: String, var amount: Float)

data class TransferRequest(val senderid: String, val token: String, val recipientid: String, val amount: Float)