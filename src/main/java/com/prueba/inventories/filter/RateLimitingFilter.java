package com.prueba.inventories.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter implements Filter {
    private final Map<String, ClientRequestCount> requestCounts = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_MINUTE = 60;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        String clientIp = getClientIp(httpRequest);
        ClientRequestCount clientCount = requestCounts.computeIfAbsent(clientIp, k -> new ClientRequestCount());
        synchronized (clientCount) {
            if (System.currentTimeMillis() - clientCount.timestamp > 60_000) {
                clientCount.count = new AtomicInteger(0);
                clientCount.timestamp = System.currentTimeMillis();
            }
            if (clientCount.count.incrementAndGet() > MAX_REQUESTS_PER_MINUTE) {
                httpResponse.setStatus(429);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("""
                    {
                        "errors": [{
                            "status": "429",
                            "title": "Too Many Requests",
                            "detail": "Has excedido el límite de 60 peticiones por minuto"
                        }]
                    }
                    """);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private static class ClientRequestCount {
        AtomicInteger count = new AtomicInteger(0);
        long timestamp = System.currentTimeMillis();
    }
}
