package com.RealState.Project.Service.Impl;

import com.RealState.Project.DTO.PropertyRequestDTO;
import com.RealState.Project.Entity.Office;
import com.RealState.Project.Entity.Property;
import com.RealState.Project.Entity.User;
import com.RealState.Project.Repository.OfficeRepository;
import com.RealState.Project.Repository.PropertyRepository;
import com.RealState.Project.Repository.UserRepository;
import com.RealState.Project.Service.Mappers.PropertyMapper;
import com.RealState.Project.Service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {
    public final PropertyRepository propertyRepository;
    public final OfficeRepository officeRepository;
    public final UserRepository userRepository;
    public final PropertyMapper propertyMapper;

    @Override
    public PropertyRequestDTO addProperty(PropertyRequestDTO request){
        Office office = officeRepository.findById(request.getOfficeId())
                .orElseThrow(() -> new RuntimeException("Office not found"));

        User owner=userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Property property=Property.builder()
                .type(request.getType())
                .houseNo(request.getHouseNo())
                .locality(request.getLocality())
                .BHK(request.getBHK())
                .city(request.getCity())
                .area(request.getArea())
                .size(request.getSize())
                .year_built(request.getYear_built())
                .pin(request.getPin())
                .office(office)
                .owner(owner)
                .build();

        propertyRepository.save(property);

        return request;
    }

    @Override
    public PropertyRequestDTO updateProperty(Long pid, PropertyRequestDTO request) {

        Property property = propertyRepository.findById(pid)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        Office office = officeRepository.findById(request.getOfficeId())
                .orElseThrow(() -> new RuntimeException("Office not found"));

        User owner=userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        property.setType(request.getType());
        property.setHouseNo(request.getHouseNo());
        property.setLocality(request.getLocality());
        property.setBHK(request.getBHK());
        property.setCity(request.getCity());
        property.setArea(request.getArea());
        property.setSize(request.getSize());
        property.setYear_built(request.getYear_built());
        property.setPin(request.getPin());
        property.setOffice(office);
        property.setOwner(owner);

        propertyRepository.save(property);
        return request;
    }

    @Override
    public void deletePropertyByID(Long id) {
        Property property=propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        propertyRepository.delete(property);

    }

    // get property corresponding to ID
    @Override
    public PropertyRequestDTO getPropertyById(Long id){
        Property property=propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        PropertyRequestDTO propertyRequestDTO=propertyMapper.toPropertyRequestDTO(property);

        User user = property.getOwner();
        Office office = property.getOffice();

        propertyRequestDTO.setOwnerId(user.getId());
        propertyRequestDTO.setOfficeId((long) office.getId());

        return propertyRequestDTO;
    }
}
