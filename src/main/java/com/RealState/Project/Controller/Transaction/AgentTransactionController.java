package com.RealState.Project.Controller.Transaction;


import com.RealState.Project.Entity.Transaction;
import com.RealState.Project.Service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentTransactionController {

    private final TransactionService transactionService;

    @GetMapping("/me/transactions")
    public List<Transaction> getAgentTransactions(@RequestParam Long agentId){

        return transactionService.getAgentTransactions(agentId);
    }
}