package com.example.javaservlets.LoginSignup;

import com.example.javaservlets.Authentication.JwtUtil;
import com.example.javaservlets.Database.DBUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try (Connection conn = DBUtil.getConnection()) {
            // Check if the user exists in the Staff table
            String sql = "SELECT * FROM Staff WHERE email = ? AND password_hash = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {




                    String staffToken = JwtUtil.generateToken(email, rs.getString("role"),rs.getString("org_id"),rs.getString("staff_id"));
                    response.setHeader("Authorization", "Bearer " + staffToken);
                    //System.out.println(staffToken);
                    HttpSession session = request.getSession();
                    session.setAttribute("token", staffToken);
                    //String orgId = JwtUtil.getOrgId(staffToken);
                    //String role = JwtUtil.getRole(staffToken);
                    //System.out.println(staffToken)]

                    if(rs.getString("role").equals("ADMIN")){
                        response.sendRedirect("Admin.jsp");
                    }
                    else if (rs.getString("role").equals("STAFF")){
                        response.sendRedirect("Staff.jsp");
                    }

                } else {

                    response.sendRedirect("index.jsp?error=Invalid email or password");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.");
        }
    }
}
