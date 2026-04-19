package com.RealState.Project.Controller.PropertyController;

import com.RealState.Project.DTO.PropertyRequestDTO;
import com.RealState.Project.DTO.PropertyResponseDTO;
import com.RealState.Project.Service.PropertyService;
import com.RealState.Project.Utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class UserPropertyController {
    private final PropertyService propertyService;

    @PostMapping
    public PropertyResponseDTO addNewProperty(
            @RequestBody PropertyRequestDTO propertyRequestDTO) {

        return propertyService.addProperty(propertyRequestDTO);
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<PropertyResponseDTO> getPropertyById(
            @PathVariable Long propertyId) {

        PropertyResponseDTO property =
                propertyService.getPropertyById(propertyId);

        return ResponseEntity.ok(property);
    }

    @PutMapping("/{propertyId}")
    public ResponseEntity<PropertyResponseDTO> updateProperty(
            @PathVariable Long propertyId,
            @RequestBody PropertyRequestDTO propertyRequestDTO) {

        return ResponseEntity.ok(
                propertyService.updateProperty(propertyId, propertyRequestDTO)
        );
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<ApiResponse<?>> deleteProperty(@PathVariable Long propertyId) {

        return propertyService.deletePropertyByID(propertyId);

    }

    @GetMapping("/me/properties")
    public ResponseEntity<List<PropertyResponseDTO>> getMyProperties(){

        return ResponseEntity.ok(
                propertyService.getMyProperties()
        );
    }

}
