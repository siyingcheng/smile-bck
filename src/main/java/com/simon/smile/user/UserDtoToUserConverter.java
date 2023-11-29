package com.simon.smile.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserDtoToUserConverter implements Converter<UserDto, AppUser> {
    @Override
    public AppUser convert(UserDto source) {
        AppUser appUser = AppUser.builder()
                .id(source.id())
                .username(source.username())
                .nickname(source.nickname())
                .email(source.email())
                .roles(source.roles())
                .build();
        if (Objects.nonNull(source.enabled())) {
            appUser.setEnabled(source.enabled());
        }
        return appUser;
    }
}
