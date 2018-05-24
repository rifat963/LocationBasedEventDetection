<?php 

	require_once("FoursquareAPI.class.php");
     
        $location = "Quelli Del Bar Castello,italy";
	// Set your client key and secret
	$client_key = "M421SQPJS0YV5PZA2ZFSKZUTVSWKJAAOTNESIPXXMYCUSEBV"; // Your Client ID
	$client_secret = "ZG4FKJZYXLVQ2EDLQ3SSF3GPW5OGWTT0X33RI2MHGGQCPYQA"; // Your Client Secret
	// Load the Foursquare API library
	$foursquare = new FoursquareAPI($client_key,$client_secret);
	
	// Generate a latitude/longitude pair using Google Maps API
	list($lat,$lng) = $foursquare->GeoLocate($location);
	
        $lat="45.18955148396"; $lng="9.1634804496959";
	
	// Prepare parameters
	$params = array("ll"=>"$lat,$lng","limit"=>"5","radius"=>"200");
	
	// Perform a request to a public resource
	$response = $foursquare->GetPublic("venues/search",$params);
        //echo $response;
	$venues = json_decode($response);
        
  
        // Start XML file, echo parent node
  
				
	foreach($venues->response->venues as $venue):
					
           // echo '<marker ';
            echo 'lat="' .$venue->location->lat. '" ';
            echo 'lng="' .$venue->location->lng. '" ';
            if($venue->location->lat==$lat && $venue->location->lng==$lng)
            {
                echo 'test';
            }
            
            if(isset($venue->categories['0']))
            {
              //  echo 'icon="' .$venue->categories['0']->icon->prefix. '" ';
                //echo '<image class="icon" src="'.$venue->categories['0']->icon->prefix.'88.png"/>';
                                    
            }
            else{
		//echo '<image class="icon" src="https://foursquare.com/img/categories/building/default_88.png"/>';
               // echo 'icon="' ."https://foursquare.com/img/categories/building/default_". '" ';
            }
            
            
            if(isset($venue->url) && !empty($venue->url))
            {
	//	echo '<a href="'.$venue->url.'" target="_blank"/><b>';
                echo 'venue_url="' .$venue->url. '" ';
              
                echo 'venue_name="' .$venue->name. '" ';
            
	                             
		}else{
	//	echo $venue->name."<br>";	
                echo 'venue_name="' .$venue->name. '" ';
          
            }
		
            if(isset($venue->categories['0']))
            {
		if(property_exists($venue->categories['0'],"name"))
		{
                        echo 'venue_categories="' .$venue->categories['0']->name. '" ';
          
                	//echo ' <i> '.$venue->categories['0']->name.'</i><br/>';
		}
            }
				
            if(property_exists($venue->hereNow,"count"))
            {
		//echo ''.$venue->hereNow->count ." people currently here <br/> ";
                echo 'venue_herenow_count="' .$venue->hereNow->count. '" ';                
            }
            echo 'venue_stats_userscount="' .$venue->stats->usersCount. '" '; 
            echo 'venue_stats_checkinscount="' .$venue->stats->checkinsCount. '" ';
         endforeach; 
         
        
         
         
         
         
         
         ?>
	
