package com.RealState.Project.Repository;

import com.RealState.Project.DTO.AgentSummaryDTO;
import com.RealState.Project.DTO.RevenuePointDTO;
import com.RealState.Project.Entity.Agent;
import com.RealState.Project.Entity.Office;
import com.RealState.Project.Entity.Transaction;
import com.RealState.Project.Entity.Type.Listing_type;
import com.RealState.Project.Entity.Type.Transactions_types;
import com.RealState.Project.Entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findByBuyer(User user);

    List<Transaction> findByAgent(Agent agent);

    Long countByAgent(Agent agent);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.agent = :agent")
    Double getTotalRevenue(Agent agent);

    @Query("SELECT SUM(t.amount) FROM Transaction t")
    Double totalRevenue();


    @Query("""
    SELECT SUM(t.amount)
    FROM Transaction t 
    WHERE t.transactionDate >= :date
    """)
    Double revenueAfter(LocalDate date);


    @Query("SELECT COUNT(t) FROM Transaction t")
    Long totalTransactions();


    Long countByType(Transactions_types type);


    // sell deals
    @Query("""
            SELECT COUNT(t)
            FROM Transaction t
            JOIN t.token lt
            WHERE lt.listingType = :type
            """)
    Long countByListingType(Listing_type type);


    // deals after date
    @Query("""
            SELECT COUNT(t)
            FROM Transaction t
            WHERE t.transactionDate >= :date
            """)
    Long dealsAfter(LocalDate date);


    // total amount
    @Query("""
            SELECT COALESCE(SUM(t.amount),0)
            FROM Transaction t
            """)
    Double totalAmount();


    // average deal amount
    @Query("""
            SELECT COALESCE(AVG(t.amount),0)
            FROM Transaction t
            """)
    Double averageDealAmount();


    @Query("""
SELECT a FROM Agent a 
LEFT JOIN ListingToken l 
ON l.agent = a AND l.status = com.RealState.Project.Entity.Type.Status.ACTIVE
WHERE a.office = :office
GROUP BY a
ORDER BY COUNT(l) ASC
""")
    List<Agent> findLeastBusyAgent(Office office);


    @Query("""
SELECT new com.RealState.Project.DTO.AgentSummaryDTO(
    a.id,
    up.name,
    p.total_deals,
    p.score,
    up.phone,
    a.status,
    p.user_rating
)
FROM Agent a
JOIN a.user u
JOIN u.userProfile up
JOIN Performance p ON p.agent = a
ORDER BY p.score DESC, p.user_rating DESC
""")
    List<AgentSummaryDTO> topAgents(Pageable pageable);


    @Query("""
SELECT new com.RealState.Project.DTO.AgentSummaryDTO(
    a.id,
    up.name,
    p.total_deals,
    p.score,
    up.phone,
    a.status,
    p.user_rating
)
FROM Agent a
JOIN a.user u
JOIN u.userProfile up
JOIN Performance p ON p.agent = a
JOIN a.office o
WHERE o.id = :officeId
ORDER BY p.score DESC, p.user_rating DESC
""")
    List<AgentSummaryDTO> topAgentsByOffice(
            @Param("officeId") Long officeId,
            Pageable pageable
    );


    @Query("""
           SELECT COALESCE(SUM(t.amount),0)
           FROM Transaction t
           JOIN t.token lt
           WHERE lt.listingType = :type
           """)
    Double revenueByListingType(Listing_type type);

    @Query("""
    SELECT COUNT(t)
    FROM Transaction t
    WHERE t.agent.office = :office
    """)
    Long totalDeals(Office office);


    @Query("""
    SELECT COALESCE(SUM(t.amount),0)
    FROM Transaction t
    WHERE t.agent.office = :office
    """)
    Double totalRevenue(Office office);


    @Query("""
    SELECT COALESCE(SUM(t.amount),0)
    FROM Transaction t
    WHERE t.agent.office = :office
    AND t.transactionDate >= :date
    """)
    Double monthlyRevenue(Office office, LocalDate date);

    @Query("""
    SELECT COUNT(t)
    FROM Transaction t
    WHERE t.agent = :agent
    AND t.transactionDate >= :date
    """)
    Long dealsAfter(Agent agent, LocalDate date);


    @Query("""
    SELECT COALESCE(SUM(t.amount),0)
    FROM Transaction t
    WHERE t.agent = :agent
    AND t.transactionDate >= :date
    """)
    Double revenueAfter(Agent agent, LocalDate date);

    List<Transaction> findByAgentOffice(Office office);


    @Query("""
    SELECT t
    FROM Transaction t
    JOIN FETCH t.agent a
    JOIN FETCH a.user u
    JOIN FETCH u.userProfile
    JOIN FETCH t.buyer b
    JOIN FETCH b.userProfile
    JOIN FETCH t.token lt
    JOIN FETCH lt.pid p
    JOIN FETCH p.owner
    """)
    List<Transaction> findAllWithDetails();


    @Query("""
    SELECT t
    FROM Transaction t
    JOIN FETCH t.agent a
    JOIN FETCH a.user au
JOIN FETCH au.userProfile
JOIN FETCH t.buyer b
JOIN FETCH b.userProfile
JOIN FETCH t.token lt
JOIN FETCH lt.pid p
JOIN FETCH p.owner o
JOIN FETCH o.userProfile
WHERE t.buyer = :user
""")
    List<Transaction> findByBuyerWithDetails(User user);

    @Query("""
SELECT t
FROM Transaction t
JOIN FETCH t.agent a
JOIN FETCH a.user au
JOIN FETCH au.userProfile
JOIN FETCH t.buyer b
JOIN FETCH b.userProfile
JOIN FETCH t.token lt
JOIN FETCH lt.pid p
JOIN FETCH p.owner o
JOIN FETCH o.userProfile
WHERE p.owner = :user
""")
    List<Transaction> findBySellerWithDetails(User user);


    @Query("""
SELECT t FROM Transaction t
ORDER BY t.transactionDate DESC
""")
    List<Transaction> findRecentTransactions(Pageable pageable);


    @Query("""
SELECT new com.RealState.Project.DTO.RevenuePointDTO(
    CONCAT('', FUNCTION('YEAR', t.transactionDate)),
    SUM(t.amount * 1.0)
)
FROM Transaction t
WHERE t.transactionDate BETWEEN :from AND :to
GROUP BY CONCAT('', FUNCTION('YEAR', t.transactionDate))
ORDER BY CONCAT('', FUNCTION('YEAR', t.transactionDate))
""")
    List<RevenuePointDTO> adminYearlyRevenue(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );


    @Query("""
SELECT new com.RealState.Project.DTO.RevenuePointDTO(
    CONCAT(
        'Week ',
        FUNCTION('YEAR', t.transactionDate),
        '-',
        FUNCTION('WEEK', t.transactionDate)
    ),
    SUM(t.amount * 1.0)
)
FROM Transaction t
WHERE t.transactionDate BETWEEN :from AND :to
GROUP BY CONCAT(
        'Week ',
        FUNCTION('YEAR', t.transactionDate),
        '-',
        FUNCTION('WEEK', t.transactionDate)
)
ORDER BY CONCAT(
        'Week ',
        FUNCTION('YEAR', t.transactionDate),
        '-',
        FUNCTION('WEEK', t.transactionDate)
)
""")
    List<RevenuePointDTO> adminWeeklyRevenue(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("""
SELECT new com.RealState.Project.DTO.RevenuePointDTO(
    CONCAT(
        FUNCTION('YEAR', t.transactionDate),
        '-',
        FUNCTION('MONTH', t.transactionDate)
    ),
    SUM(t.amount * 1.0)
)
FROM Transaction t
WHERE t.transactionDate BETWEEN :from AND :to
GROUP BY CONCAT(
        FUNCTION('YEAR', t.transactionDate),
        '-',
        FUNCTION('MONTH', t.transactionDate)
)
ORDER BY CONCAT(
        FUNCTION('YEAR', t.transactionDate),
        '-',
        FUNCTION('MONTH', t.transactionDate)
)
""")
    List<RevenuePointDTO> adminMonthlyRevenue(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );


    @Query("""
SELECT new com.RealState.Project.DTO.RevenuePointDTO(
    CONCAT('', FUNCTION('YEAR', t.transactionDate)),
    SUM(t.amount * 1.0)
)
FROM Transaction t
WHERE t.agent.id = :agentId
AND t.transactionDate BETWEEN :from AND :to
GROUP BY CONCAT('', FUNCTION('YEAR', t.transactionDate))
ORDER BY CONCAT('', FUNCTION('YEAR', t.transactionDate))
""")
    List<RevenuePointDTO> agentYearlyRevenue(
            @Param("agentId") Long agentId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("""
SELECT new com.RealState.Project.DTO.RevenuePointDTO(
     CONCAT(
        'Week ',
        FUNCTION('YEAR', t.transactionDate),
        '-',
        FUNCTION('WEEK', t.transactionDate)
    ),
    SUM(t.amount * 1.0)
)
FROM Transaction t
WHERE t.agent.id = :agentId
AND t.transactionDate BETWEEN :from AND :to
GROUP BY CONCAT(
        'Week ',
        FUNCTION('YEAR', t.transactionDate),
        '-',
        FUNCTION('WEEK', t.transactionDate)
    )
ORDER BY CONCAT(
        'Week ',
        FUNCTION('YEAR', t.transactionDate),
        '-',
        FUNCTION('WEEK', t.transactionDate)
    )
""")
    List<RevenuePointDTO> agentWeeklyRevenue(
            @Param("agentId") Long agentId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("""
SELECT new com.RealState.Project.DTO.RevenuePointDTO(
    CONCAT(
        FUNCTION('YEAR', t.transactionDate),
        '-',
        FUNCTION('MONTH', t.transactionDate)
    ),
    SUM(t.amount * 1.0)
)
FROM Transaction t
WHERE t.agent.id = :agentId
AND t.transactionDate BETWEEN :from AND :to
GROUP BY CONCAT(
        FUNCTION('YEAR', t.transactionDate),
        '-',
        FUNCTION('MONTH', t.transactionDate)
)
ORDER BY CONCAT(
        FUNCTION('YEAR', t.transactionDate),
        '-',
        FUNCTION('MONTH', t.transactionDate)
)
""")
    List<RevenuePointDTO> agentMonthlyRevenue(
            @Param("agentId") Long agentId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("""
SELECT new com.RealState.Project.DTO.RevenuePointDTO(
    CONCAT(
        'Week ',
        FUNCTION('YEAR', t.transactionDate),
        '-W',
        FUNCTION('WEEK', t.transactionDate)
    ),
    SUM(t.amount * 1.0)
)
FROM Transaction t
JOIN t.agent a
JOIN a.office o
WHERE o.id = :officeId
AND t.transactionDate BETWEEN :from AND :to
GROUP BY CONCAT(
        'Week ',
        FUNCTION('YEAR', t.transactionDate),
        '-W',
        FUNCTION('WEEK', t.transactionDate)
)
ORDER BY CONCAT(
        'Week ',
        FUNCTION('YEAR', t.transactionDate),
        '-W',
        FUNCTION('WEEK', t.transactionDate)
)
""")
    List<RevenuePointDTO> officeWeeklyRevenue(
            @Param("officeId") Long officeId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("""
SELECT new com.RealState.Project.DTO.RevenuePointDTO(
    CONCAT(
        FUNCTION('YEAR', t.transactionDate),
        '-',
        FUNCTION('MONTH', t.transactionDate)
    ),
    SUM(t.amount * 1.0)
)
FROM Transaction t
JOIN t.agent a
JOIN a.office o
WHERE o.id = :officeId
AND t.transactionDate BETWEEN :from AND :to
GROUP BY CONCAT(
        FUNCTION('YEAR', t.transactionDate),
        '-',
        FUNCTION('MONTH', t.transactionDate)
)
ORDER BY CONCAT(
        FUNCTION('YEAR', t.transactionDate),
        '-',
        FUNCTION('MONTH', t.transactionDate)
)
""")
    List<RevenuePointDTO> officeMonthlyRevenue(
            @Param("officeId") Long officeId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("""
SELECT new com.RealState.Project.DTO.RevenuePointDTO(
    CONCAT('', FUNCTION('YEAR', t.transactionDate)),
    SUM(t.amount * 1.0)
)
FROM Transaction t
JOIN t.agent a
JOIN a.office o
WHERE o.id = :officeId
AND t.transactionDate BETWEEN :from AND :to
GROUP BY CONCAT('', FUNCTION('YEAR', t.transactionDate))
ORDER BY CONCAT('', FUNCTION('YEAR', t.transactionDate))
""")
    List<RevenuePointDTO> officeYearlyRevenue(
            @Param("officeId") Long officeId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("""
SELECT t FROM Transaction t
JOIN FETCH t.token l
JOIN FETCH l.pid p
WHERE t.agent.id = :agentId
ORDER BY t.amount DESC
""")
    List<Transaction> findTopTransactionByAgent(Long agentId);


    boolean existsByToken_Id(Long listingId);



}


