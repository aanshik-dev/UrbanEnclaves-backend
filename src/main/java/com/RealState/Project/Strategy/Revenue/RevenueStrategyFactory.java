package com.RealState.Project.Strategy.Revenue;

import com.RealState.Project.Entity.Type.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RevenueStrategyFactory {

    private final AdminRevenueStrategy admin;
    private final AgentRevenueStrategy agent;
    private final OfficeRevenueStrategy office;

    public RevenueStrategy getStrategy(UserType type){

        switch (type){

            case ADMIN:
                return admin;

            case AGENT:
                return agent;

            case OFFICE:
                return office;

            default:
                throw new RuntimeException("Invalid user type");
        }

    }

}