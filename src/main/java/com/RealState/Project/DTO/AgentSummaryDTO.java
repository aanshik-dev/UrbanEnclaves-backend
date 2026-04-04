package com.RealState.Project.DTO;


import com.RealState.Project.Entity.Type.Status;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentSummaryDTO {

    private Long agentId;
    private String username;
    private Long phone;
    private Status status;
    private Float rating;

}