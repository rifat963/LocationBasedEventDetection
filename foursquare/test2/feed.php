<?php
require('EpiCurl.php');
require('EpiOAuth.php');
require('EpiFoursquare.php');

//$fsObj = new EpiFoursquare();


  $fsObj = new EpiFoursquare();
  $venues = $fsObj->get_basic('/users/search', array('twitter' => 'naveen'));
  echo $venues->responseText;
/*try{
	$venues = $fsObj->get_basic('/venues.json', 
			array('geolat' => '30.268056',
			'geolong' => '-97.741381',
			'l' => '50'));
			
	$venues_array = json_decode($venues->responseText);
		
}catch(Exception $e){
	echo "Error: ". $e;
}*/

?>
