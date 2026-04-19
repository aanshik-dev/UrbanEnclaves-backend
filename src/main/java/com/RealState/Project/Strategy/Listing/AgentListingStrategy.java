package com.RealState.Project.Strategy.Listing;

import com.RealState.Project.Entity.Agent;
import com.RealState.Project.Entity.ListingToken;
import com.RealState.Project.Entity.Type.Status;
import com.RealState.Project.Entity.User;
import com.RealState.Project.Repository.AgentRepository;
import com.RealState.Project.Repository.ListingTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AgentListingStrategy implements ListingAccessStrategy {

    private final AgentRepository agentRepository;
    private final ListingTokenRepository listingRepository;

    @Override
    public List<ListingToken> getListings(User user){

        Agent agent = agentRepository.findByUser(user)
                .orElseThrow();

        List<ListingToken> activeListings =
                listingRepository.findByAgentAndStatus(agent, Status.ACTIVE);

        List<ListingToken> transactedListings =
                listingRepository.findListingsInTransactionsByAgent(agent);

        Set<ListingToken> result = new LinkedHashSet<>();
        result.addAll(activeListings);
        result.addAll(transactedListings);

        return new ArrayList<>(result);
    }

    @Override
    public boolean canAccess(ListingToken listing, User user){

        return listing
                .getAgent()
                .getUser()
                .getId()
                .equals(user.getId());
    }
}