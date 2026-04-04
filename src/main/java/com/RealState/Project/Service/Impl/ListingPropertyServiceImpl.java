package com.RealState.Project.Service.Impl;

import com.RealState.Project.DTO.ListingTokenDTO;
import com.RealState.Project.Entity.Agent;
import com.RealState.Project.Entity.ListingToken;
import com.RealState.Project.Entity.Property;
import com.RealState.Project.Entity.Type.Status;
import com.RealState.Project.Repository.AgentRepository;
import com.RealState.Project.Repository.ListingTokenRepository;
import com.RealState.Project.Repository.PropertyRepository;
import com.RealState.Project.Service.ListingPropertyServices;
import com.RealState.Project.Service.Mappers.ListingTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListingPropertyServiceImpl implements ListingPropertyServices {
    private final ListingTokenRepository listingTokenRepository;
    private final AgentRepository agentRepository;
    private final PropertyRepository propertyRepository;
    private final ListingTokenMapper listingTokenMapper;

    @Override
    public List<ListingTokenDTO> getAllListedProperties(){

        List<ListingToken> tokens= listingTokenRepository.findAll();

        List<ListingTokenDTO> tokenDTOs=new ArrayList<ListingTokenDTO>();

        for(ListingToken token:tokens){
            ListingTokenDTO listingTokenDTO=listingTokenMapper.toDTO(token);
            listingTokenDTO.setAgent_id(token.getAgent().getId());
            listingTokenDTO.setPid((long) token.getPid().getId());
            tokenDTOs.add(listingTokenDTO);
        }

        return tokenDTOs;
    }

    @Override
    public ListingTokenDTO createListingToken(ListingTokenDTO listingTokenDTO){
        Agent agent =  agentRepository.findById(listingTokenDTO.getAgent_id())
                .orElseThrow(()->new RuntimeException("Agent not found"));

        Property property= propertyRepository.findById(listingTokenDTO.getPid())
                .orElseThrow(()->new RuntimeException("Property not found"));

        ListingToken listingToken=listingTokenMapper.toEntity(listingTokenDTO);
        listingToken.setPid(property);
        listingToken.setAgent(agent);

        listingTokenRepository.save(listingToken);
        return listingTokenDTO;
    }

    @Override
    public ListingTokenDTO getListedPropertyById(Long listingTokenId){
        ListingToken listingToken = listingTokenRepository.findById(listingTokenId)
                .orElseThrow(()->new RuntimeException("Listing Token not found"));

        ListingTokenDTO listingTokenDTO=listingTokenMapper.toDTO(listingToken);
        listingTokenDTO.setPid((long) listingToken.getPid().getId());
        listingToken.setAgent(listingToken.getAgent());

        return listingTokenDTO;
    }

    @Override
    public ListingTokenDTO updateListingPropertyByID(Long tokenId, ListingTokenDTO listingTokenDTO){
        Agent agent =  agentRepository.findById(listingTokenDTO.getAgent_id())
                .orElseThrow(()->new RuntimeException("Agent not found"));

        Property property= propertyRepository.findById(listingTokenDTO.getPid())
                .orElseThrow(()->new RuntimeException("Property not found"));

        ListingToken listingToken = listingTokenRepository.findById(tokenId)
                .orElseThrow(()->new RuntimeException("Token not found"));

        listingToken.setStatus(listingTokenDTO.getStatus());
        listingToken.setDescription(listingTokenDTO.getDescription());
        listingToken.setPrice(listingTokenDTO.getPrice());
        listingToken.setListingType(listingTokenDTO.getListingType());
        listingToken.setAgent(agent);
        listingToken.setPid(property);
        listingTokenRepository.save(listingToken);

        return listingTokenDTO;
    }

    @Override
    public void deleteListingPropertyById(Long listingTokenId){
        listingTokenRepository.deleteById(listingTokenId);
    }
}
