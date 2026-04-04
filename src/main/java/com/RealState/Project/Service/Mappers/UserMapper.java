package com.RealState.Project.Service.Mappers;

import com.RealState.Project.DTO.UserRequestDTO;
import com.RealState.Project.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserRequestDTO toUserRequestDTO(User user);

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "providerType",ignore = true)
    @Mapping(target = "providerId",ignore = true)
    @Mapping(target = "password",ignore = true)
    @Mapping(target = "userProfile",ignore = true)
    User toUser(UserRequestDTO userRequestDTO);
}
