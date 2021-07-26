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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MoneyTransferApplicationTests @Autowired constructor(
	val restTemplate: TestRestTemplate,
	val usersRepository: UsersRepository,
	val transfersRepository: TransfersRepository) {

	@Test
	fun `Assert status code for transfer`() {
		val entity = restTemplate.getForEntity<List<User>>("/transfer")
		assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
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
	fun `request for Post with two last users, expected that last user balance greater than 0`() {
		val users = usersRepository.findUsers()
		val transferRequest: TransferRequest = TransferRequest(
			users[users.lastIndex].userid,
			users[users.lastIndex].token,
			users[users.lastIndex - 1].userid,
			users[users.lastIndex].balance
		)
		val responseEntity = restTemplate.postForEntity<String>("/transfer", transferRequest)
		assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
	}

	@Test
	fun `bad request with bad senderid`() {
		val users = usersRepository.findUsers()
		val transferRequest: TransferRequest = TransferRequest(
			"somerandomtext",
			users[users.lastIndex].token,
			users[users.lastIndex - 1].userid,
			users[users.lastIndex].balance
		)
		val responseEntity = restTemplate.postForEntity<String>("/transfer", transferRequest)
		assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `bad request with bad recipientid`() {
		val users = usersRepository.findUsers()
		val transferRequest: TransferRequest = TransferRequest(
			users[users.lastIndex].userid,
			users[users.lastIndex].token,
			"",
			users[users.lastIndex].balance
		)
		val responseEntity = restTemplate.postForEntity<String>("/transfer", transferRequest)
		assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `bad request with equal senderid and recipientid`() {
		val users = usersRepository.findUsers()
		val transferRequest: TransferRequest = TransferRequest(
			users[users.lastIndex].userid,
			users[users.lastIndex].token,
			users[users.lastIndex].userid,
			users[users.lastIndex].balance
		)
		val responseEntity = restTemplate.postForEntity<String>("/transfer", transferRequest)
		assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `bad request with wrong token`() {
		val users = usersRepository.findUsers()
		val transferRequest: TransferRequest = TransferRequest(
			users[users.lastIndex].userid,
			users[users.lastIndex].token,
			users[users.lastIndex - 1].userid,
			users[users.lastIndex].balance
		)
		val responseEntity = restTemplate.postForEntity<String>("/transfer", transferRequest)
		assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `bad request with negative amount`() {
		val users = usersRepository.findUsers()
		val transferRequest: TransferRequest = TransferRequest(
			users[users.lastIndex].userid,
			users[users.lastIndex].token,
			users[users.lastIndex - 1].userid,
			users[users.lastIndex].balance * -1f
		)
		val responseEntity = restTemplate.postForEntity<String>("/transfer", transferRequest)
		assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
	}

	@Test
	fun `bad request with amount bigger than balance`() {
		val users = usersRepository.findUsers()
		val transferRequest: TransferRequest = TransferRequest(
			users[users.lastIndex].userid,
			users[users.lastIndex].token,
			users[users.lastIndex - 1].userid,
			users[users.lastIndex].balance + 1f
		)
		val responseEntity = restTemplate.postForEntity<String>("/transfer", transferRequest)
		assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
	}
}

@DataJpaTest
class MoneyTransferRepositoriesTests @Autowired constructor(
	val entityManager: TestEntityManager,
	val usersRepository: UsersRepository,
	val transfersRepository: TransfersRepository){

	@Test
	fun `find user by userid`()
	{

	}
}