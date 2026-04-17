package com.RealState.Project.DTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfficeDashboardDTO {

    private Long totalAgents;
    private Long activeAgents;

    private Long totalProperties;

    private Long totalListings;
    private Long activeListings;
    private Long soldListings;
    private Long rentedListings;

    private Long activeInSell;
    private Long activeInRent;

    private Long totalDeals;

    private Double totalRevenue;
    private Double monthlyRevenue;  
}