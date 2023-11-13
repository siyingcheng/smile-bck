package com.simon.smile.user.address;

import com.simon.smile.common.exception.ObjectNotFoundException;
import com.simon.smile.user.AppUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    private static final String ERROR_ADDRESS_NOT_FOUND = "Not found the fullAddress with ID: 1";

    @Mock
    private AddressRepository addressRepository;
    @InjectMocks
    private AddressService addressService;
    private final AppUser appUser = new AppUser()
            .setId(1)
            .setUsername("test")
            .setEmail("test@exmaple.com")
            .setEnabled(true)
            .setRoles("ROLE_USER");
    private Address address;

    private List<Address> addressList;

    @BeforeEach
    void setUp() {
        address = new Address()
                .setId(1)
                .setDefault(true)
                .setOwner(appUser)
                .setPhone("13012345678")
                .setFullAddress("test fullAddress");

        addressList = List.of(
                address,
                new Address()
                        .setId(2)
                        .setFullAddress("test fullAddress 2")
                        .setPhone("13012345678")
                        .setOwner(appUser)
                        .setDefault(false)
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Verify create fullAddress success")
    void validCreateSuccess() {

        given(addressRepository.save(any(Address.class))).willReturn(address);

        Address createdAddress = addressService.create(address);

        assertThat(createdAddress.getFullAddress()).isEqualTo(address.getFullAddress());
        assertThat(createdAddress.getPhone()).isEqualTo(address.getPhone());
        assertThat(createdAddress.getOwner()).isEqualTo(address.getOwner());
        assertThat(createdAddress.isDefault()).isEqualTo(address.isDefault());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("Verify delete fullAddress success")
    void validDeleteSuccess() {
        given(addressRepository.findById(1)).willReturn(Optional.of(address));

        doNothing().when(addressRepository).deleteById(anyInt());

        addressService.delete(1);

        verify(addressRepository, times(1)).findById(anyInt());
        verify(addressRepository, times(1)).deleteById(anyInt());
    }

    @Test
    @DisplayName("Verify delete fullAddress error when id not exist")
    void validDeleteErrorWhenIDNotExist() {
        given(addressRepository.findById(1)).willThrow(new ObjectNotFoundException(ERROR_ADDRESS_NOT_FOUND));

        Throwable throwable = catchThrowable(() -> addressService.delete(1));

        assertThat(throwable).isInstanceOf(ObjectNotFoundException.class);
        assertThat(throwable).hasMessage(ERROR_ADDRESS_NOT_FOUND);
        verify(addressRepository, times(1)).findById(anyInt());
        verify(addressRepository, times(0)).deleteById(anyInt());
    }

    @Test
    @DisplayName("Verify find fullAddress by ID success")
    void validFindByIdSuccess() {
        given(addressRepository.findById(anyInt())).willReturn(Optional.of(address));

        Address foundAddress = addressService.findById(1);

        assertThat(foundAddress.getFullAddress()).isEqualTo(address.getFullAddress());
        assertThat(foundAddress.getPhone()).isEqualTo(address.getPhone());
        assertThat(foundAddress.getOwner()).isEqualTo(address.getOwner());
        assertThat(foundAddress.isDefault()).isEqualTo(address.isDefault());
        verify(addressRepository, times(1)).findById(anyInt());
    }

    @Test
    @DisplayName("Verify find addresses by owner Id success")
    void validFindAddressesByOwnerIDSuccess() {
        given(addressRepository.findByOwnerId(anyInt())).willReturn(addressList);

        List<Address> foundAddresses = addressService.findByOwnerId(appUser.getId());

        assertThat(foundAddresses).hasSize(2);
        assertThat(foundAddresses.get(0).getFullAddress()).isEqualTo(address.getFullAddress());
        verify(addressRepository, times(1)).findByOwnerId(anyInt());
    }

    @Test
    @DisplayName("Verify update fullAddress success")
    void validUpdateSuccess() {
        var newAddress = new Address()
                .setId(1)
                .setFullAddress("new fullAddress")
                .setPhone("new phone")
                .setOwner(appUser)
                .setDefault(true);
        given(addressRepository.findById(anyInt())).willReturn(Optional.of(address));
        given(addressRepository.save(any(Address.class))).willReturn(newAddress);

        Address updatedAddress = addressService.update(1, newAddress);
        assertThat(updatedAddress.getFullAddress()).isEqualTo(newAddress.getFullAddress());
        assertThat(updatedAddress.getPhone()).isEqualTo(newAddress.getPhone());
        assertThat(updatedAddress.getOwner()).isEqualTo(newAddress.getOwner());
        assertThat(updatedAddress.isDefault()).isEqualTo(newAddress.isDefault());
        verify(addressRepository, times(1)).findById(anyInt());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("Verify update fullAddress error when the ID not exist")
    void validUpdateErrorWhenIDNotExist() {
        given(addressRepository.findById(anyInt()))
                .willThrow(new ObjectNotFoundException(ERROR_ADDRESS_NOT_FOUND));

        Throwable throwable = catchThrowable(() -> addressService.update(1, address));
        assertThat(throwable).isInstanceOf(ObjectNotFoundException.class);
        assertThat(throwable).hasMessage(ERROR_ADDRESS_NOT_FOUND);
        verify(addressRepository, times(1)).findById(anyInt());
        verify(addressRepository, times(0)).save(any(Address.class));
    }

    @Test
    @DisplayName("Verify find fullAddress by ID error when id not exist")
    void validFindByIdErrorWhenIDNotExist() {
        given(addressRepository.findById(anyInt()))
                .willThrow(new ObjectNotFoundException(ERROR_ADDRESS_NOT_FOUND));

        Throwable throwable = catchThrowable(() -> addressService.findById(1));
        assertThat(throwable).isInstanceOf(ObjectNotFoundException.class);
        assertThat(throwable).hasMessage(ERROR_ADDRESS_NOT_FOUND);
        verify(addressRepository, times(1)).findById(anyInt());
    }
}