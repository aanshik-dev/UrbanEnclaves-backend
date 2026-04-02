package com.RealState.Project.Controller.Transaction;

import com.RealState.Project.Entity.Transaction;
import com.RealState.Project.Service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserTransactionController {

    private final TransactionService transactionService;


    @GetMapping("/me/transactions")
    public List<Transaction> getUserTransactions(@RequestParam Long userId){

        return transactionService.getUserTransactions(userId);
    }
}