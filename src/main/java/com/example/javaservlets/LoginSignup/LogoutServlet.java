package com.example.javaservlets.LoginSignup;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/Logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the current session, if exists
        HttpSession session = request.getSession(false);  // false prevents creating a new session if none exists

        if (session != null) {
            // Invalidate the session, removing all stored data
            session.invalidate();
        }

        // Redirect the user to the login page
        response.sendRedirect("index.jsp");
    }
}
