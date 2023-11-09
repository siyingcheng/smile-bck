package com.simon.smile.user.address;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simon.smile.common.exception.ObjectNotFoundException;
import com.simon.smile.user.AppUser;
import com.simon.smile.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Address API endpoint testing")
class AddressControllerTest {

    private static final String ERROR_ADDRESS_NOT_FOUND = "Not found the address with ID: 1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AddressService addressService;

    @MockBean
    private UserService userService;

    @Value("${api.base-url}/users")
    private String baseUrl;

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
                .setAddress("test address");

        addressList = List.of(
                address,
                new Address()
                        .setId(2)
                        .setAddress("test address 2")
                        .setPhone("13012345678")
                        .setOwner(appUser)
                        .setDefault(false)
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Verify find address by ID success")
    void testFindAddressByIdSuccess() throws Exception {
        given(addressService.findById(anyInt())).willReturn(address);

        AddressDto addressDto = addressMapper.toDto(address);

        mockMvc.perform(get(baseUrl + "/address/{id}", 1)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find address success"))
                .andExpect(jsonPath("$.data.address").value(addressDto.address()))
                .andExpect(jsonPath("$.data.phone").value(addressDto.phone()))
                .andExpect(jsonPath("$.data.isDefault").value(addressDto.isDefault()));
    }

    @Test
    @DisplayName("Verify find address by ID error when ID not exist")
    void testFindAddressByIdErrorWhenIdNotExist() throws Exception {
        given(addressService.findById(anyInt())).willThrow(new ObjectNotFoundException(ERROR_ADDRESS_NOT_FOUND));

        mockMvc.perform(get(baseUrl + "/address/{id}", 1)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value(ERROR_ADDRESS_NOT_FOUND))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    @DisplayName("Verify find addresses by owner ID success")
    void testFindAddressesByOwnerIdSuccess() throws Exception {
        given(addressService.findByOwnerId(anyInt())).willReturn(addressList);

        mockMvc.perform(get(baseUrl + "/{userId}/address", 1)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Find addresses success"))
                .andExpect(jsonPath("$.data").value(hasSize(2)))
                .andExpect(jsonPath("$.data[0].address").value(address.getAddress()))
                .andExpect(jsonPath("$.data[0].phone").value(address.getPhone()))
                .andExpect(jsonPath("$.data[0].isDefault").value(address.isDefault()))
                .andExpect(jsonPath("$.data[0].owner").doesNotHaveJsonPath());
    }

    @Test
    @DisplayName("Verify create address success")
    void testCreateAddressSuccess() throws Exception {
        given(userService.findById(anyInt())).willReturn(appUser);
        given(addressService.create(any(Address.class))).willReturn(address);

        String body = objectMapper.writeValueAsString(addressMapper.toDto(address));

        mockMvc.perform(post(baseUrl + "/{userId}/address", 1)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Create address success"))
                .andExpect(jsonPath("$.data.address").value(address.getAddress()))
                .andExpect(jsonPath("$.data.phone").value(address.getPhone()))
                .andExpect(jsonPath("$.data.isDefault").value(address.isDefault()))
                .andExpect(jsonPath("$.data.owner").doesNotHaveJsonPath());
    }

    @Test
    @DisplayName("Verify create address error when address incorrect")
    void testCreateAddressError() throws Exception {
        given(userService.findById(anyInt())).willReturn(appUser);

        AddressDto addressDto = new AddressDto(null, null, null, null);

        mockMvc.perform(post(baseUrl + "/{userId}/address", 1)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .content(objectMapper.writeValueAsString(addressDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Provided arguments are invalid, set data for details"))
                .andExpect(jsonPath("$.data.address").value("address is required"))
                .andExpect(jsonPath("$.data.phone").value("phone is required"));
    }

    @Test
    @DisplayName("Verify delete address success")
    void testDeleteAddressSuccess() throws Exception {
        doNothing().when(addressService).delete(anyInt());

        mockMvc.perform(delete(baseUrl + "/address/{addressId}", 1)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Delete address success"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    @DisplayName("Verify delete address error when ID not exist")
    void testDeleteAddressErrorWhenIdNotExist() throws Exception {
        doThrow(new ObjectNotFoundException(ERROR_ADDRESS_NOT_FOUND)).when(addressService).delete(anyInt());

        mockMvc.perform(delete(baseUrl + "/address/{addressId}", 1)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value(ERROR_ADDRESS_NOT_FOUND))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    @DisplayName("Verify update address success")
    void testUpdateAddressSuccess() throws Exception {
        given(userService.findById(anyInt())).willReturn(appUser);
        given(addressService.update(anyInt(), any(Address.class))).willReturn(address);

        String body = objectMapper.writeValueAsString(addressMapper.toDto(address));

        mockMvc.perform(put(baseUrl + "/{userId}/address/{addressId}", 1, 1)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flag").value(true))
                .andExpect(jsonPath("$.message").value("Update address success"))
                .andExpect(jsonPath("$.data.address").value(address.getAddress()))
                .andExpect(jsonPath("$.data.phone").value(address.getPhone()))
                .andExpect(jsonPath("$.data.isDefault").value(address.isDefault()))
                .andExpect(jsonPath("$.data.owner").doesNotHaveJsonPath());
    }

    @Test
    @DisplayName("Verify update address error when user id not exist")
    void testUpdateAddressErrorWhenUserIdNotExist() throws Exception {
        given(userService.findById(anyInt())).willThrow(new ObjectNotFoundException("Not found user with ID: 1"));

        String body = objectMapper.writeValueAsString(addressMapper.toDto(address));

        mockMvc.perform(put(baseUrl + "/{userId}/address/{addressId}", 1, 1)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value("Not found user with ID: 1"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    @DisplayName("Verify update address error when address id not exist")
    void testUpdateAddressErrorWhenAddressIdNotExist() throws Exception {
        given(userService.findById(anyInt())).willReturn(appUser);
        given(addressService.update(anyInt(), any(Address.class))).willThrow(new ObjectNotFoundException(ERROR_ADDRESS_NOT_FOUND));

        String body = objectMapper.writeValueAsString(addressMapper.toDto(address));

        mockMvc.perform(put(baseUrl + "/{userId}/address/{addressId}", 1, 1)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.flag").value(false))
                .andExpect(jsonPath("$.message").value(ERROR_ADDRESS_NOT_FOUND))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }
}