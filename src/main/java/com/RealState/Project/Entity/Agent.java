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
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name="agent_id")
    private User user;

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
