package com.example.javaservlets.LoginSignup;

import com.example.javaservlets.Common.Idgeneration;
import com.example.javaservlets.Database.DBUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/StaffSignup")
public class StaffSignupServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String staffId= Idgeneration.generateStaffId();
        String staffName = request.getParameter("staff_name");
        String staffEmail = request.getParameter("staff_email");
        String password = request.getParameter("password");
        String orgId = request.getParameter("org_id");

        try (Connection conn = DBUtil.getConnection()) {
            // Check if org_id is valid
            String checkOrgSql = "SELECT * FROM Organizations WHERE org_id = ?";
            try (PreparedStatement checkOrgStmt = conn.prepareStatement(checkOrgSql)) {
                checkOrgStmt.setString(1, orgId);
                ResultSet rs = checkOrgStmt.executeQuery();
                if (!rs.next()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Organization ID.");
                    return;
                }
            }

            // Insert staff
            String staffSql = "INSERT INTO Staff (staff_id,staff_name, email, password_hash, role, org_id) VALUES (?,?, ?, ?, 'STAFF', ?)";
            try (PreparedStatement staffStmt = conn.prepareStatement(staffSql)) {
                staffStmt.setString(1, staffId);
                staffStmt.setString(2, staffName);
                staffStmt.setString(3, staffEmail);
                staffStmt.setString(4, password); // Make sure to hash the password
                staffStmt.setString(5, orgId);
                staffStmt.executeUpdate();
            }


            response.sendRedirect("index.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.");
        }
    }
}
