package com.simon.smile.auth;

import com.simon.smile.security.JwtProvider;
import com.simon.smile.user.UserDto;
import com.simon.smile.user.UserToUserDtoConverter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final JwtProvider jwtProvider;

    private final UserToUserDtoConverter userToUserDtoConverter;

    public AuthService(JwtProvider jwtProvider, UserToUserDtoConverter userToUserDtoConverter) {
        this.jwtProvider = jwtProvider;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    public Map<String, Object> createLoginInfo(Authentication authentication) {
        Map<String, Object> loginInfo = new HashMap<>();
        // create user info
        AppUserPrincipal principal = (AppUserPrincipal) authentication.getPrincipal();
        UserDto userDto = userToUserDtoConverter.convert(principal.appUser());
        loginInfo.put("userInfo", userDto);
        // create a JWT
        String token = this.jwtProvider.createToken(authentication);
        loginInfo.put("token", token);
        return loginInfo;
    }
}
