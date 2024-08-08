package com.ride_hailing.passenger.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Passenger")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Passenger {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "PASSENGER_ID")
    private Integer passengerId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "EMAIL")
    private String email;
}
