package com.simon.smile.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoToUserConverter implements Converter<UserDto, AppUser> {
    @Override
    public AppUser convert(UserDto source) {
        return AppUser.builder()
                .id(source.id())
                .username(source.username())
                .nickname(source.nickname())
                .email(source.email())
                .roles(source.roles())
                .enabled(source.enabled())
                .build();
    }
}
