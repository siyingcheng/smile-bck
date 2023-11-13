package com.simon.smile.system;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Component
@WebFilter(urlPatterns = "/*")
@Slf4j
public class LogFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper resp = new ContentCachingResponseWrapper(response);

        // Execution request chain
        filterChain.doFilter(req, resp);

        // Get cache
        byte[] requestBody = req.getContentAsByteArray();
        byte[] responseBody = resp.getContentAsByteArray();

        log.info("request URI = {} {}", req.getMethod().toUpperCase(Locale.ROOT), req.getRequestURL());
        log.info("request body = {}", new String(requestBody, StandardCharsets.UTF_8));
        log.info("response body = {}", new String(responseBody, StandardCharsets.UTF_8));

        // Finally respond to the client with the cached data.
        resp.copyBodyToResponse();
    }
}
