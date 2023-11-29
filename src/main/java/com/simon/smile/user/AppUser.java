package com.simon.smile.user;

import com.simon.smile.user.address.Address;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AppUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotEmpty(message = "username is required")
    @Length(min = 3, max = 16, message = "username length must between 3 and 16")
    @Column(unique = true)
    private String username;

    @Length(max = 32, message = "nickname length must between 0 and 32")
    private String nickname;

    private String password;

    @NotEmpty(message = "email is required")
    @Email(message = "email format is invalid")
    @Column(unique = true)
    private String email;

    private String roles;

    private Boolean enabled;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
    private List<Address> addressList = new ArrayList<>();
}
