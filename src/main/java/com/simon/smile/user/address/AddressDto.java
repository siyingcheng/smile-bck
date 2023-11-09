package com.simon.smile.user.address;

import jakarta.validation.constraints.NotEmpty;

public record AddressDto(
        Integer id,
        @NotEmpty(message = "address is required")
        String address,
        @NotEmpty(message = "phone is required")
        String phone,
        Boolean isDefault
) {
}
