package com.RealState.Project.Service;

import com.RealState.Project.DTO.TransactionRequestDTO;
import com.RealState.Project.Entity.Transaction;

import java.util.List;

public interface TransactionService {

    Transaction createTransaction(TransactionRequestDTO request);

    List<Transaction> getAllTransactions();

    Transaction getTransactionById(Long id);

    List<Transaction> getUserTransactions(Long userId);

    List<Transaction> getAgentTransactions(Long agentId);
}