package com.RealState.Project.Service.Impl;


import com.RealState.Project.DTO.*;
import com.RealState.Project.Entity.*;
import com.RealState.Project.Entity.Type.Status;
import com.RealState.Project.Entity.Type.Transactions_types;
import com.RealState.Project.Entity.Type.UserType;
import com.RealState.Project.Repository.*;
import com.RealState.Project.Service.ListingPropertyServices;
import com.RealState.Project.Strategy.Listing.ListingAccessStrategy;
import com.RealState.Project.Strategy.Listing.ListingStrategyFactory;
import com.RealState.Project.Utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListingPropertyServiceImpl implements ListingPropertyServices {
    private final ListingTokenRepository listingTokenRepository;
    private final AgentRepository agentRepository;
    private final PropertyRepository propertyRepository;
    private final SecurityUtil securityUtil;
    private  final ListingStrategyFactory listingStrategyFactory;
    private final PerformanceRepository performanceRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public List<ListingTokenResponseDTO> getAllListedProperties() {

        return listingTokenRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public ListingTokenResponseDTO createListingToken(
            ListingTokenRequestDTO request){

        // Get logged in user
        User currentUser = securityUtil.getCurrentUser();

        // Get property
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Check ownership
        if (!property.getOwner().getId().equals(currentUser.getId())){
            throw new RuntimeException("Only owner can list property");
        }

        // Create listing (NO AGENT)
        ListingToken listing = ListingToken.builder()
                .listingType(request.getListingType())
                .price(request.getPrice())
                .description(request.getDescription())
                .status(Status.ACTIVE)   // 🔥 IMPORTANT CHANGE
                .pid(property)
                .agent(null)               // 🔥 NO AGENT
                .build();

        ListingToken saved = listingTokenRepository.save(listing);

        // use convert method (consistency)
        return convertToDTO(saved);
    }

    @Override
    public ListingTokenResponseDTO getListedPropertyById(Long listingId){

        ListingToken listing = listingTokenRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        return convertToDTO(listing);
    }

    @Override
    public ListingTokenResponseDTO updateListingPropertyByID(
            Long listingId,
            ListingTokenRequestDTO request){

        ListingToken listing = listingTokenRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        // get logged in user
        User currentUser = securityUtil.getCurrentUser();

        // property owner
        User owner = listing.getPid().getOwner();

        // authorization check
        if(!owner.getId().equals(currentUser.getId())){
            throw new RuntimeException("You are not authorized to update this listing");
        }

        // update fields
        listing.setListingType(request.getListingType());
        listing.setPrice(request.getPrice());
        listing.setDescription(request.getDescription());

        ListingToken saved = listingTokenRepository.save(listing);

        return convertToDTO(saved);
    }

    @Override
    public void deleteListingPropertyById(Long listingId){

        ListingToken listing = listingTokenRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));


        User currentUser = securityUtil.getCurrentUser();
        User owner = listing.getPid().getOwner();


        if(!owner.getId().equals(currentUser.getId())){
            throw new RuntimeException("You are not authorized to delete this listing");
        }

        listingTokenRepository.delete(listing);
    }

    @Override
    public List<ListingTokenResponseDTO> getMyListings(){

        User user = securityUtil.getCurrentUser();

        UserType type = user.getUserProfile().getUserType();

        ListingAccessStrategy strategy =
                listingStrategyFactory.getStrategy(type);

        List<ListingToken> listings =
                strategy.getListings(user);

        return listings.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<ListingTokenResponseDTO> getAvailableListingsForAgent(){

        User currentUser = securityUtil.getCurrentUser();

        // Only agent allowed
        if(currentUser.getUserProfile().getUserType() != UserType.AGENT){
            throw new RuntimeException("Only agents can view available listings");
        }

        List<ListingToken> listings =
                listingTokenRepository.findAvailableListings();

        return listings.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public ListingTokenResponseDTO acceptListing(Long listingId){

        ListingToken listing = listingTokenRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        User currentUser = securityUtil.getCurrentUser();

        // Only AGENT can accept
        if(currentUser.getUserProfile().getUserType() != UserType.AGENT){
            throw new RuntimeException("Only agent can accept listing");
        }

        Agent agent = agentRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        if(listing.getAgent() != null){
            throw new RuntimeException("Listing already assigned to another agent");
        }

        // assign agent
        listing.setAgent(agent);

        // activate listing
//        listing.setStatus(Status.ACTIVE);

        ListingToken saved = listingTokenRepository.save(listing);

        return convertToDTO(saved);
    }

    @Override
    public ListingTokenResponseDTO leaveListing(Long listingId){

        ListingToken listing = listingTokenRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        User currentUser = securityUtil.getCurrentUser();

        // Only AGENT can leave
        if(currentUser.getUserProfile().getUserType() != UserType.AGENT){
            throw new RuntimeException("Only agent can leave listing");
        }

        Agent agent = agentRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        // ❗ Check if listing has agent
        if(listing.getAgent() == null){
            throw new RuntimeException("Listing is not assigned to any agent");
        }

        // ❗ Check ownership
        if(!listing.getAgent().getId().equals(agent.getId())){
            throw new RuntimeException("You are not assigned to this listing");
        }

        // ❗ Prevent leaving if already sold (optional)
        boolean isSold = transactionRepository
                .existsByToken_Id(listingId);

        if(isSold){
            throw new RuntimeException("Cannot leave a sold listing");
        }

        // Remove agent
        listing.setAgent(null);

        // Update status
//        listing.setStatus(Status.INACTIVE); // or AVAILABLE (your choice)

        ListingToken saved = listingTokenRepository.save(listing);

        return convertToDTO(saved);
    }


    private ListingTokenResponseDTO convertToDTO(ListingToken listing){

        // ---------- PROPERTY ----------
        Property property = listing.getPid();

        PropertyForOtherTableResponseDTO propertyDTO =
                new PropertyForOtherTableResponseDTO(
                        property.getId(),
                        property.getHouseNo(),
                        property.getDescription(),
                        property.getLocality(),
                        property.getArea(),
                        property.getCity(),
                        property.getPin(),
                        property.getSize(),
                        property.getType(),
                        property.getBHK()
                );

        // ---------- OWNER ----------
        User owner = property.getOwner();
        UserProfile ownerProfile = owner.getUserProfile();

        UserForOtherTableResponseDTO ownerDTO =
                new UserForOtherTableResponseDTO(
                        ownerProfile.getName(),
                        String.valueOf(ownerProfile.getPhone()),
                        ownerProfile.getProfileURL()
                );

        // ---------- AGENT ----------
        AgentForOtherTableResponseDTO agentDTO = null;

        if(listing.getAgent() != null){

            Agent agent = listing.getAgent();
            User user = agent.getUser();
            UserProfile profile = user.getUserProfile();

            String rating = "0";

            Performance performance =
                    performanceRepository.findByAgent(agent).orElse(null);

            if(performance != null){
                rating = String.valueOf(performance.getUser_rating());
            }

            agentDTO = AgentForOtherTableResponseDTO.builder()
                    .name(profile.getName())
                    .phone(String.valueOf(profile.getPhone()))
                    .profileUrl(profile.getProfileURL())
                    .userRating(rating)
                    .build();
        }

        // ---------- FINAL DTO ----------
        return ListingTokenResponseDTO.builder()
                .tokenId(listing.getId())
                .listingDate(listing.getListingDate())
                .listingType(listing.getListingType())
                .price(listing.getPrice())
                .description(listing.getDescription())
                .status(listing.getStatus())
                .property(propertyDTO)
                .owner(ownerDTO)   // 🔥 NEW
                .agent(agentDTO)
                .build();
    }
}
