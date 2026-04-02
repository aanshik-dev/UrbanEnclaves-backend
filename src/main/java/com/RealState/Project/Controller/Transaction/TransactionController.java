package com.RealState.Project.Controller.Transaction;


import com.RealState.Project.DTO.TransactionRequestDTO;
import com.RealState.Project.Entity.Transaction;
import com.RealState.Project.Service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;


    // POST Transaction
    @PostMapping
    public Transaction createTransaction(@RequestBody TransactionRequestDTO request){

        return transactionService.createTransaction(request);
    }

    // GET All Transactions
    @GetMapping
    public List<Transaction> getAllTransactions(){

        return transactionService.getAllTransactions();
    }


    // GET Transaction by Id
    @GetMapping("/{transactionId}")
    public Transaction getTransactionById(@PathVariable Long transactionId){

        return transactionService.getTransactionById(transactionId);
    }


}