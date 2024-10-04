package com.example.javaservlets.Authentication;



import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter({"/AddItem", "/ProcessTransaction","/FetchInventory","/FetchTransaction"})
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Get the Authorization header
        String token = httpRequest.getHeader("Authorization");
        System.out.println(token);
        // Check if token is not null and properly formatted (Bearer token)
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);  // Remove the "Bearer " prefix


            try {
                // Validate the token and extract email
                String email = JwtUtil.extractEmail(token);
                if (JwtUtil.validateToken(token, email)) {
                    // Token is valid, continue with the request
                    chain.doFilter(request, response);
                    return;  // Important: Return after successful token validation
                }
            } catch (Exception e) {
                e.printStackTrace();  // Log the exact exception for debugging
            }
        }

        // If token is invalid or not present, redirect to login page
        httpResponse.sendRedirect("index.jsp?error=Invalid email or password");
    }

}
