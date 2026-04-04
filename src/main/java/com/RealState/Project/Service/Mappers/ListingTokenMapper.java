package com.RealState.Project.Service.Mappers;

import com.RealState.Project.DTO.ListingTokenDTO;
import com.RealState.Project.Entity.ListingToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ListingTokenMapper {

    @Mapping(target = "agent_id",ignore = true)
    @Mapping(target = "pid",ignore = true)
    ListingTokenDTO toDTO(ListingToken listingToken);

    @Mapping(target = "token_id",ignore = true)
    @Mapping(target = "listingDate",ignore = true)
    @Mapping(target = "agent",ignore = true)
    @Mapping(target = "pid",ignore = true)
    ListingToken toEntity(ListingTokenDTO listingTokenDTO);
}
