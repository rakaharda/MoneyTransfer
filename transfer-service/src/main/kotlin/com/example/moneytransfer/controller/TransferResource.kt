package com.example.moneytransfer.controller

import com.example.moneytransfer.model.Transfer
import com.example.moneytransfer.model.TransferRequest
import com.example.moneytransfer.service.TransferService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
class TransferResource(val service: TransferService) {
    @Operation(summary = "Returns list of transfers with specified parameters")
    @GetMapping("/transfer")
    fun index(@RequestParam(name = "limit", required = false) limit: Int?,
              @RequestParam(name = "skip", required = false) skip: Int?,
              @RequestParam(name = "userid", required = false) userid: UUID?):
            List<Transfer> = service.findTransfers(limit, skip, userid)
    @Operation(summary = "Makes transfer between accounts")
    @PostMapping("/transfer")
    fun post(@RequestBody transferRequest: TransferRequest){
        service.post(transferRequest)
    }
}