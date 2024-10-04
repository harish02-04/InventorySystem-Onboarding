package com.example.javaservlets.Common;

import com.example.javaservlets.Authentication.JwtUtil;
import com.example.javaservlets.Database.DBUtil;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@WebServlet("/FetchTransaction")
public class FetchTransactionServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader("Authorization");
        token = token.substring(7);

        String org_id = JwtUtil.getOrgId(token);
        String role = JwtUtil.getRole(token);
        String staff_id = JwtUtil.getStaff_id(token);
        if (Objects.equals(role, "ADMIN")) {
            try (Connection conn = DBUtil.getConnection()) {
                String sql = "SELECT * FROM Stock_Transactions WHERE org_id='" + org_id + "'";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                List<Map<String, Object>> inventoryList = new ArrayList<>();

                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();

                    item.put("transaction_id", rs.getString("transaction_id"));
                    item.put("item_id", rs.getString("item_id"));
                    item.put("transaction_type", rs.getString("transaction_type"));
                    item.put("quantity", rs.getString("quantity"));
                    item.put("transaction_date", rs.getDate("transaction_date"));
                    item.put("staff_id", rs.getString("staff_id"));
                    inventoryList.add(item);
                }


                // Send the inventory list as a JSON response
                PrintWriter out = response.getWriter();
                out.write(new com.google.gson.Gson().toJson(inventoryList));
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching Transaction data");
            }
        }
        else if(Objects.equals(role, "STAFF")){
            try (Connection conn = DBUtil.getConnection()) {
                String sql = "SELECT * FROM Stock_Transactions WHERE staff_id='" + staff_id + "'";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                List<Map<String, Object>> inventoryList = new ArrayList<>();

                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();

                    item.put("transaction_id", rs.getString("transaction_id"));
                    item.put("item_id", rs.getString("item_id"));
                    item.put("transaction_type", rs.getString("transaction_type"));
                    item.put("quantity", rs.getString("quantity"));
                    item.put("transaction_date", rs.getDate("transaction_date"));
                    inventoryList.add(item);
                }


                // Send the inventory list as a JSON response
                PrintWriter out = response.getWriter();
                out.write(new com.google.gson.Gson().toJson(inventoryList));
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching Transaction data");
            }
        }

    }

}
