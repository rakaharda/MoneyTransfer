package com.example.MoneyTransfer

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
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
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.sql.*

@SpringBootApplication
class MoneyTransferApplication


fun main(args: Array<String>) {
	runApplication<MoneyTransferApplication>(*args)
}

@RestController
class TransferResource(val service: TransferService) {
	@Operation(summary = "Returns list of transfers with specified parameters")
	@GetMapping("/transfer")
	fun index(@RequestParam(name = "limit", required = false) limit: Int?,
			  @RequestParam(name = "skip", required = false) skip: Int?,
			  @RequestParam(name = "userid", required = false) userid: String?):
			List<Transfer> = service.findTransfers(limit, skip, userid)
	@Operation(summary = "Makes transfer between accounts")
	@PostMapping("/transfer")
	fun post(@RequestBody transferRequest: TransferRequest){
		service.post(transferRequest)
	}
}

@RestController
class UserResource(val service: UserService) {
	@GetMapping()
	fun index(): List<User> = service.findUsers()
}

@Service
class TransferService(val db: TransfersRepository, val udb: UsersRepository){

	fun findTransfers(limit: Int?, skip: Int?, userid: String?): List<Transfer>{
		val l = limit ?: db.size()
		val s = skip ?: 0
		val u = userid ?: "*"
		return db.findTransfers(l, s, u)
	}

	fun post(transferRequest: TransferRequest){
		val sender = udb.findUser(transferRequest.senderid)
			?:  throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong sender ID")
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
	@Query("select * from transfers where senderid = :userid or recipientid = :userid limit :limitBot, :limitTop")
	fun findTransfers(@Param("limitTop") limit: Int = size(),
					  @Param("limitBot") skip: Int = 0,
					  @Param("userid") userid: String = "*"): List<Transfer>
	@Query("select count(*) from transfers")
	fun size() : Int
}

@Table("USERS")
data class User(val username: String, @Id val userid: String, var balance: Float, val token: String)

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

data class TransferRequest(
	@Schema(description = "UUID of the sender", format="uuid", example = "\"2e250f45-0884-4b75-8fc3-4a645197fd30\"")
	val senderid: String,
	@Schema(description = "Token of the sender", example = "5bb3c7f4-e9fa-490c-b6e6-e46e681838f6")
	val token: String,
	@Schema(description = "UUID of the recipient", format="uuid", example = "\"5717ed3f-a95a-4c38-9e5e-484916e6786e\"")
	val recipientid: String,
	@Schema(description = "Amount of money to transfer", example = "23.5")
	val amount: Float)