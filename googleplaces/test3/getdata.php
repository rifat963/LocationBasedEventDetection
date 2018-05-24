<?php

require_once 'GooglePlaces.php';
$google_places = new GooglePlaces('AIzaSyAzpjIr3JJPX-_3051t-PyMwAPfsanKVJs');

$google_places->location = array(-33.86820, 151.1945860);
$google_places->radius   = 800;
$results                 = $google_places->nearbySearch();


print_r($results);


?>

