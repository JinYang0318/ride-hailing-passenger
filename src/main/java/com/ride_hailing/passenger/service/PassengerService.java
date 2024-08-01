package com.ride_hailing.passenger.service;

import com.ride_hailing.passenger.dto.PassengerDTO;
import com.ride_hailing.passenger.mapper.Mapper;
import com.ride_hailing.passenger.model.Passenger;
import com.ride_hailing.passenger.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PassengerService {
    private final PassengerRepository passengerRepository;
    private final Mapper<Passenger, PassengerDTO> passengerMapper;

    public Optional<PassengerDTO> getPassengerById(Integer passengerId) {
        return passengerRepository.findByPassengerId(passengerId)
                .map(passengerMapper::mapToDTO);
    }

    public List<PassengerDTO> getPassengerByIds(List<Integer> passengerIds) {
        return passengerRepository.findAllById(passengerIds)
                .stream()
                .map(passengerMapper::mapToDTO)
                .toList();
    }

    public List<PassengerDTO> getAllPassengers() {
        return passengerRepository.findAll()
                .stream().map(passengerMapper::mapToDTO)
                .toList();
    }

    public Optional<PassengerDTO> createPassenger(PassengerDTO passengerDTO) {
        return Optional.of(passengerRepository.save(passengerMapper.mapToEntity(passengerDTO)))
                .map(passengerMapper::mapToDTO);
    }

    public Optional<PassengerDTO> updatePassenger(Integer passengerId, PassengerDTO passengerDTO) {
        return passengerRepository.findById(passengerId)
                .map(existingPassenger -> {
                    existingPassenger.setName(passengerDTO.name());
                    existingPassenger.setEmail(passengerDTO.email());

                    Passenger updatedPassenger = passengerRepository.save(existingPassenger);

                    return passengerMapper.mapToDTO(updatedPassenger);
                });
    }

    public void deletePassenger(Integer passengerId) {
        passengerRepository.deleteById(passengerId);
    }
}
