package com.simon.smile.user.address;

import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    public AddressDto toDto(Address address) {
        return new AddressDto(address.getId(), address.getFullAddress(), address.getPhone(), address.isDefault());
    }

    public Address toEntity(AddressDto addressDto) {
        return new Address()
                .setFullAddress(addressDto.fullAddress())
                .setPhone(addressDto.phone())
                .setDefault(addressDto.isDefault());
    }
}
