package com.simon.smile.user.address;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AddressMapper {
    public AddressDto toDto(Address address) {
        return new AddressDto(address.getId(), address.getFullAddress(), address.getPhone(), address.isDefault());
    }

    public Address toEntity(AddressDto addressDto) {
        return new Address()
                .setId(addressDto.id())
                .setFullAddress(addressDto.fullAddress())
                .setPhone(addressDto.phone())
                .setDefault(!Objects.isNull(addressDto.isDefault()) && addressDto.isDefault());
    }
}
