package com.example.javaservlets.AdminModule;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.Objects;

import com.example.javaservlets.Database.DBUtil;
import com.example.javaservlets.Authentication.JwtUtil;
import org.json.JSONObject;

@WebServlet("/AdjustInventory")
public class AdjustInventoryServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set response type
        response.setContentType("application/json");
        JSONObject jsonResponse = new JSONObject();

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader("Authorization");
        token = token.substring(7);

        String org_id= JwtUtil.getOrgId(token);

        try {
            // Parse the request body as JSON
            JSONObject requestData = new JSONObject(request.getReader().readLine());

            String itemId = requestData.getString("item_id");
            String newPrice = requestData.optString("new_price", null);
            String newQuantity = requestData.optString("new_quantity", null);

            // Establish database connection
            Connection conn = DBUtil.getConnection();

            // Build the SQL query
            StringBuilder sql = new StringBuilder("UPDATE Items SET ");
            boolean firstCondition = true; // Track if we're adding the first condition (to avoid a comma issue)

            if (!Objects.equals(newPrice, "") && !newPrice.isEmpty()) {
                sql.append("unit_price = ?");
                firstCondition = false;
            }
            if (!Objects.equals(newQuantity, "") && !newQuantity.isEmpty()) {
                if (!firstCondition) {
                    sql.append(", ");
                }
                sql.append("quantity_in_stock = ?");
            }
            sql.append(" WHERE item_id = ?");

            // Prepare SQL statement
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());

            int parameterIndex = 1;
            if (!Objects.equals(newPrice, "") && !newPrice.isEmpty()) {
                System.out.println("check3");
                pstmt.setDouble(parameterIndex++, Double.parseDouble(newPrice));
            }
            if (!Objects.equals(newQuantity, "") && !newQuantity.isEmpty()) {
                System.out.println("check4");
                pstmt.setInt(parameterIndex++, Integer.parseInt(newQuantity));
            }
            pstmt.setString(parameterIndex, itemId);

            // Execute update
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Inventory adjusted successfully");
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Item not found or no changes made");
            }

            // Close connection
            pstmt.close();
            conn.close();
        } catch (SQLException | IOException e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Error adjusting inventory: " + e.getMessage());
        }

        // Write response
        response.getWriter().write(jsonResponse.toString());
    }

}
