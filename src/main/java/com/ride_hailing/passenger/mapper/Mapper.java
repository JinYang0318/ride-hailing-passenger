package com.ride_hailing.passenger.mapper;

public interface Mapper<D, E> {
    D mapToEntity(E entity);
    E mapToDTO(D dto);
}
