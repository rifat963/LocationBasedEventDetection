<?php
//require("phpsqlinfo_dbinfo.php");
$username="root";
$password="";
$database="point_of_interest";



// Gets data from URL parameters
$name = $_GET['name'];
$Description = $_GET['Description'];
$lat = $_GET['lat'];
$lng = $_GET['lng'];
$starttime=$_GET['starttime'];
$endtime=$_GET['endtime'];
$startdate=$_GET['startdate'];
$enddate=$_GET['enddate'];
$type = $_GET['type'];

// Opens a connection to a MySQL server
$connection = mysql_connect ("localhost", $username, $password);
if (!$connection) {
  die('Not connected : ' . mysql_error());
}

// Set the active MySQL database
$db_selected = mysql_select_db($database, $connection);
if (!$db_selected) {
  die ('Can\'t use db : ' . mysql_error());
}

// Insert new row with user data
$query = sprintf("INSERT INTO eventsdetails " .
         " (id, name, Description, lat, lng,starttime,endtime,startdate,enddate, type ) " .
         " VALUES (NULL, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
         mysql_real_escape_string($name),
         mysql_real_escape_string($Description),
         mysql_real_escape_string($lat),
         mysql_real_escape_string($lng),
         mysql_real_escape_string($starttime),
         mysql_real_escape_string($endtime),
       
        mysql_real_escape_string($startdate),
        mysql_real_escape_string($enddate),
        mysql_real_escape_string($type));

$result = mysql_query($query);

if (!$result) {
  die('Invalid query: ' . mysql_error());
}



