package com.example.javaservlets.LoginSignup;

import com.example.javaservlets.Common.Idgeneration;
import com.example.javaservlets.Database.DBUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AdminSignup")
public class AdminSignupServlet extends HttpServlet {



    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String orgName = request.getParameter("org_name");
        String orgEmail = request.getParameter("org_email");
        String staffName = request.getParameter("admin_name");
        String staffEmail = request.getParameter("admin_email");
        String staffPassword = request.getParameter("password");

        String orgId = Idgeneration.generateOrgId(); // Generate a random 6-digit org_id
        String staffId= Idgeneration.generateStaffId();
        try (Connection conn = DBUtil.getConnection()) {
            // Insert organization details
            String orgSql = "INSERT INTO Organizations (org_id, org_name, org_email,admin_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement orgStmt = conn.prepareStatement(orgSql)) {

                orgStmt.setString(1, orgId);
                orgStmt.setString(2, orgName);
                orgStmt.setString(3, orgEmail);
                orgStmt.setString(4, staffId);
                orgStmt.executeUpdate();
            }

            // Insert admin staff details
            String staffSql = "INSERT INTO Staff (staff_id,staff_name, email, password_hash, role, org_id) VALUES (?,?, ?, ?, 'ADMIN', ?)";
            try (PreparedStatement staffStmt = conn.prepareStatement(staffSql)) {
                staffStmt.setString(1, staffId);
                staffStmt.setString(2, staffName);
                staffStmt.setString(3, staffEmail);
                staffStmt.setString(4, staffPassword); // Use plain text or your hashing logic here
                staffStmt.setString(5, orgId);
                staffStmt.executeUpdate();
            }


            response.sendRedirect("index.jsp"); // Redirect on successful signup
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.");
        }
    }
}
