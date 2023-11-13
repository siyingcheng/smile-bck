package com.simon.smile.user.address;

import com.simon.smile.common.Result;
import com.simon.smile.user.AppUser;
import com.simon.smile.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.base-url}/users")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    private final AddressMapper addressMapper;

    private final UserService userService;

    @GetMapping("/address/{id}")
    public Result findAddressById(@PathVariable Integer id) {
        return Result.success("Find fullAddress success")
                .setData(addressMapper.toDto(addressService.findById(id)));
    }

    @GetMapping("/{userId}/address")
    public Result findAddressesByUserId(@PathVariable Integer userId) {
        List<AddressDto> addressDtoList = addressService.findByOwnerId(userId)
                .stream()
                .map(addressMapper::toDto)
                .toList();
        return Result.success("Find addresses success")
                .setData(addressDtoList);
    }

    @PostMapping("/{userId}/address")
    public Result createAddress(@PathVariable Integer userId, @Valid @RequestBody AddressDto addressDto) {
        AppUser appUser = userService.findById(userId);
        Address address = addressMapper.toEntity(addressDto).setOwner(appUser);
        return Result.success("Create fullAddress success")
                .setData(addressMapper.toDto(addressService.create(address)));
    }

    @DeleteMapping("/address/{addressId}")
    public Result deleteAddress(@PathVariable Integer addressId) {
        addressService.delete(addressId);
        return Result.success("Delete fullAddress success");
    }

    @PutMapping("/{userId}/address/{addressId}")
    public Result updateAddress(@PathVariable Integer userId, @PathVariable Integer addressId, @Valid @RequestBody AddressDto addressDto) {
        AppUser appUser = userService.findById(userId);
        Address address = addressMapper.toEntity(addressDto).setOwner(appUser);
        return Result.success("Update fullAddress success")
                .setData(addressMapper.toDto(addressService.update(addressId, address)));
    }
}
