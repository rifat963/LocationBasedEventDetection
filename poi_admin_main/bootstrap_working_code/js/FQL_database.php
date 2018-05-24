<?php

$username='root';
$password='';
$database='point_of_interest';
function parseToXML($htmlStr) 
{ 
$xmlStr=str_replace('<','&lt;',$htmlStr); 
$xmlStr=str_replace('>','&gt;',$xmlStr); 
$xmlStr=str_replace('"','&quot;',$xmlStr); 
$xmlStr=str_replace("'",'&#39;',$xmlStr); 
$xmlStr=str_replace("&",'&amp;',$xmlStr); 
return $xmlStr; 
} 

// Opens a connection to a MySQL server
$connection=mysql_connect ('localhost', $username, $password);
if (!$connection) {
  die('Not connected : ' . mysql_error());
}

// Set the active MySQL database
$db_selected = mysql_select_db($database, $connection);
if (!$db_selected) {
  die ('Can\'t use db : ' . mysql_error());
}

// Select all the rows in the markers table
$query_fb = "SELECT * FROM poi_facebook WHERE 1";
$query_foursquare = "SELECT * FROM poi_foursquare_extend WHERE 1";
$result_fb = mysql_query($query_fb);
$result_foursquare = mysql_query($query_foursquare);

if (!$result_fb) {
  die('Invalid query: ' . mysql_error());
}

header("Content-type: text/xml");

// Start XML file, echo parent node
echo '<markers>';

// Iterate through the rows, printing XML nodes for each
//while ($row_fb = @mysql_fetch_assoc($result_fb)){
  // ADD TO XML DOCUMENT NODE
  //echo '<marker ';
  //echo 'name="' . parseToXML($row['name']) . '" ';
 // echo 'address="' . parseToXML($row['address']) . '" ';
  
 while ($row_fb = mysql_fetch_array($result_fb)) {
  //   echo $row_foursquare['venue_name'];
  //   echo $row_fb['name'];
     
     
  //   if($row_fb['name']==$row_foursquare['venue_name']){
         
    // }
     echo '<marker ';
     echo 'lat="' .$row_fb['lat']. '" ';
     echo 'lng="' .$row_fb['lng']. '" ';
 
     echo 'name="' .parseToXML($row_fb['name']). '" ';
     echo 'id="' .parseToXML($row_fb['fb_id']). '" ';       
     echo 'street="' .parseToXML($row_fb['address']). '" ';
     echo 'category="' .parseToXML($row_fb['category']). '" ';
     echo 'checkin_count="' .$row_fb['checkin_count']. '" ';
    // echo 'venue_stats_userscount="' .$row_foursquare['venue_stats_userscount']. '" '; 
   //  echo 'venue_stats_checkinscount="' .$row_foursquare['venue_stats_checkinscount']. '" ';
  
  echo '/>';
}

// End XML file
echo '</markers>';

?>