package com.RealState.Project.Service;

import com.RealState.Project.DTO.PropertyRequestDTO;
import com.RealState.Project.DTO.PropertyResponseDTO;
import com.RealState.Project.Utils.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PropertyService {
    PropertyResponseDTO addProperty(PropertyRequestDTO request) ;

    PropertyResponseDTO updateProperty(Long pid, PropertyRequestDTO request);

    ResponseEntity<ApiResponse<?>> deletePropertyByID(Long id);

    // get property corresponding to ID
    PropertyResponseDTO getPropertyById(Long id);

    List<PropertyResponseDTO> getMyProperties();
}
