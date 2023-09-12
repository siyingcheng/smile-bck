package com.simon.smile.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record UserDto(Integer id,
                      @NotEmpty(message = "username is required")
                      @Length(min = 3, max = 16, message = "username length must between 3 and 16")
                      String username,
                      @Length(max = 32, message = "nickname length must between 0 and 32")
                      String nickname,
                      @NotEmpty(message = "email is required")
                      @Email(message = "email format is invalid")
                      String email,
                      String roles,
                      boolean enabled) {
}
