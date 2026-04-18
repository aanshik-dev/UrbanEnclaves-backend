package com.RealState.Project.Service;

import com.RealState.Project.DTO.*;
import com.RealState.Project.DTO.Admin.TopAgentDTO;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface OfficeService {

    OfficeDashboardDTO getDashboard();

    List<AgentSummaryDTO> getAgents();

    List<PropertyDTO> getProperties();

    List<ListingDTO> getListings();

    List<AgentSummaryDTO> getTopAgents();
}