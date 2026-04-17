package com.RealState.Project.Strategy.Revenue;


import com.RealState.Project.DTO.RevenuePointDTO;
import com.RealState.Project.Entity.Type.AnalyticsType;
import com.RealState.Project.Repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgentRevenueStrategy implements RevenueStrategy {

    private final TransactionRepository transactionRepository;

    @Override
    public List<RevenuePointDTO> generateRevenue(
            AnalyticsType type,
            LocalDate from,
            LocalDate to,
            Long userId
    ) {

        switch (type){

            case WEEKLY:
                return transactionRepository.agentWeeklyRevenue(userId,from,to);

            case MONTHLY:
                return transactionRepository.agentMonthlyRevenue(userId,from,to);

            case YEARLY:
                return transactionRepository.agentYearlyRevenue(userId,from,to);

            default:
                throw new RuntimeException("Invalid type");
        }

    }
}