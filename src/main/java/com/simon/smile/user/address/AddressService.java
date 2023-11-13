package com.simon.smile.user.address;

import com.simon.smile.common.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    public Address create(Address address) {
        return addressRepository.save(address);
    }

    public void delete(int id) {
        findById(id);
        addressRepository.deleteById(id);
    }

    public Address findById(int id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Not found fullAddress with ID: %s", id)));
    }

    public List<Address> findByOwnerId(Integer ownerId) {
        return addressRepository.findByOwnerId(ownerId);
    }

    public Address update(Integer id, Address newAddress) {
        findById(id);
        return addressRepository.save(newAddress);
    }
}
