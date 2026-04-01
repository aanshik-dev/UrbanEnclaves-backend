package com.RealState.Project.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
public class Images {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int image_id;

    @Column(nullable = false)
    private String url;

    @CreationTimestamp
    private LocalDate date;


    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name="pid")
    private Property propertyId;
}
