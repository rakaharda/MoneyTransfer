package com.example.moneytransfer.datasource

import com.example.moneytransfer.model.Transfer
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.UUID

interface TransfersRepository : CrudRepository<Transfer, UUID> {
    @Query("select * from transfers")
    fun findTransfers(): List<Transfer>
    @Query("select * from transfers where senderid = :userid or recipientid = :userid")
    fun findTransfers(@Param("userid") userid: UUID): List<Transfer>
    @Query("select count(*) from transfers")
    fun size() : Int
}