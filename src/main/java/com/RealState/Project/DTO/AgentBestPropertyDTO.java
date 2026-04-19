package com.RealState.Project.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgentBestPropertyDTO {
    private PropertyForOtherTableResponseDTO propertyDetail;
    private ListingForOtherTableResponseDTO listingDetail;
}
