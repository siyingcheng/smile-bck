package com.simon.smile.user.address;

import jakarta.validation.constraints.NotEmpty;

public record AddressDto(
        Integer id,
        @NotEmpty(message = "fullAddress is required")
        String fullAddress,
        @NotEmpty(message = "phone is required")
        String phone,
        Boolean isDefault
) {
}
