package com.RealState.Project.DTO;


import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionDTO {

    private Long transactionId;
    private Long amount;
    private LocalDate date;

    // Agent
    private Long agentId;
    private String agentName;

    // Buyer
    private Long buyerId;
    private String buyerName;

    // Seller
    private Long sellerId;
    private String sellerName;

    // Listing
    private Long propertyId;
    private Float listingPrice;
    private String listingType;
    private String city;
}