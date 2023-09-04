package com.simon.smile.user;

import com.simon.smile.common.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.base-url}/users")
public class UserController {
    private final UserService userService;

    private final UserToUserDtoConverter userToUserDtoConverter;

    public UserController(UserService userService, UserToUserDtoConverter userToUserDtoConverter) {
        this.userService = userService;
        this.userToUserDtoConverter = userToUserDtoConverter;
    }

    @GetMapping("/{id}")
    public Result findUserById(@PathVariable Integer id) {
        return Result.success()
                .setCode(HttpStatus.OK.value())
                .setMessage("Find user success")
                .setData(userToUserDtoConverter.convert(userService.findById(id)));
    }

    @GetMapping
    public Result findUsers() {
        List<UserDto> userDtoList = userService.findAll()
                .stream()
                .map(userToUserDtoConverter::convert)
                .collect(Collectors.toList());
        return Result.success()
                .setCode(HttpStatus.OK.value())
                .setMessage("Find all users success")
                .setData(userDtoList);
    }
}
