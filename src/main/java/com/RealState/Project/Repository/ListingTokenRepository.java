package com.RealState.Project.Repository;

import com.RealState.Project.Entity.Agent;
import com.RealState.Project.Entity.ListingToken;
import com.RealState.Project.Entity.Office;
import com.RealState.Project.Entity.Type.Listing_type;
import com.RealState.Project.Entity.Type.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ListingTokenRepository extends JpaRepository<ListingToken,Long> {

    List<ListingToken> findByPidOfficeId(Long officeId);

    Long countByAgent(Agent agent);

    Long countByAgentAndStatus(Agent agent, Status status);

    Long countByStatus(Status status);


    Long countByPidOffice(Office office);

    Long countByPidOfficeAndStatus(Office office, Status status);

    Long countByPidOfficeAndListingType(Office office, Listing_type type);

    @Query("""
    SELECT COUNT(l)
    FROM ListingToken l
    WHERE l.pid.office = :office
    AND l.listingDate >= :date
    """)
    Long listingsAfter(Office office, LocalDate date);
}
