package dev.esteban.motuswebapp.configuration;

import dev.esteban.motuswebapp.service.MotusApiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationVerify implements HandlerInterceptor {
    private final MotusApiService motusApiService;

    public AuthenticationVerify(MotusApiService motusApiService) {
        this.motusApiService = motusApiService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();

        if (session.getAttribute("username") != null && motusApiService.isAuthenticated()) {
            return true; // User is authenticated, proceed with the request
        }
            response.sendRedirect("/login"); // Redirect to login page if not authenticated
            return false; // Prevent further handling of the request
    }
}