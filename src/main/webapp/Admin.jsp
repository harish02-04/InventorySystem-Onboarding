

<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%
    // Check if the token exists in the session
    String token = (String) session.getAttribute("token");

    // If the token is null, redirect to login.jsp
    if (token == null || token.isEmpty()) {
%>
<script type="text/javascript">
    window.location.href = 'index.jsp';
</script>
<%
        return; // Stop the page from rendering
    }
%>

<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">

    <link rel="stylesheet" type="text/css" href="Styles/Admin.css">
    <title>Admin</title>
    <script>
        function addItem(event) {
            event.preventDefault(); // Prevent the default form submission

            // Retrieve the token from the session (injected via JSP)
            const token = '<%= token %>';

            // Gather form data
            const itemName = document.querySelector('input[name="item_name"]').value;
            const itemDescription = document.querySelector('textarea[name="item_description"]').value;
            const category = document.querySelector('input[name="category"]').value;
            const unitPrice = document.querySelector('input[name="unit_price"]').value;
            const quantityInStock = document.querySelector('input[name="quantity_in_stock"]').value;
            const reorderLevel = document.querySelector('input[name="reorder_level"]').value;

            // Send the POST request using Fetch API
            fetch("AddItem", {
                method: "POST",
                headers: {
                    "Authorization": "Bearer " + token, // Include the JWT token in the request headers
                    "Content-Type": "application/json"  // Set content type to JSON
                },
                body: JSON.stringify({
                    token:token,
                    item_name: itemName,
                    item_description: itemDescription,
                    category: category,
                    unit_price: unitPrice,
                    quantity_in_stock: quantityInStock,
                    reorder_level: reorderLevel
                })
            })
                .then(response => {
                    if (response.ok) {
                        return response.json(); // Parse JSON response if the request is successful
                    }
                    throw new Error("Network response was not ok.");
                })
                .then(data => {
                    document.getElementById('itemForm').reset();
                    window.alert("Item added successfully");
                })
                .catch(error => {
                    alert("Error adding item: " + data.message);
                });
        }

        function fetchInventory() {
            const token = '<%= token %>';

            fetch('fetchInventory', {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                }
            })
                .then(response => response.json())
                .then(data => {
                    displayInventoryTable(data);
                })
                .catch(error => {
                    console.error("Error fetching inventory:", error);
                });
        }

        // Function to display inventory details as a table
        function displayInventoryTable(inventoryData) {
            const table = document.createElement('table');
            table.innerHTML = `
                <tr>
                    <th>Item ID</th>
                    <th>Item Name</th>
                    <th>Description</th>
                    <th>Category</th>
                    <th>Unit Price</th>
                    <th>Quantity in Stock</th>
                    <th>Reorder Level</th>
                </tr>
            `;

            inventoryData.forEach(item => {
                const row = document.createElement('tr');

                row.innerHTML =
                    '<td>' + (item.item_id || '') + '</td>' +
                    '<td>' + (item.item_name || '') + '</td>' +
                    '<td>' + (item.item_description || '') + '</td>' +
                    '<td>' + (item.category || '') + '</td>' +
                    '<td>' + (item.unit_price || '') + '</td>' +
                    '<td>' + (item.quantity_in_stock || '') + '</td>' +
                    '<td>' + (item.reorder_level || '') + '</td>';

                table.appendChild(row);
            });

            // Append the table to a div or body
            document.getElementById('inventoryTableContainer').innerHTML = '';
            document.getElementById('inventoryTableContainer').appendChild(table);
        }


        function fetchTransactions() {
            const token = '<%= token %>';

            fetch('FetchTransaction', {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                }
            })
                .then(response => response.json())
                .then(data => {
                    displayTransactionTable(data);
                })
                .catch(error => {
                    console.error("Error fetching inventory:", error);
                });
        }

        function displayTransactionTable(transactionData) {
            const table = document.createElement('table');

            table.innerHTML = `
                <tr>
                    <th>Transaction_ID</th>
                    <th>Item_ID</th>
                    <th>Transaction_Type</th>
                    <th>Quantity</th>
                    <th>Date</th>
                    <th>Done By(Staff_ID)</th>

                </tr>
            `;

            transactionData.forEach(tran => {
                const row = document.createElement('tr');

                row.innerHTML =
                    '<td>' + (tran.transaction_id || '') + '</td>' +
                    '<td>' + (tran.item_id || '') + '</td>' +
                    '<td>' + (tran.transaction_type || '') + '</td>' +
                    '<td>' + (tran.quantity || '') + '</td>' +
                    '<td>' + (tran.transaction_date || '') + '</td>' +
                    '<td>' + (tran.staff_id || '') + '</td>' ;

                table.appendChild(row);
            });

            // Append the table to a div or body
            document.getElementById('transactionTableContainer').innerHTML = '';
            document.getElementById('transactionTableContainer').appendChild(table);
        }


        function adjustInventory(event) {
            event.preventDefault(); // Prevent the default form submission

            const token = '<%= token %>';

            // Gather form data
            const itemId = document.querySelector('input[name="item_id"]').value;
            const newPrice = document.querySelector('input[name="new_price"]').value;
            const newQuantity = document.querySelector('input[name="new_quantity"]').value;

            // Send the POST request using Fetch API
            fetch("AdjustInventory", {
                method: "POST",
                headers: {
                    "Authorization": "Bearer " + token, // Include the JWT token in the request headers
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    item_id: itemId,
                    new_price: newPrice,
                    new_quantity: newQuantity
                })
            })
                .then(response => {
                    if (response.ok) {
                        return response.json(); // Parse JSON response if successful
                    }
                    throw new Error("Network response was not ok.");
                })
                .then(data => {
                    if (data.success) {

                        alert("Inventory adjusted successfully!");
                        document.getElementById('adjustForm').reset();
                    } else {
                        alert("Error adjusting inventory: " + data.message);
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                });
        }

        window.onload = function() {
            const token = '<%= token %>';
            fetch('infoServlet', {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/json'
                }
            })
                .then(response => response.json())  // Parse the JSON response
                .then(data => {
                    // Update the HTML content with the fetched company name and role
                    console.log(data.companyName);
                    document.getElementById('companyName').innerText = data.companyName;
                    document.getElementById('userRole').innerText = data.userRole;
                })
                .catch(error => {
                    console.error('Error fetching company name and role:', error);
                });
        }
        function logout() {
            // Use fetch API to trigger the logout servlet
            fetch('Logout', {
                method: 'GET', // Sending a GET request
                credentials: 'include' // Include credentials (cookies, etc.)
            })
                .then(response => {
                    if (response.redirected) {
                        // Redirect if the response indicates a redirection
                        window.location.href = response.url;
                    }
                })
                .catch(error => {
                    console.error('Error during logout:', error);
                });
        }
    </script>
</head>
<body>
<nav class="navbar">
    <div class="navbar-left">
        <h1 class="company-name" id="companyName"></h1>
    </div>
    <div class="navbar-right">
        <div class="profile-section">
            <i class="fas fa-user-circle"></i>
            <span class="profile-name" id="userRole"></span>
        </div>
        <button class="logout-btn" onclick="logout()">Logout</button>
    </div>
</nav>
<br/>
<div style="margin: 50px">
<h2 style="margin-right: 1100px;">Add Item</h2>
<form onsubmit="addItem(event)" id="itemForm">
    <input type="text" name="item_name" required placeholder="Item Name">
    <textarea name="item_description" placeholder="Item Description"></textarea>
    <input type="text" name="category" required placeholder="Category">
    <input type="number" name="unit_price" required placeholder="Unit Price">
    <input type="number" name="quantity_in_stock" required placeholder="Quantity in Stock">
    <input type="number" name="reorder_level" required placeholder="Reorder Level">
    <button type="submit">Add Item</button>
</form>

</br></br>

<h2 style="margin-right: 1010px;">Adjust Inventory</h2>
<form onsubmit="adjustInventory(event)" id="adjustForm">
    <input type="text" name="item_id" required placeholder="Item ID">
    <input type="number" name="new_price" placeholder="New Price (Optional)">
    <input type="number" name="new_quantity" placeholder="New Quantity (Optional)">
    <button type="submit">Adjust</button>
</form>
</br></br>
<button onclick="fetchInventory()">View Inventory</button>
</br>
<div id="inventoryTableContainer"></div>
</br></br></br></br>

<button onclick="fetchTransactions()">View Transactions</button>
</br>
<div id="transactionTableContainer"></div>
</div>
</body>
</html>
