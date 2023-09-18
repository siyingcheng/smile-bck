package com.simon.smile.auth;

import com.simon.smile.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.base-url}")
public class AuthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result getLoginInfo(Authentication authentication) {
        String username = ((AppUserPrincipal) authentication.getPrincipal()).appUser().getUsername();
        LOGGER.debug("Authentication user: '{}'", username);
        LOGGER.debug("Authentication authorities: '{}'", authentication.getAuthorities());
        return Result.success("welcome " + username)
                .setData(authService.createLoginInfo(authentication));
    }
}
