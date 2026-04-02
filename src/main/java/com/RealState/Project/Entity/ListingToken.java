package com.RealState.Project.Entity;

import com.RealState.Project.Entity.Type.Listing_type;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ListingToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int token_id;

    @CreationTimestamp
    private LocalDate listingDate;

    @Column(name="listType",nullable = false)
    @Enumerated(EnumType.STRING)
    private Listing_type listingType;

    @Column(nullable = false)
    private float price;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String status;

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name="pid")
    private Property pid;

    @ManyToOne(optional = true)
    @JoinColumn(name = "aid")   // FK column in listing_token
    private Agent agent_id;
}
