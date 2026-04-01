package com.RealState.Project.Entity;
import com.RealState.Project.Entity.Type.Property_type;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pid;

    @Column(nullable = false,length = 10)
    private String houseNo;

    @Column(nullable = false)
    private String locality;

    @Column(nullable = false)
    private String area;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false,length = 10)
    private int pin;

    @Column(nullable = false)
    private float size;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Property_type type;

    @Column(nullable = false)
    private int BHK;

    @Column(length=4)
    private int year_built;

    @ManyToOne(cascade = CascadeType.PERSIST,optional = false)
    @JoinColumn(name="ownerId")
    private User owner_id;

    @ManyToOne(cascade = CascadeType.PERSIST,optional = false)
    @JoinColumn(name = "officeId")
    private Office office_id;
}
