<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>User Details</title>
</head>
<body>
<h1>User Details</h1>
<table border="1">
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Email</th>
        <th>Age</th>
    </tr>
    <%
        String DB_URL = "jdbc:mysql://localhost:3306/demo";
        String DB_USER = "root"; // Your DB username
        String DB_PASSWORD = "Harish@123"; // Your DB password

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectSQL = "SELECT * FROM users";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectSQL)) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("username");
                    String email = resultSet.getString("email");
    %>
    <tr>
        <td><%= id %></td>
        <td><%= name %></td>
        <td><%= email %></td>

    </tr>
    <%
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    %>
</table>
<a href="index.jsp">Add Another User</a>
</body>
</html>
