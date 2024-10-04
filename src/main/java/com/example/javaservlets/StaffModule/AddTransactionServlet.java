

package com.example.javaservlets.StaffModule;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.javaservlets.Authentication.JwtUtil;
import com.example.javaservlets.Common.Idgeneration;
import com.example.javaservlets.Database.DBUtil;
import org.json.JSONObject;
@WebServlet("/ProcessTransaction")
public class AddTransactionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Parse the request JSON body
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            jsonBuffer.append(line);
        }
        String jsonString = jsonBuffer.toString();
        JSONObject jsonObject = new JSONObject(jsonString);

        String itemId = jsonObject.getString("item_id");
        String transactionType = jsonObject.getString("transaction_type");
        int quantity = jsonObject.getInt("quantity");


        String token=jsonObject.getString("token");
        String staffId = JwtUtil.getStaff_id(token);
        String orgId = JwtUtil.getOrgId(token);
        String transactionId = Idgeneration.generateTransactionId(); // Generate a unique transaction ID





        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBUtil.getConnection(); // Replace with your DB connection method

            // Begin Transaction
            conn.setAutoCommit(false);

            // Check the item quantity in stock
            String checkQuantitySql = "SELECT quantity_in_stock, reorder_level FROM Items WHERE item_id = ?";
            stmt = conn.prepareStatement(checkQuantitySql);
            stmt.setString(1, itemId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int quantityInStock = rs.getInt("quantity_in_stock");
                int reorderLevel = rs.getInt("reorder_level");

                if (transactionType.equals("OUT")) {
                    if (quantityInStock - quantity < reorderLevel) {

                        response.getWriter().write("{\"success\": false, \"message\": \"Quantity falls below reorder level\"}");


                        throw new Exception("Transaction failed: Quantity falls below reorder level");

                    }
                }

                // Update the quantity in stock in Items table

                    String updateItemsSql = "UPDATE Items SET quantity_in_stock = quantity_in_stock + ? WHERE item_id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateItemsSql);
                    updateStmt.setInt(1, transactionType.equals("IN") ? quantity : -quantity);
                    updateStmt.setString(2, itemId);
                    updateStmt.executeUpdate();

                    // Insert into Stock_Transactions table
                    String insertTransactionSql = "INSERT INTO Stock_Transactions (transaction_id, item_id, transaction_type, quantity, transaction_date, staff_id, org_id) VALUES (?, ?, ?, ?, NOW(), ?, ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insertTransactionSql);

                    insertStmt.setString(1, transactionId);
                    insertStmt.setString(2, itemId);
                    insertStmt.setString(3, transactionType);
                    insertStmt.setInt(4, quantity);
                    insertStmt.setString(5, staffId);
                    insertStmt.setString(6, orgId);
                    insertStmt.executeUpdate();



                // Commit the transaction
                conn.commit();

                response.getWriter().write("{\"success\": true, \"message\": \"Transaction successful\"}");
            } else {
                throw new Exception("Transaction failed: Item not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //response.getWriter().write("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        } finally {
            try {
                conn.close(); // Ensure you close the DB connection and statement
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

