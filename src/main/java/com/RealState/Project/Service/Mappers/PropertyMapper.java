package com.RealState.Project.Service.Mappers;

import com.RealState.Project.DTO.PropertyRequestDTO;
import com.RealState.Project.Entity.Property;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING , uses = {UserMapper.class,OfficeMapper.class})

public interface PropertyMapper {

    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "officeId", ignore = true)
    PropertyRequestDTO toPropertyRequestDTO(Property property);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "office", ignore = true)
    Property toProperty(PropertyRequestDTO propertyRequestDTO);

}
