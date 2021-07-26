package com.example.MoneyTransfer

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.*
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.boot.test.web.client.TestRestTemplate
import kotlinx.serialization.json.*
import kotlinx.serialization.*
import org.springframework.web.util.UriComponentsBuilder

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MoneyTransferApplicationTests @Autowired constructor(
	val restTemplate: TestRestTemplate,
	val usersRepository: UsersRepository,
	val transfersRepository: TransfersRepository) {

	@Test
	fun `assert status code for transfer`() {
		val entity = restTemplate.getForEntity<List<Transfer>>("/transfer")
		assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
	}

	@Test
	fun `get with limit param`() {
		for(i in 1..10)
		{
			val users = usersRepository.findUsers()
			val transferRequest: TransferRequest = TransferRequest(
				users[users.lastIndex].userid,
				users[users.lastIndex].token,
				users[users.lastIndex - 1].userid,
				0.01f
			)
			restTemplate.postForEntity<String>("/transfer", transferRequest)
		}
		val limit = 5
		val entity = restTemplate.getForEntity<List<Transfer>>("/transfer?limit=$limit")
		assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
		assertThat(entity.body!!.size).isEqualTo(limit)
	}

	@Test
	fun `when no param then returns all`() {
		for(i in 1..10)
		{
			val users = usersRepository.findUsers()
			val transferRequest: TransferRequest = TransferRequest(
				users[users.lastIndex].userid,
				users[users.lastIndex].token,
				users[users.lastIndex - 1].userid,
				0.01f
			)
			restTemplate.postForEntity<String>("/transfer", transferRequest)
		}
		val entity = restTemplate.getForEntity<List<Transfer>>("/transfer")
		assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
		assertThat(entity.body!!.size).isEqualTo(transfersRepository.size())
	}

	@Test
	fun `get with skip param`() {
		val skip = 2
		val n = 10
		for(i in 1..n)
		{
			val users = usersRepository.findUsers()
			val transferRequest: TransferRequest = TransferRequest(
				users[users.lastIndex].userid,
				users[users.lastIndex].token,
				users[users.lastIndex - 1].userid,
				0.01f
			)
			restTemplate.postForEntity<String>("/transfer", transferRequest)
		}
		val entity = restTemplate.getForEntity<List<Transfer>>("/transfer?skip=$skip")
		assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
		assertThat(entity.body!!.size).isEqualTo(n - skip)
	}

	@Test
	fun `get with userid`() {
		for(i in 1..10)
		{
			val users = usersRepository.findUsers()
			val transferRequest: TransferRequest = TransferRequest(
				users[0].userid,
				users[0].token,
				users[users.lastIndex - 1].userid,
				0.01f
			)
			restTemplate.postForEntity<String>("/transfer", transferRequest)
		}
		val user = usersRepository.findUsers()[0]
		var count = 0
		val transfers = transfersRepository.findTransfers()
		for(t in transfers) if (user.userid == t.senderid || user.userid == t.recipientid) count++
		val entity = restTemplate.getForEntity<List<Transfer>>("/transfer?userid=$user")
		assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
		assertThat(entity.body!!.size).isEqualTo(count)
	}

	@Test
	fun `when invalid userid returns 0`() {
		for(i in 1..10)
		{
			val users = usersRepository.findUsers()
			val transferRequest: TransferRequest = TransferRequest(
				users[0].userid,
				users[0].token,
				users[users.lastIndex - 1].userid,
				0.01f
			)
			restTemplate.postForEntity<String>("/transfer", transferRequest)
		}
		val user = usersRepository.findUsers()[0]
		var count = 0
		val transfers = transfersRepository.findTransfers()
		for(t in transfers) if (user.userid == t.senderid || user.userid == t.recipientid) count++
		val entity = restTemplate.getForEntity<List<Transfer>>("/transfer?userid=$user")
		assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
		assertThat(entity.body!!.size).isEqualTo(0)
	}

	@Test
	fun `find user by userid`() {
		val entity = restTemplate.exchange<List<User>>("/", HttpMethod.GET)
		for (user in entity.body!!) {
			val foundUser = user.userid?.let { usersRepository.findUser(it) }
			assertThat(foundUser).isEqualTo(user)
		}
	}

	@Test
	fun `when correct request then return OK, tests last 2 users in db, expecting last user balance not 0`() {
		val users = usersRepository.findUsers()
		val transferRequest: TransferRequest = TransferRequest(
			users[users.lastIndex].userid,
			users[users.lastIndex].token,
			users[users.lastIndex - 1].userid,
			0.01f
		)
		val responseEntity = restTemplate.postForEntity<String>("/transfer", transferRequest)
		assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
	}

	@Test
	fun `when invalid senderid then return BAD_REQUEST`() {
		val users = usersRepository.findUsers()
		val transferRequest: TransferRequest = TransferRequest(
			"somerandomtext",
			users[users.lastIndex].token,
			users[users.lastIndex - 1].userid,
			0.01f
		)
		val responseEntity = restTemplate.postForEntity<String>("/transfer", transferRequest)
		assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `when invalid recipientid then return BAD_REQUEST`() {
		val users = usersRepository.findUsers()
		val transferRequest: TransferRequest = TransferRequest(
			users[users.lastIndex].userid,
			users[users.lastIndex].token,
			"",
			0.01f
		)
		val responseEntity = restTemplate.postForEntity<String>("/transfer", transferRequest)
		assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `when senderid equals recipientid then return BAD_REQUEST`() {
		val users = usersRepository.findUsers()
		val transferRequest: TransferRequest = TransferRequest(
			users[users.lastIndex].userid,
			users[users.lastIndex].token,
			users[users.lastIndex].userid,
			0.01f
		)
		val responseEntity = restTemplate.postForEntity<String>("/transfer", transferRequest)
		assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `when invalid token then return BAD_REQUEST`() {
		val users = usersRepository.findUsers()
		val transferRequest: TransferRequest = TransferRequest(
			users[users.lastIndex].userid,
			"1234567890",
			users[users.lastIndex - 1].userid,
			0.01f
		)
		val responseEntity = restTemplate.postForEntity<String>("/transfer", transferRequest)
		assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `when amount is negative then return BAD_REQUEST`() {
		val users = usersRepository.findUsers()
		val transferRequest: TransferRequest = TransferRequest(
			users[users.lastIndex].userid,
			users[users.lastIndex].token,
			users[users.lastIndex - 1].userid,
			-1f
		)
		val responseEntity = restTemplate.postForEntity<String>("/transfer", transferRequest)
		assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `when amount greater than balance then return BAD_REQUEST`() {
		val users = usersRepository.findUsers()
		val transferRequest: TransferRequest = TransferRequest(
			users[users.lastIndex].userid,
			users[users.lastIndex].token,
			users[users.lastIndex - 1].userid,
			users[users.lastIndex].balance + 0.01f
		)
		val responseEntity = restTemplate.postForEntity<String>("/transfer", transferRequest)
		assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `test`(){
		val obj = restTemplate.exchange<String>("/transfer", HttpMethod.GET)
	}
}
