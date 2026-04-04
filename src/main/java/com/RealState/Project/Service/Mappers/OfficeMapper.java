package com.RealState.Project.Service.Mappers;

import com.RealState.Project.DTO.OfficeRequestDTO;
import com.RealState.Project.Entity.Office;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,uses = {UserMapper.class})
public interface OfficeMapper {
    OfficeRequestDTO toOfficeRequestDTO(Office office);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "agents",ignore = true)
    @Mapping(target = "properties",ignore = true)
    Office toOffice(OfficeRequestDTO officeRequestDTO);
}
