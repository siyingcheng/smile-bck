package com.simon.smile.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.simon.smile.user.Roles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final RSAPublicKey publicKey;
    private final RSAPrivateKey privateKey;
    private final CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;
    private final CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint;
    private final CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler;
    private static final String USERS_URI = "/users/**";

    @Value("${api.base-url}")
    private String baseUrl;

    public SecurityConfiguration(CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint,
                                 CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint,
                                 CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler
    ) throws NoSuchAlgorithmException {
        this.customBasicAuthenticationEntryPoint = customBasicAuthenticationEntryPoint;
        this.customBearerTokenAuthenticationEntryPoint = customBearerTokenAuthenticationEntryPoint;
        this.customBearerTokenAccessDeniedHandler = customBearerTokenAccessDeniedHandler;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // The generated key will have a size of 2048
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        // default prefix is `SCOPE_`, `SCOPE_ROLE_ADMIN`, so we need set prefix to empty.
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build(); // JSON Web Key
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(jwk)));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, HandlerMappingIntrospector introspector) throws Exception {
        /*
            https://spring.io/security/cve-2023-34035
            The application does not use requestMatchers(String) or requestMatchers(HttpMethod, String)
            This method cannot decide whether these patterns are Spring MVC patterns or not.
            If this endpoint is a Spring MVC endpoint, please use `requestMatchers(MvcRequestMatcher)`
            otherwise, please use `requestMatchers(AntPathRequestMatcher)`.
        */
        MvcRequestMatcher.Builder mvcMatcher = new MvcRequestMatcher.Builder(introspector);
        MvcRequestMatcher[] permitAllMatchers = new MvcRequestMatcher[]{
                mvcMatcher.pattern("/swagger-ui.html"),
                mvcMatcher.pattern("/swagger-ui/**"),
                mvcMatcher.pattern("/v3/api-docs/**"),
                mvcMatcher.pattern("/v3/api-docs"),
                mvcMatcher.pattern(HttpMethod.POST, this.baseUrl + "/users"),
                mvcMatcher.pattern(HttpMethod.GET, this.baseUrl + "/users/current_user")
        };
        MvcRequestMatcher[] adminMatchers = new MvcRequestMatcher[]{
                mvcMatcher.pattern(HttpMethod.GET, this.baseUrl + "/users"),
                mvcMatcher.pattern(HttpMethod.POST, this.baseUrl + "/users/filter"),
                mvcMatcher.pattern(HttpMethod.GET, this.baseUrl + USERS_URI),
                mvcMatcher.pattern(HttpMethod.PUT, this.baseUrl + USERS_URI),
                mvcMatcher.pattern(HttpMethod.DELETE, this.baseUrl + USERS_URI)
        };
        return httpSecurity
                .authorizeHttpRequests(request -> request
                        .requestMatchers(permitAllMatchers).permitAll()
                        .requestMatchers(adminMatchers).hasAuthority(Roles.ROLE_ADMIN.getRole())
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                        // Always a good idea to put this as last.
                        .anyRequest().authenticated())
                // This is for h2 console browser access.
                .headers(headers -> headers.frameOptions(Customizer.withDefaults()).disable())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(customBearerTokenAuthenticationEntryPoint)
                        .accessDeniedHandler(customBearerTokenAccessDeniedHandler))
                .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(customBasicAuthenticationEntryPoint))
                /* Configures the spring boot application as an OAuth2 Resource Server which authenticates all
                 the incoming requests (except the ones excluded above) using JWT authentication.
                 */
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
