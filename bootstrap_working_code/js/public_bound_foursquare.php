<?php 

	require_once("FoursquareAPI.class.php");
        ini_set('max_execution_time', 300);
        include 'spherical-geometry.class.php';
     
        $username="root";
        $password="";
        $database="point_of_interest";



        $connection = mysql_connect ("localhost", $username, $password);
        if (!$connection) {
            die('Not connected : ' . mysql_error());
        }

        // Set the active MySQL database
        $db_selected = mysql_select_db($database, $connection);
        if (!$db_selected) {
            die ('Can\'t use db : ' . mysql_error());
        }
        
        $query_del=  sprintf("DELETE FROM `poi_foursquare_extend` WHERE 1");
        $test_2 = mysql_query($query_del);
        
        
        
        $location = "pavia";
	// Set your client key and secret
	$client_key = "M421SQPJS0YV5PZA2ZFSKZUTVSWKJAAOTNESIPXXMYCUSEBV"; // Your Client ID
	$client_secret = "ZG4FKJZYXLVQ2EDLQ3SSF3GPW5OGWTT0X33RI2MHGGQCPYQA"; // Your Client Secret
	// Load the Foursquare API library
	$foursquare = new FoursquareAPI($client_key,$client_secret);
       
	// Generate a latitude/longitude pair using Google Maps API
	list($lat,$lng) = $foursquare->GeoLocate($location);
	// Prepare parameters
	$params_center = array("ll"=>"$lat,$lng","limit"=>"50");
	
        /* Bound Values */
        $event=new LatLng($lat, $lng);
   
        /* Code for calculating bounds   */
  
        $radius=300;
        $LatLng=$event;
        
        $latRadiansDistance = $radius / 6378137;
        $latDegreesDistance = rad2deg($latRadiansDistance);
        $lngDegreesDistance = rad2deg($latRadiansDistance / cos(deg2rad($LatLng->getLat())));
    
        // SW point
        $swLat = $LatLng->getLat() - $latDegreesDistance;
        $swLng = $LatLng->getLng() - $lngDegreesDistance;
        $sw = new LatLng($swLat, $swLng);
        // NE point
        $neLat = $LatLng->getLat() + $latDegreesDistance;
        $neLng = $LatLng->getLng() + $lngDegreesDistance;
        $ne = new LatLng($neLat, $neLng);
        // SE point
        $seLat = $LatLng->getLat() - $latDegreesDistance;
        $seLng = $LatLng->getLng() + $lngDegreesDistance;
        $se = new LatLng($seLat, $seLng);
    
        //NW
        $nwLat = $LatLng->getLat() + $latDegreesDistance;
        $nwLng = $LatLng->getLng() - $lngDegreesDistance;
        $nw = new LatLng($nwLat, $nwLng);
        //echo $nw->getLat();
        
	// Perform a request to a public resource
	$response_center = $foursquare->GetPublic("venues/search",$params_center);
        // echo $response;
	$venues_center = json_decode($response_center);
        
        
        $params_sw = array("ll"=>"$swLat,$swLng","limit"=>"50");
	$params_ne = array("ll"=>"$neLat,$neLng","limit"=>"50");
	$params_se = array("ll"=>"$seLat,$seLng","limit"=>"50");
	$params_nw = array("ll"=>"$nwLat,$nwLng","limit"=>"50");
	
        
        
        $response_center = $foursquare->GetPublic("venues/search",$params_center);
        //$response_sw = $foursquare->GetPublic("venues/search",$params_sw);
       // echo $response_sw;
        
        // echo $response;
	$venues_center = json_decode($response_center);
        
        $response_sw = $foursquare->GetPublic("venues/search",$params_sw);
        // echo $response;
	$venues_sw = json_decode($response_sw);
        //print_r($venues_sw);
        
        $response_ne = $foursquare->GetPublic("venues/search",$params_ne);
        // echo $response;
	$venues_ne = json_decode($response_ne);
        
        
        $response_se = $foursquare->GetPublic("venues/search",$params_se);
        // echo $response;
	$venues_se = json_decode($response_se);
        
        
        $response_nw = $foursquare->GetPublic("venues/search",$params_nw);
         //echo $response_nw;
	$venues_nw = json_decode($response_nw);
        
       // $check_array=array();
       
        // print_r($venues);
        //header("Content-type: text/xml");

        // Start XML file, echo parent node
        //echo '<markers>'
        //$a=array("red","green");
        //array_push($a,"blue","yellow");
        $check_array_lat=array($LatLng->getLat());
        $check_array_lng=array($LatLng->getLng());
        $address=0;  
        $count=0;
	foreach($venues_center->response->venues as $venue):
					
           // echo '<marker ';
            
            //echo 'lat="' .$venue->location->lat. '" ';
            //echo 'lng="' .$venue->location->lng. '" ';
            
           // $check_array_lat=array($venue->location->lat);
            array_push($check_array_lat, $venue->location->lat);
            array_push($check_array_lng, $venue->location->lng);
            
            if(isset($venue->location->address))
                $address=$venue->location->address;
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
                //echo '<a href="'.$venue->url.'" target="_blank"/><b>';
                //echo 'venue url="' .$venue->url. '" ';
              
                //echo 'venue name="' .$venue->name. '" ';
                  
                $venue_url=$venue->url;
                $venue_name=$venue->name;
                
		}else{
		//echo $venue->name."<br>";	
                //echo 'venue name="' .$venue->name. '" ';
                $venue_name=$venue->name;
                $venue_url="";
          
            }
		
            if(isset($venue->categories['0']))
            {
		if(property_exists($venue->categories['0'],"name"))
		{
             //           echo 'venue categories="' .$venue->categories['0']->name. '" ';
                        $venue_category=$venue->categories['0']->name;
                	//echo ' <i> '.$venue->categories['0']->name.'</i><br/>';
		}
            }
				
            if(property_exists($venue->hereNow,"count"))
            {
		//echo ''.$venue->hereNow->count ." people currently here <br/> ";
           //     echo 'venue herenow count="' .$venue->hereNow->count. '" ';  
                $venue_hereNow_count=$venue->hereNow->count;
            }
       //     echo 'venue stats userscount="' .$venue->stats->usersCount. '" '; 
         //   echo 'venue stats checkinscount="' .$venue->stats->checkinsCount. '" ';
            
         $query = sprintf("INSERT INTO poi_foursquare_extend " .
         " (id, lat,lng, venue_url,venue_name,venue_categories,venue_stats_userscount,venue_stats_checkinscount,venue_herenow_count,address) " .
         " VALUES (NULL, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s','%s');",
         mysql_real_escape_string($venue->location->lat),
         mysql_real_escape_string($venue->location->lng),
         mysql_real_escape_string($venue_url),
         mysql_real_escape_string($venue_name),
         mysql_real_escape_string($venue_category),
         mysql_real_escape_string($venue->stats->usersCount),
         mysql_real_escape_string($venue->stats->checkinsCount),
         mysql_real_escape_string($venue_hereNow_count),
         mysql_real_escape_string($address));
         $test = mysql_query($query);
            
            
         $count++;
         
         endforeach; 
        // print_r($check_array_lng);
         
         echo '<hr>';
         
         foreach($venues_ne->response->venues as $venue_ne):
                
                $flag=0;
                for($i=0;$i<$count;$i++){
                    //echo $check_array_lat[$i];
                    
                    if($check_array_lat[$i]==$venue_ne->location->lat && $check_array_lng[$i]==$venue_ne->location->lng){
                        
                        $flag=1;
                    } 
                }
             
                if($flag==0){
                    
                    //echo 'lat="' .$venue_ne->location->lat. '" ';
                    //echo 'lng="' .$venue_ne->location->lng. '" ';
                    if(isset($venue_ne->location->address))
                    $address=$venue_ne->location->address;
                    if(isset($venue_ne->url) && !empty($venue_ne->url))
                    {
                        //echo 'venue url="' .$venue_ne->url. '" ';
                        //echo 'venue name="' .$venue_ne->name. '" ';
                        $venue_url=$venue_ne->url;
                        $venue_name=$venue_ne->name;
                                   
                    }else{
                        
                        //echo 'venue name="' .$venue_ne->name. '" ';
                        $venue_name=$venue_ne->name;
                      
                        $venue_url="";
                    }
                    
                    if(isset($venue_ne->categories['0']))
                    {
                        if(property_exists($venue_ne->categories['0'],"name"))
                        {
                            //echo 'venue categories="' .$venue_ne->categories['0']->name. '" ';
                            $venue_category=$venue_ne->categories['0']->name;
                        }
                    }
				
                    if(property_exists($venue_ne->hereNow,"count"))
                    {
	                //echo 'venue herenow count="' .$venue_ne->hereNow->count. '" ';                
                        $venue_hereNow_count=$venue_ne->hereNow->count;
                        
                    }
                    //echo 'venue stats userscount="' .$venue_ne->stats->usersCount. '" '; 
                    //echo 'venue stats checkinscount="' .$venue_ne->stats->checkinsCount. '" ';
                    $query_ne = sprintf("INSERT INTO poi_foursquare_extend " .
                    " (id, lat,lng, venue_url,venue_name,venue_categories,venue_stats_userscount,venue_stats_checkinscount,venue_herenow_count) " .
                    " VALUES (NULL, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                    mysql_real_escape_string($venue_ne->location->lat),
                    mysql_real_escape_string($venue_ne->location->lng),
                    mysql_real_escape_string($venue_url),
                    mysql_real_escape_string($venue_name),
                    mysql_real_escape_string($venue_category),
                    mysql_real_escape_string($venue_ne->stats->usersCount),
                    mysql_real_escape_string($venue_ne->stats->checkinsCount),
                    mysql_real_escape_string($venue_hereNow_count),
                    mysql_real_escape_string($address));
                $test2 = mysql_query($query_ne);                    
                }  else {
                    
                    $flag=0;    
                }
         
                
         
            endforeach;
         
         echo '<hr>';
         
         foreach($venues_sw->response->venues as $venue_sw):
                
                $flag=0;
                for($i=0;$i<$count;$i++){
                    //echo $check_array_lat[$i];
                    
                    if($check_array_lat[$i]==$venue_sw->location->lat && $check_array_lng[$i]==$venue_sw->location->lng){
                        
                        $flag=1;
                    } 
                }
             
                if($flag==0){
                    
                    //echo 'lat="' .$venue_sw->location->lat. '" ';
                    //echo 'lng="' .$venue_sw->location->lng. '" ';
                    if(isset($venue_sw->location->address))
                    $address=$venue_sw->location->address;
                    if(isset($venue_sw->url) && !empty($venue_sw->url))
                    {
                        //echo 'venue url="' .$venue_sw->url. '" ';
                        //echo 'venue name="' .$venue_sw->name. '" ';
                        $venue_url=$venue_sw->url;
                        $venue_name=$venue_sw->name;
                        
                    }else{
                        
                        //echo 'venue name="' .$venue_sw->name. '" ';
                        $venue_name=$venue_sw->name;
                        $venue_url="";
                    }
                    
                    if(isset($venue_sw->categories['0']))
                    {
                        if(property_exists($venue_sw->categories['0'],"name"))
                        {
                            //echo 'venue categories="' .$venue_sw->categories['0']->name. '" ';
                            $venue_category=$venue_sw->categories['0']->name;
                            
                        }
                    }
				
                    if(property_exists($venue_sw->hereNow,"count"))
                    {
	                //echo 'venue herenow count="' .$venue_sw->hereNow->count. '" ';                
                        $venue_hereNow_count=$venue_sw->hereNow->count;
       
                    }
                    //echo 'venue stats userscount="' .$venue_sw->stats->usersCount. '" '; 
                    //echo 'venue stats checkinscount="' .$venue_sw->stats->checkinsCount. '" ';
                    $query_sw = sprintf("INSERT INTO poi_foursquare_extend " .
                    " (id, lat,lng, venue_url,venue_name,venue_categories,venue_stats_userscount,venue_stats_checkinscount,venue_herenow_count) " .
                    " VALUES (NULL, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                    mysql_real_escape_string($venue_sw->location->lat),
                    mysql_real_escape_string($venue_sw->location->lng),
                    mysql_real_escape_string($venue_url),
                    mysql_real_escape_string($venue_name),
                    mysql_real_escape_string($venue_category),
                    mysql_real_escape_string($venue_sw->stats->usersCount),
                    mysql_real_escape_string($venue_sw->stats->checkinsCount),
                    mysql_real_escape_string($venue_hereNow_count),
                    mysql_real_escape_string($address));

                    $test2 = mysql_query($query_sw);
                    
                    
                }  else {
                    
                    $flag=0;    
                }
                
                
            endforeach;
         
            echo '<hr>';
         
         foreach($venues_se->response->venues as $venue_se):
                
                $flag=0;
                for($i=0;$i<$count;$i++){
                    //echo $check_array_lat[$i];
                    
                    if($check_array_lat[$i]==$venue_se->location->lat && $check_array_lng[$i]==$venue_se->location->lng){
                        
                        $flag=1;
                    } 
                }
             
                if($flag==0){
                    
                    //echo 'lat="' .$venue_se->location->lat. '" ';
                    //echo 'lng="' .$venue_se->location->lng. '" ';
                    if(isset($venue_se->location->address))
                    $address=$venue_se->location->address;
                    
                    if(isset($venue_se->url) && !empty($venue_se->url))
                    {
                        //echo 'venue url="' .$venue_se->url. '" ';
                        //echo 'venue name="' .$venue_se->name. '" ';
                        $venue_url=$venue_se->url;
                        $venue_name=$venue_se->name;
                      
                    }else{
                        
                        //echo 'venue name="' .$venue_se->name. '" ';
                        $venue_name=$venue_se->name;
                        $venue_url="";
                    }
                    
                    if(isset($venue_se->categories['0']))
                    {
                        if(property_exists($venue_se->categories['0'],"name"))
                        {
                            $venue_category=$venue_se->categories['0']->name;
                          
                            //echo 'venue categories="' .$venue_se->categories['0']->name. '" ';
                        }
                    }
				
                    if(property_exists($venue_se->hereNow,"count"))
                    {
	                //echo 'venue herenow count="' .$venue_se->hereNow->count. '" ';                
                        $venue_hereNow_count=$venue_se->hereNow->count;
       
                        
                    }
                    //echo 'venue stats userscount="' .$venue_se->stats->usersCount. '" '; 
                    //echo 'venue stats checkinscount="' .$venue_se->stats->checkinsCount. '" ';
                    
                    
                    $query_se = sprintf("INSERT INTO poi_foursquare_extend " .
                    " (id, lat,lng, venue_url,venue_name,venue_categories,venue_stats_userscount,venue_stats_checkinscount,venue_herenow_count) " .
                    " VALUES (NULL, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                    mysql_real_escape_string($venue_se->location->lat),
                    mysql_real_escape_string($venue_se->location->lng),
                    mysql_real_escape_string($venue_url),
                    mysql_real_escape_string($venue_name),
                    mysql_real_escape_string($venue_category),
                    mysql_real_escape_string($venue_se->stats->usersCount),
                    mysql_real_escape_string($venue_se->stats->checkinsCount),
                    mysql_real_escape_string($venue_hereNow_count),
                    mysql_real_escape_string($address));

                    $test2 = mysql_query($query_se);
                                        
                }  else {
                    
                    $flag=0;    
                }
                
            endforeach;
            
            
                 echo '<hr>';
         
         foreach($venues_nw->response->venues as $venue_nw):
                
                $flag=0;
                for($i=0;$i<$count;$i++){
                    //echo $check_array_lat[$i];
                    
                    if($check_array_lat[$i]==$venue_nw->location->lat && $check_array_lng[$i]==$venue_nw->location->lng){
                        
                        $flag=1;
                    } 
                }
             
                if($flag==0){
                    
                    echo 'lat="' .$venue_nw->location->lat. '" ';
                    echo 'lng="' .$venue_nw->location->lng. '" ';
                   // echo 'lng="' .$venue_nw->location->address. '" ';
                    
                    if(isset($venue_nw->location->address))
                    $address=$venue_nw->location->address;
                    
                    if(isset($venue_nw->url) && !empty($venue_nw->url))
                    {
                        //echo 'venue url="' .$venue_nw->url. '" ';
                        //echo 'venue name="' .$venue_nw->name. '" ';
                        $venue_url=$venue_nw->url;
                        $venue_name=$venue_nw->name;
                                   
                    }else{
                        
                      //  echo 'venue name="' .$venue_nw->name. '" ';
                        $venue_name=$venue_nw->name;
                        $venue_url="";
                    }
                    
                    if(isset($venue_nw->categories['0']))
                    {
                        if(property_exists($venue_nw->categories['0'],"name"))
                        {
                            //echo 'venue categories="' .$venue_nw->categories['0']->name. '" ';
                            $venue_category=$venue_nw->categories['0']->name;
                        }
                    }
				
                    if(property_exists($venue_nw->hereNow,"count"))
                    {
	                //echo 'venue herenow count="' .$venue_nw->hereNow->count. '" ';   
                        $venue_hereNow_count=$venue_nw->hereNow->count;
                    }
                    //echo 'venue stats userscount="' .$venue_nw->stats->usersCount. '" '; 
                    //echo 'venue stats checkinscount="' .$venue_nw->stats->checkinsCount. '" ';
                    $query_nw = sprintf("INSERT INTO poi_foursquare_extend " .
                    " (id, lat,lng, venue_url,venue_name,venue_categories,venue_stats_userscount,venue_stats_checkinscount,venue_herenow_count) " .
                    " VALUES (NULL, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                    mysql_real_escape_string($venue_nw->location->lat),
                    mysql_real_escape_string($venue_nw->location->lng),
                    mysql_real_escape_string($venue_url),
                    mysql_real_escape_string($venue_name),
                    mysql_real_escape_string($venue_category),
                    mysql_real_escape_string($venue_nw->stats->usersCount),
                    mysql_real_escape_string($venue_nw->stats->checkinsCount),
                    mysql_real_escape_string($venue_hereNow_count),
                    mysql_real_escape_string($address));

                    $test2 = mysql_query($query_nw);
                                        
                }  else {
                    
                    $flag=0;    
                }
                
            endforeach;
            
         //echo '</markers>';
         
         ?>
	
