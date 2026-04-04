package com.RealState.Project.Entity;


import com.RealState.Project.Entity.Type.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@ToString
public class Agent{
    @Id
   // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="serial_no")
    private int sno;   // hidden

    @OneToOne(cascade = CascadeType.PERSIST, optional = false,orphanRemoval = true)
    @MapsId
    @JoinColumn(name="agent_id")
    private User aid;

    @Column(nullable = false)
    private float commissionRate;

    @Column(nullable = false)
    private String licenceNumber;

    @Column(nullable = false)
    private int experience;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name="officeId")
    private Office office_id;
}
