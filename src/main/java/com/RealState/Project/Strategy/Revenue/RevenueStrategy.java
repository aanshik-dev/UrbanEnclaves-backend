package com.RealState.Project.Strategy.Revenue;

import com.RealState.Project.DTO.RevenuePointDTO;
import com.RealState.Project.Entity.Type.AnalyticsType;

import java.time.LocalDate;
import java.util.List;

public interface RevenueStrategy {

    List<RevenuePointDTO> generateRevenue(
            AnalyticsType type,
            LocalDate from,
            LocalDate to,
            Long userId
    );

}