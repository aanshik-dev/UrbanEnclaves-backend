package com.RealState.Project.Controller;

import com.RealState.Project.DTO.AgentDashboardDTO;
import com.RealState.Project.DTO.AgentPerformanceDTO;
import com.RealState.Project.DTO.ListingTokenResponseDTO;
import com.RealState.Project.Service.AgentService;
import com.RealState.Project.Service.ListingPropertyServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;
    private final ListingPropertyServices listingPropertyServices;


    @GetMapping("/me/dashboard")
    public AgentDashboardDTO getDashboard(){

        return agentService.getMyDashboard();
    }


    @GetMapping("/me/performance")
    public AgentPerformanceDTO getMyPerformance(){

        return agentService.getMyPerformance();
    }


    @GetMapping("/{agentId}/performance")
    public AgentPerformanceDTO getAgentPerformance(
            @PathVariable Long agentId){

        return agentService.getAgentPerformance(agentId);
    }


    @GetMapping("/performance")
    public List<AgentPerformanceDTO> getAllPerformance(){

        return agentService.getAllPerformance();
    }

    @GetMapping("/listings/available")
    public ResponseEntity<List<ListingTokenResponseDTO>> getAvailableListings(){

        return ResponseEntity.ok(
                listingPropertyServices.getAvailableListingsForAgent()
        );
    }


}