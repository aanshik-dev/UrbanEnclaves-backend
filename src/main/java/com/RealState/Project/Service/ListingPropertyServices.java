package com.RealState.Project.Service;

import com.RealState.Project.DTO.ListingTokenDTO;
import com.RealState.Project.DTO.ListingTokenRequestDTO;
import com.RealState.Project.DTO.ListingTokenResponseDTO;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ListingPropertyServices {

    List<ListingTokenResponseDTO> getAllListedProperties();
    ListingTokenResponseDTO createListingToken(ListingTokenRequestDTO listingTokenDTO);
    ListingTokenResponseDTO getListedPropertyById(Long listingTokenId);
    ListingTokenResponseDTO updateListingPropertyByID(Long tokenId,ListingTokenRequestDTO listingTokenDTO);
    void  deleteListingPropertyById(Long listingTokenId);
    List<ListingTokenResponseDTO> getMyListings();

    List<ListingTokenResponseDTO> getAvailableListingsForAgent();
    ListingTokenResponseDTO acceptListing(Long listingId);

    Object leaveListing(Long id);
}


