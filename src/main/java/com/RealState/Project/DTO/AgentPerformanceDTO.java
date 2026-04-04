package com.RealState.Project.DTO;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgentPerformanceDTO {

    private Long agentId;

    private String agentName;

    private Long totalDeals;

    private Double totalRevenue;

    private Double totalCommission;

}