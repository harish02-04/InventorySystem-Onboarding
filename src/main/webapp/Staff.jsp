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
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Staff</title>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
  <link rel="stylesheet" type="text/css" href="Styles/Staff.css">
  <script>
    function processTransaction(event) {
      event.preventDefault(); // Prevent form submission

      const token = '<%= session.getAttribute("token") %>';

      if (!token) {
        // Redirect to login if no token is found
        alert("Unauthorized access! Redirecting to login...");
        window.location.href = "index.jsp";
        return;
      }

      // Gather form data
      const itemId = document.querySelector('input[name="item_id"]').value;
      const transactionType = document.querySelector('select[name="transaction_type"]').value;
      const quantity = document.querySelector('input[name="quantity"]').value;

      // Send the POST request using Fetch API
      fetch("ProcessTransaction", {
        method: "POST",
        headers: {
          "Authorization": "Bearer " + token, // Include the JWT token in the request headers
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          token:token,
          item_id: itemId,
          transaction_type: transactionType,
          quantity: quantity
        })
      })
              .then(response => {
                if (response.ok) {
                  return response.json();
                } else if (response.status === 401) {
                  // If unauthorized, redirect to login
                  window.location.href = "login.jsp";
                }
                throw new Error("Transaction failed.");
              })
              .then(data => {
                document.getElementById('tranForm').reset();
                alert("Transaction successful");
              })
              .catch(error => {
                console.error("Error:", error);
                alert("Error processing transaction: " + error.message);
              });
    }

    function fetchInventory() {
      const token = '<%= token %>';

      fetch('FetchInventory', {
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

                </tr>
            `;

      transactionData.forEach(tran => {
        const row = document.createElement('tr');

        row.innerHTML =
                '<td>' + (tran.transaction_id || '') + '</td>' +
                '<td>' + (tran.item_id || '') + '</td>' +
                '<td>' + (tran.transaction_type || '') + '</td>' +
                '<td>' + (tran.quantity || '') + '</td>' +
                '<td>' + (tran.transaction_date || '') + '</td>' ;

        table.appendChild(row);
      });

      // Append the table to a div or body
      document.getElementById('transactionTableContainer').innerHTML = '';
      document.getElementById('transactionTableContainer').appendChild(table);
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
<div style="margin: 50px;">
<h2>Make Transaction</h2>
<form onsubmit="processTransaction(event)" id="tranForm">

  <input type="text" id="item_id" name="item_id" required placeholder="Item ID">
  <br><br>

  <label for="transaction_type">Transaction Type:</label>
  <select id="transaction_type" name="transaction_type" required>
    <option value="IN">IN</option>
    <option value="OUT">OUT</option>
  </select>
  <br><br>


  <input type="number" id="quantity" name="quantity" required placeholder="Quantity">
  <br><br>

  <button type="submit">Submit</button>
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
