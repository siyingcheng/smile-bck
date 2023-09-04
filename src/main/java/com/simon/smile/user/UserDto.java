package com.simon.smile.user;

import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record UserDto(Integer id,
                      @NotEmpty(message = "username is required")
                      @Length(min = 3, max = 16, message = "username length must between 4 and 16")
                      String username,
                      @Length(max = 16, message = "nickname length must between 0 and 16")
                      String nickname,
                      @NotEmpty(message = "email is required")
                      String email,
                      String roles,
                      boolean enabled) {
}
