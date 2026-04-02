package com.RealState.Project.Entity;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
public class Office {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int oid;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false,length=40)
    private String email;

    @Column(nullable = false)
    private long phone;

    @Column(nullable = false)
    private String city;
}
