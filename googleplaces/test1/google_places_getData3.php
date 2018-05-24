<?php
require_once('googlePlaces.php');

$apiKey       = 'AIzaSyAzpjIr3JJPX-_3051t-PyMwAPfsanKVJs';
$googlePlaces = new googlePlaces($apiKey);

// Set the longitude and the latitude of the location you want to search near for places
$latitude   = '-33.8804166';
$longitude = '151.2107662';
$googlePlaces->setLocation($latitude . ',' . $longitude);

$googlePlaces->setRadius(5000);

$results = $googlePlaces->Search();
//echo $results;
var_dump($results);
?>