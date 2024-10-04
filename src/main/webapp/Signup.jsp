<!DOCTYPE html>
<html>

<head>
    <link rel="stylesheet" type="text/css" href="Styles/Signup.css">
    <title>Signup</title>
</head>
<body>
<h2>Admin Signup</h2>
<form action="AdminSignup" method="post">
    <input type="text" name="org_name" placeholder="Organization Name" required>
    <input type="email" name="org_email" placeholder="Organization Email" required>
    <input type="text" name="admin_name" placeholder="Admin Name" required>
    <input type="email" name="admin_email" placeholder="Admin Email" required>
    <input type="password" name="password" placeholder="Password" required>
    <button type="submit">Sign Up</button>
</form>

<h2>Staff Signup</h2>
<form action="StaffSignup" method="post">
    <input type="text" name="staff_name" placeholder="Staff Name" required>
    <input type="email" name="staff_email" placeholder="Staff Email" required>
    <input type="password" name="password" placeholder="Password" required>
    <input type="text" name="org_id" placeholder="Organization ID" required>
    <button type="submit">Sign Up</button>
</form>
</body>
</html>
