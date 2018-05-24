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
	require_once("FoursquareAPI.class.php");
     
        $location = "pavia";
	// Set your client key and secret
	$client_key = "M421SQPJS0YV5PZA2ZFSKZUTVSWKJAAOTNESIPXXMYCUSEBV"; // Your Client ID
	$client_secret = "ZG4FKJZYXLVQ2EDLQ3SSF3GPW5OGWTT0X33RI2MHGGQCPYQA"; // Your Client Secret
	// Load the Foursquare API library
	$foursquare = new FoursquareAPI($client_key,$client_secret);
	
	// Generate a latitude/longitude pair using Google Maps API
	list($lat,$lng) = $foursquare->GeoLocate($location);
	
	
	// Prepare parameters
	$params = array("ll"=>"$lat,$lng","limit"=>"100","radius"=>"10000");
	
	// Perform a request to a public resource
	$response = $foursquare->GetPublic("venues/search",$params);
       // echo $response;
	$venues = json_decode($response);
        
       // print_r($venues);
        header("Content-type: text/xml");

        // Start XML file, echo parent node
        echo '<markers>';

				
	foreach($venues->response->venues as $venue):
					
            echo '<marker ';
            echo 'lat="' .$venue->location->lat. '" ';
            echo 'lng="' .$venue->location->lng. '" ';
            if(isset($venue->location->address))
            echo 'address="' .parseToXML($venue->location->address). '" ';
            
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
                echo 'venue_url="' .parseToXML($venue->url). '" ';
              
                echo 'venue_name="' .parseToXML($venue->name). '" ';
            
	                             
		}else{
	//	echo $venue->name."<br>";	
                echo 'venue_name="' .parseToXML($venue->name). '" ';
          
            }
		
            if(isset($venue->categories['0']))
            {
		if(property_exists($venue->categories['0'],"name"))
		{
                        echo 'venue_categories="' .parseToXML($venue->categories['0']->name). '" ';
          
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
      //      echo 'rating="' .$venue->rating. '" ';
            //echo '<b><i>History</i></b> :'.$venue->stats->usersCount." visitors , ".$venue->stats->checkinsCount." visits ";
           // echo '<b><i>like</i></b>'.$venue->likes->count;
            
            echo '/>';
         endforeach; 
         
         echo '</markers>';
         
         ?>
	
