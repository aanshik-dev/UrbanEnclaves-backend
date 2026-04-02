package com.RealState.Project.Service.Impl;

import com.RealState.Project.DTO.TransactionRequestDTO;
import com.RealState.Project.Entity.Agent;
import com.RealState.Project.Entity.ListingToken;
import com.RealState.Project.Entity.Transaction;
import com.RealState.Project.Entity.User;
import com.RealState.Project.Repository.AgentRepository;
import com.RealState.Project.Repository.ListingTokenRepository;
import com.RealState.Project.Repository.TransactionRepository;
import com.RealState.Project.Repository.UserRepository;
import com.RealState.Project.Service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AgentRepository agentRepository;
    private final UserRepository userRepository;
    private final ListingTokenRepository listingTokenRepository;

    @Override
    public Transaction createTransaction(TransactionRequestDTO request) {

        Agent agent = agentRepository.findById(request.getAgentId())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        User buyer = userRepository.findById(request.getBuyerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ListingToken token = listingTokenRepository.findById(request.getTokenId())
                .orElseThrow(() -> new RuntimeException("Token not found"));

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .mode(request.getMode())
                .agent(agent)
                .buyer(buyer)
                .token(token)
                .build();

        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Override
    public List<Transaction> getUserTransactions(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return transactionRepository.findByBuyer_id(user);
    }

    @Override
    public List<Transaction> getAgentTransactions(Long agentId) {

        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        return transactionRepository.findByAgent_id(agent);
    }
}
