package com.RealState.Project.DTO.Admin;

import com.RealState.Project.DTO.AgentSummaryDTO;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDashboardDTO {

    private Long totalUsers;
    private Long totalAgents;
    private Long totalProperties;
    private Long activeListings;
    private Long soldProperties;

    private Double totalRevenue;

    private List<AgentSummaryDTO> agents;
    private Long pendingQueries;
}