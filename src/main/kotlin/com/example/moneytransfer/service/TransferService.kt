package com.example.moneytransfer.service

import com.example.moneytransfer.datasource.TransfersRepository
import com.example.moneytransfer.datasource.UsersRepository
import com.example.moneytransfer.model.Transfer
import com.example.moneytransfer.model.TransferRequest
import com.example.moneytransfer.round
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.sql.Timestamp

@Service
class TransferService(val db: TransfersRepository, val udb: UsersRepository){

    fun findTransfers(limit: Int?, skip: Int?, userid: String?): List<Transfer>{
        val l = limit ?: db.size()
        val s = skip ?: 0
        return if(userid != null)
            db.findTransfers(l, s, userid)
        else
            db.findTransfers(l, s)
    }

    @Transactional
    fun post(transferRequest: TransferRequest){
        val sender = udb.findUser(transferRequest.senderid)
        transferRequest.amount = transferRequest.amount.round(2)
        when {
            sender == null ->
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong sender ID")
            sender.token != transferRequest.token ->
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong token")
            udb.findUser(transferRequest.recipientid) == null ->
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't find recipient ID")
            transferRequest.recipientid == transferRequest.senderid ->
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't transfer money to your own account")
            sender.balance < transferRequest.amount ->
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds")
            transferRequest.amount <= 0 ->
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid amount")
        }
        udb.updateBalance(transferRequest.senderid, transferRequest.amount * -1)
        udb.updateBalance(transferRequest.recipientid, transferRequest.amount)
        val transfer = Transfer(
            null,
            Timestamp(System.currentTimeMillis()),
            transferRequest.senderid,
            transferRequest.recipientid,
            transferRequest.amount)

        db.save(transfer)
    }
}