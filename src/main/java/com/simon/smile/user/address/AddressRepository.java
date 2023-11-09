package com.simon.smile.user.address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    @Query("select a from Address a where a.owner.id = :owner_id")
    List<Address> findByOwnerId(@Param("owner_id") Integer id);

    @Query("select a from Address a where a.owner.id = :owner_id and a.id = :address_id")
    Optional<Address> findByUserIdAndAddressId(@Param("owner_id") int userId, @Param("address_id") int addressId);
}
