package com.RealState.Project.Strategy.Listing;

import com.RealState.Project.Entity.ListingToken;
import com.RealState.Project.Entity.User;

import java.util.List;

public interface ListingAccessStrategy {

    List<ListingToken> getListings(User user);

    boolean canAccess(ListingToken listing, User user);
}