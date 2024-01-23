package com.example.blog.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JWTFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Collections.list(request.getHeaderNames()).forEach(headerName -> {
            System.out.println(headerName + " : " + request.getHeader(headerName));
        });
        filterChain.doFilter(request, response);
    }
}
