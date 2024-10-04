package com.example.javaservlets.Common;

import com.example.javaservlets.Authentication.JwtUtil;
import com.example.javaservlets.Database.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;


@WebServlet("/infoServlet")
public class InfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Assuming the token is stored in the session
        String token = request.getHeader("Authorization");
        token = token.substring(7);


        String orgId = JwtUtil.getOrgId(token);
        String userRole = JwtUtil.getRole(token);
        String companyName="";

        try {
            // Step 1: Establish connection to the database
            Connection conn = DBUtil.getConnection();
            // Step 2: Create SQL query to fetch the organization name
            String sql = "SELECT org_name FROM Organizations WHERE org_id = ?";

            // Step 3: Prepare the statement to prevent SQL injection
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, orgId);

            // Step 4: Execute the query and get the result
            ResultSet rs = stmt.executeQuery();

            // Step 5: If result found, retrieve the organization name
            if (rs.next()) {
                companyName = rs.getString("org_name");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle SQL exception properly
        }

        // Set response type to JSON
        String jsonResponse = String.format("{\"companyName\": \"%s\", \"userRole\": \"%s\"}", companyName, userRole);

        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Write the JSON response
        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
}

