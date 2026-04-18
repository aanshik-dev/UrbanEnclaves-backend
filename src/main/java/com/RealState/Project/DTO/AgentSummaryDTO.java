package com.RealState.Project.DTO;


import com.RealState.Project.Entity.Type.Status;
import lombok.*;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class AgentSummaryDTO {

    private Long agentId;
    private String name;   //profile name
    private Float totalDeals;
    private Float score;
    private Long phone;
    private Status status;
    private Float rating;

    public AgentSummaryDTO(
            Long agentId,
            String name,
            Integer totalDeals,
            Float score,
            Long phone,
            Status status,
            Float rating
    ){
        this.agentId = agentId;
        this.name = name;
        this.totalDeals = totalDeals != null ? totalDeals.floatValue() : 0;
        this.score = score;
        this.phone = phone;
        this.status = status;
        this.rating = rating;
    }

    public AgentSummaryDTO(
            Long agentId,
            String name,
            Long totalDeals,
            Float score,
            Long phone,
            Status status,
            Float rating
    ){
        this.agentId = agentId;
        this.name = name;
        this.totalDeals = totalDeals != null ? totalDeals.floatValue() : 0;
        this.score = score;
        this.phone = phone;
        this.status = status;
        this.rating = rating;
    }
}