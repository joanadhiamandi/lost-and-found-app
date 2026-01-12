package com.lostandfound.app.filters;

import com.lostandfound.app.entities.AppUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class SessionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Object userObj = session != null ? session.getAttribute("loggedinuser") : null;

        // ✅ If no user in session, redirect to login
        if (userObj == null || !(userObj instanceof AppUser)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // ✅ User is logged in, allow access
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // ✅ Public pages that don't require login
        return uri.equals("/") ||
                uri.equals("/login") ||
                uri.equals("/dologin") ||
                uri.equals("/register") ||
                uri.equals("/doregister") ||
                uri.equals("/logout") ||
                uri.startsWith("/css/") ||
                uri.startsWith("/img/") ||
                uri.startsWith("/js/") ||
                uri.startsWith("/vendor/") ||
                uri.startsWith("/uploads/") ||
                uri.startsWith("/static/");
    }
}
