package com.RealState.Project.Service.Impl;

import com.RealState.Project.DTO.Admin.RevenueAnalyticsDTO;
import com.RealState.Project.DTO.RevenuePointDTO;
import com.RealState.Project.Entity.Type.AnalyticsType;
import com.RealState.Project.Entity.Type.Listing_type;
import com.RealState.Project.Entity.User;
import com.RealState.Project.Repository.TransactionRepository;
import com.RealState.Project.Service.AnalyticsService;
import com.RealState.Project.Strategy.Revenue.RevenueStrategy;
import com.RealState.Project.Strategy.Revenue.RevenueStrategyFactory;
import com.RealState.Project.Utils.SecurityUtil;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticServiceImpl implements AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final RevenueStrategyFactory strategyFactory;
    private final SecurityUtil securityUtil;

    @Override
    public RevenueAnalyticsDTO getRevenueAnalytics(AnalyticsType type) {

        User user = securityUtil.getCurrentUser();

        LocalDate now = LocalDate.now();
        LocalDate from;

        switch (type){

            case WEEKLY:
                from = now.minusWeeks(10);
                break;

            case MONTHLY:
                from = now.minusMonths(10);
                break;

            case YEARLY:
                from = now.minusYears(10);
                break;

            default:
                from = now.minusMonths(6);
        }

        RevenueStrategy strategy =
                strategyFactory.getStrategy(
                        user.getUserProfile().getUserType()
                );

        List<RevenuePointDTO> chart =
                strategy.generateRevenue(
                        type,
                        from,
                        now,
                        user.getId()
                );

        return RevenueAnalyticsDTO.builder()

                .totalRevenue(transactionRepository.totalRevenue())

                .monthlyRevenue(
                        transactionRepository.revenueAfter(
                                now.minusDays(30)
                        )
                )

                .weeklyRevenue(
                        transactionRepository.revenueAfter(
                                now.minusDays(7)
                        )
                )

                .revenueFromSell(
                        transactionRepository.revenueByListingType(
                                Listing_type.SELL
                        )
                )

                .revenueFromRent(
                        transactionRepository.revenueByListingType(
                                Listing_type.RENT
                        )
                )

                .chart(chart)

                .build();
    }
}