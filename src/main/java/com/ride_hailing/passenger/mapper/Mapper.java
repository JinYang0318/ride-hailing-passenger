package com.ride_hailing.passenger.mapper;

public interface Mapper<D, E> {
    D mapToEntity(E dto);
    E mapToDTO(D entity);
}
