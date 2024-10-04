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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/FetchInventory")
public class FetchInventoryServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader("Authorization");
        token = token.substring(7);

        String org_id= JwtUtil.getOrgId(token);
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM Items WHERE org_id='"+org_id+"'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            List<Map<String, Object>> inventoryList = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();

                item.put("item_id", rs.getString("item_id"));
                item.put("item_name", rs.getString("item_name"));
                item.put("item_description", rs.getString("item_description"));
                item.put("category", rs.getString("category"));
                item.put("unit_price", rs.getBigDecimal("unit_price"));
                item.put("quantity_in_stock", rs.getInt("quantity_in_stock"));
                item.put("reorder_level", rs.getInt("reorder_level"));
                inventoryList.add(item);
            }


            // Send the inventory list as a JSON response
            PrintWriter out = response.getWriter();
            out.write(new com.google.gson.Gson().toJson(inventoryList));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching inventory data");
        }
    }
}
