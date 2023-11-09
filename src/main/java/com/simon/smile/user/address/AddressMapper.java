package com.simon.smile.user.address;

import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    public AddressDto toDto(Address address) {
        return new AddressDto(address.getId(), address.getAddress(), address.getPhone(), address.isDefault());
    }

    public Address toEntity(AddressDto addressDto) {
        return new Address()
                .setAddress(addressDto.address())
                .setPhone(addressDto.phone())
                .setDefault(addressDto.isDefault());
    }
}
