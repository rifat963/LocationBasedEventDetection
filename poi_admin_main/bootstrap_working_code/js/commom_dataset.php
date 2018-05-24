<?php

$username='root';
$password='';
$database='point_of_interest';
include 'spherical-geometry.class.php';
ini_set('max_execution_time', 300);
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

//header("Content-type: text/xml");

$count_fb=0;
$lat_string_array_fb=array();
$lng_string_array_fb=array();

$name_stirng_array_fb=array();

while ($row_fb = @mysql_fetch_assoc($result_fb)){
 
    $lat1=$row_fb['lat'];
    $lng1=$row_fb['lng'];
    $name_fb=$row_fb['name'];
    //$s1=new LatLng($lat1, $lng1);
    $tt=$lat1.",".$lng1;
    array_push($lat_string_array_fb, $lat1);
    array_push($lng_string_array_fb, $lng1);
    array_push($name_stirng_array_fb, $name_fb);
    $count_fb++;
    
}

echo $count_fb;
$count_sq=0;
$lat_string_array_sq=array();
$lng_string_array_sq=array();

$name_stirng_array_sq=array();

while ($row_foursquare = mysql_fetch_array($result_foursquare)) {
     
    $lat2=$row_foursquare['lat'];
    $lng2=$row_foursquare['lng'];
    $name_sq=$row_fb['name'];
    //$s2=new LatLng($lat2, $lng2);
    array_push($lat_string_array_sq, $lat2);
    array_push($lng_string_array_sq, $lng2);
    array_push($name_stirng_array_sq, $name_sq);
    
    //$p= SphericalGeometry::computeDistanceBetween($s2, $s1);
    $count_sq++;
 }
echo $count_sq;
$min=0;
$index=0;
$min=array();
for($i=0;$i<$count_fb;$i++){
 
    $latf=$lat_string_array_fb[$i];
    $lngf=$lng_string_array_fb[$i];
    $namefb=$name_stirng_array_fb[$i];
    $s1=new LatLng($latf, $lngf);
    
    
    for($j=0;$j<$count_sq;$j++){
        //echo $position_array[$i];
         $lats=$lat_string_array_sq[$j];
         $lngs=$lng_string_array_sq[$j];
         
         $namesq=$name_stirng_array_sq[$j];
         
         $s2=new LatLng($lats, $lngs);
         $p= SphericalGeometry::computeDistanceBetween($s1, $s2);
         array_push($min, $p);
    }
    
    $min_value=0;
    $index=0;
    for($k=0;$k<$count_sq;$k++){
        
        $temp=$min[$k];
        if($temp<$min[$k]){
            $min_value=$temp;
            $index=$k;
        }
    }
     
    echo $name_stirng_array_sq[$index];
    
}

// End XML file
//echo '</markers>';

?>