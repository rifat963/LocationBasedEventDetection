<?php
function parseToXML($htmlStr) 
{ 
            $xmlStr=str_replace('<','&lt;',$htmlStr); 
            $xmlStr=str_replace('>','&gt;',$xmlStr); 
            $xmlStr=str_replace('"','&quot;',$xmlStr); 
            $xmlStr=str_replace("'",'&#39;',$xmlStr); 
            $xmlStr=str_replace("&",'&amp;',$xmlStr); 
            return $xmlStr; 
}
require_once('lib\Factual.php');
ini_set('max_execution_time', 300);
include 'spherical-geometry.class.php'; 
$factual = new Factual("xoTuroWBWXqA8nQrwOqDfj3ukGjgaJfvrvblbchH","W3kEHsD0stAvZNcUQw22k51I4ItmOEP0ZXMiRFCA");

$query = new FactualQuery;


$query->within(new FactualCircle(45.1894664, 9.163067, 5000));
//$query->search("pavia italy");
$query->limit(50);

$res = $factual->fetch("places", $query);  

/*Geocoding and reverse geocoding

 * //geocode (convert an address to longitude and latitude)
$res = $factual->geocode("425 Sherman Ave, Palo Alto, CA, USA");
print_r($res);

//reverse geocode  (convert a longitude and latitude to an address)
$lon = -122.143895;
$lat = 37.425674;
$res = $factual->reverseGeocode($lon,$lat);
print_r($res); 
 * 
 * 
 *  */

include 'spherical-geometry.class.php'; 


$res2 = $res->getJson();

$data2=json_decode($res2);

//print_r($data);
//$res3=$res->getData();

//print_r($res3);

header("Content-type: text/xml");

// Start XML file, echo parent node
echo '<markers>';
foreach ($data2->response->data as $value) {
    
    echo '<marker ';
    echo 'lat="' .$value->latitude. '" ';
    echo 'lng="' .$value->longitude. '" ';

    echo 'name="' .$value->name. '" ';
    echo 'admin_region="' .parseToXML($value->admin_region). '" ';
    echo 'address="' .parseToXML($value->address). '" ';
   
    echo 'category_id="' .$value->category_ids[0]. '" ';
    //echo 'category_id="' .parseToXML($value->category_labels[0][1]). '" ';
    echo '/>';
}
echo '</markers>';


