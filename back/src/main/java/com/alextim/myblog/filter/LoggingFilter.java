package com.alextim.myblog.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Order(1)
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            log.info("Request: [{}] {}?{}", wrappedRequest.getMethod(), wrappedRequest.getRequestURL(), getQueryParams(wrappedRequest));
            log.info("Request Body: {}", getBody(wrappedRequest));

            filterChain.doFilter(wrappedRequest, wrappedResponse);

            log.info("Response Status: {}", wrappedResponse.getStatus());
            logResponse(wrappedResponse);

        } finally {
            MDC.clear();
            wrappedResponse.copyBodyToResponse();
        }
    }

    private String getQueryParams(HttpServletRequest request) {
        return request.getParameterMap().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    private String getBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8);
        }
        return "[No Body]";
    }

    private void logResponse(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            String contentType = response.getContentType();
            if (contentType != null && contentType.contains("application/json")) {
                String body = new String(content, StandardCharsets.UTF_8);
                log.info("Response Body: {}", body);
            } else {
                log.info("Response Body: [Binary Content]");
            }
        } else {
            log.info("Response Body: [No Body]");
        }
    }
}