<?php
$con = mysqli_connect("solarpaygogo.com","spgg_user","spgg_user","solarpaygogo_db");

if (mysqli_connect_errno($con))
{
	echo "Failed to connect to MySQL: " . mysqli_connect_error();
}
$id = $_GET['id'];
$password = $_GET['password'];


$sql = "UPDATE user SET password =" . $password . " WHERE id =" . $id;

if ($con->query($sql) === TRUE) {
    echo "SUCCESS";
} else {
    echo "Error: " . $sql . "<br>" . $con->error;
}

mysqli_close($con);
?>