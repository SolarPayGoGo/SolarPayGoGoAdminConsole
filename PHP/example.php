<?php
$servername = "42.2.29.246";
$username = "spgg_user";
$password = "spgg_user";
$dbname = "solarpaygogo_db";

// Create connection
$conn = mysqli_connect($servername, $username, $password, $dbname);
// Check connection
if (!$conn) {
   	echo("Connection failed: " . mysqli_connect_error());
}

$sql = "SELECT id, firstName, lastName FROM user";
$result = mysqli_query($conn, $sql);

if (mysqli_num_rows($result) > 0) {
    // output data of each row
    while($row = mysqli_fetch_assoc($result)) {
        echo "id: " . $row["id"]. " - Name: " . $row["firstName"]. " " . $row["lastName"]. "<br>";
    }
} else {
    echo "0 results";
}

mysqli_close($conn);
?>