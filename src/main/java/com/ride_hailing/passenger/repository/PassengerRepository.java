package com.ride_hailing.passenger.repository;

import com.ride_hailing.passenger.model.Passenger;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassengerRepository extends ListCrudRepository<Passenger, Integer> {
    Optional<Passenger> findByPassengerId(Integer passengerId);
}
