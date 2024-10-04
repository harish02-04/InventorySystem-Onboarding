package com.example.javaservlets.Authentication;

import sun.plugin.dom.core.Element;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter({"/Admin.jsp", "/Staff.jsp"}) // Apply filter to these pages
public class RoleFilterServlet implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Retrieve the JWT token from the session
        HttpSession session = httpRequest.getSession(false); // false prevents creating a new session if none exists
        String token = (session != null) ? (String) session.getAttribute("token") : null;
        System.out.println(token);

        if (token == null || token.isEmpty()) {
            // No token found in session, redirect to login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.jsp");
            return; // Stop the request from proceeding further
        }

        // Optionally, verify token and check for roles here
        try {
            String userRole = JwtUtil.getRole(token);  // Assuming a utility to parse the token
            String requestedUri = httpRequest.getRequestURI();

            // Restrict access based on role and requested URI
            if (requestedUri.contains("/Admin.jsp") && !"ADMIN".equals(userRole)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admins only");
                return;
            } else if (requestedUri.contains("/Staff.jsp") && !"STAFF".equals(userRole)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Staff only");
                return;
            }

        } catch (Exception e) {
            // Token is invalid or parsing failed
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
            return;
        }

        // Proceed to the requested resource
        chain.doFilter(request, response);
    }

}

