<?php
require('places_class.php');

	$gplaces = New GooglePlaces;
	$gplaces->SetLocation("40.5267,81.4778");
	$gplaces->SetRadius(50);
	$gplaces->SetTypes("food");
	$results = $gplaces->Search();
	
        print_r($results);	

?>