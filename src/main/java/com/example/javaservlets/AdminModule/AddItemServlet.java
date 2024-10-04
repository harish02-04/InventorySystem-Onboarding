package com.example.javaservlets.AdminModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.javaservlets.Authentication.JwtUtil;
import com.example.javaservlets.Database.DBUtil;
import com.example.javaservlets.Common.Idgeneration;
import org.json.JSONObject;
@WebServlet("/AddItem")
public class AddItemServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Convert the string to a JSON object
        JSONObject json = new JSONObject(sb.toString());

        // Extract item details from JSON
        String itemName = json.getString("item_name");
        String itemDescription = json.optString("item_description", null); // Optional field
        String category = json.getString("category");
        double unitPrice = json.getDouble("unit_price");
        int quantityInStock = json.getInt("quantity_in_stock");
        int reorderLevel = json.getInt("reorder_level");
        String token=json.getString("token");
        String itemId= Idgeneration.generateItemId();
        String orgId= JwtUtil.getOrgId(token);







        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO Items (item_id,item_name, item_description, category, unit_price, quantity_in_stock, reorder_level,org_id) VALUES (?,?,?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, itemId);
                stmt.setString(2, itemName);
                stmt.setString(3, itemDescription);
                stmt.setString(4, category);
                stmt.setBigDecimal(5, new BigDecimal(unitPrice));
                stmt.setInt(6, quantityInStock);
                stmt.setInt(7, reorderLevel);
                stmt.setString(8, orgId);
                stmt.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.");

        }
    }
}
