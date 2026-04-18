package com.RealState.Project.Service;

import com.RealState.Project.DTO.Admin.*;
import com.RealState.Project.DTO.AgentSummaryDTO;

import java.util.List;

public interface AdminService {

    AdminDashboardDTO getDashboard();

    void activateAgent(Long agentId);

    void deactivateAgent(Long agentId);

    QueryResponseDTO runQuery(String sql);

    RevenueAnalyticsDTO getRevenueAnalytics();

    TransactionAnalyticsDTO getDealsAnalytics();

    List<AgentSummaryDTO> getTopAgents();
}