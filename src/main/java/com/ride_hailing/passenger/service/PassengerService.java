package com.ride_hailing.passenger.service;

import com.ride_hailing.passenger.dto.PassengerDTO;
import com.ride_hailing.passenger.mapper.PassengerMapper;
import com.ride_hailing.passenger.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PassengerService {
    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;

    public Optional<PassengerDTO> getPassengerById(Integer passengerId){
        return passengerRepository.findByPassengerId(passengerId)
                .map(passengerMapper::mapToDTO);
    }

    public List<PassengerDTO> getPassengerByIds(List<Integer>passengerIds){
        return passengerRepository.findAllById(passengerIds)
                .stream()
                .map(passengerMapper::mapToDTO)
                .toList();
    }

    public Optional<PassengerDTO> createPassenger(PassengerDTO passengerDTO){
        return Optional.of(passengerRepository.save(passengerMapper.mapToEntity(passengerDTO)))
                .map(passengerMapper::mapToDTO);
    }

    public void deletePassenger(Integer passengerId){
       passengerRepository.deleteById(passengerId);
    }
}
