package com.example.moneytransfer.datasource

import com.example.moneytransfer.model.Transfer
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface TransfersRepository : CrudRepository<Transfer, String> {
    @Query("select * from transfers limit :limitBot, :limitTop")
    fun findTransfers(@Param("limitTop") limit: Int = size(),
                      @Param("limitBot") skip: Int = 0): List<Transfer>
    @Query("select * from transfers where senderid = :userid or recipientid = :userid limit :limitBot, :limitTop")
    fun findTransfers(@Param("limitTop") limit: Int = size(),
                      @Param("limitBot") skip: Int = 0,
                      @Param("userid") userid: String): List<Transfer>
    @Query("select count(*) from transfers")
    fun size() : Int
}