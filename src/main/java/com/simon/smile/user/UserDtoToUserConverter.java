package com.simon.smile.user;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoToUserConverter implements Converter<UserDto, AppUser> {
    @Override
    public AppUser convert(UserDto source) {
        return new AppUser()
                .setId(source.id())
                .setUsername(source.username())
                .setNickname(source.nickname())
                .setEmail(source.email())
                .setRoles(source.roles())
                .setEnabled(source.enabled());
    }
}
