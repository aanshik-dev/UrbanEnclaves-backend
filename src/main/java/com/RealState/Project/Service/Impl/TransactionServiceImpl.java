package com.RealState.Project.Service.Impl;

import com.RealState.Project.DTO.TransactionDTO;
import com.RealState.Project.DTO.TransactionRequestDTO;
import com.RealState.Project.Entity.Agent;
import com.RealState.Project.Entity.ListingToken;
import com.RealState.Project.Entity.Transaction;
import com.RealState.Project.Entity.User;
import com.RealState.Project.Mapper.TransactionMapper;
import com.RealState.Project.Repository.AgentRepository;
import com.RealState.Project.Repository.ListingTokenRepository;
import com.RealState.Project.Repository.TransactionRepository;
import com.RealState.Project.Repository.UserRepository;
import com.RealState.Project.Service.TransactionService;
import com.RealState.Project.Strategy.TransactionAccessStrategy;
import com.RealState.Project.Strategy.TransactionStrategyFactory;
import com.RealState.Project.Utils.SecurityUtil;
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
    public final SecurityUtil securityUtil;
    private final TransactionStrategyFactory strategyFactory;

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
    public List<TransactionDTO> getTransactions() {
        User user = securityUtil.getCurrentUser();

        return strategyFactory
                .getStrategy(user.getUserProfile().getUserType())
                .getTransactions(user)
                .stream()
                .map(TransactionMapper::toDTO)
                .toList();
    }


    @Override
    public TransactionDTO getTransactionById(Long id) {

        User user = securityUtil.getCurrentUser();

        Transaction transaction =
                transactionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Transaction not found"));

        TransactionAccessStrategy strategy =
                strategyFactory.getStrategy(
                        user.getUserProfile().getUserType()
                );

        if(!strategy.canAccess(transaction,user)){

            throw new RuntimeException("Access Denied");
        }

        return TransactionMapper.toDTO(transaction);
    }
}
