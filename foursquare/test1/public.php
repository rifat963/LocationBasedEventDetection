<?php 
	require_once("FoursquareAPI.class.php");
	// Set your client key and secret
	$client_key = "M421SQPJS0YV5PZA2ZFSKZUTVSWKJAAOTNESIPXXMYCUSEBV";
	$client_secret = "ZG4FKJZYXLVQ2EDLQ3SSF3GPW5OGWTT0X33RI2MHGGQCPYQA";
	// Load the Foursquare API library

	if($client_key=="" or $client_secret=="")
	{
        echo 'Load client key and client secret from <a href="https://developer.foursquare.com/">foursquare</a>';
        exit;
	}

	$foursquare = new FoursquareAPI($client_key,$client_secret);
	$location = array_key_exists("location",$_GET) ? $_GET['location'] : "Montreal, QC";
        
?>
<!doctype html>
<html>
<head>
	<title>PHP-Foursquare :: Unauthenticated Request Example</title>
	<style>
	div.venue
	{   
		float: left;
		padding: 10px;
		background: #efefef;
		height: 90px;
		margin: 10px;
		width: 340px;
    }
    div.venue a
    {
    	color:#000;
    	text-decoration: none;

    }
    div.venue .icon
    {
    	background: #000;
		width: 88px;
		height: 88px;
		float: left;
		margin: 0px 10px 0px 0px;
    }
	</style>
</head>
<body>
<h1>Basic Request Example</h1>
<p>
	Search for venues near...
	<form action="" method="GET">
		<input type="text" name="location" />
		<input type="submit" value="Search!" />
	</form>
<p>Searching for venues near <?php echo $location; ?></p>
<hr />
<?php 
	
	
	// Generate a latitude/longitude pair using Google Maps API
	list($lat,$lng) = $foursquare->GeoLocate($location);

	
	// Prepare parameters
	$params = array("ll"=>"$lat,$lng");
	
	// Perform a request to a public resource
	//$response = $foursquare->poiGET("venues/search",$params);
        
        
                $endpoint="venues/search";
                $url = $foursquare->BaseUrl . trim($endpoint,"/");
               
                // Append the client details
		$params['client_id'] = $foursquare->ClientID;
		
              
                $params['client_secret'] = $foursquare->ClientSecret;
		$params['v'] = $foursquare->Version;
		$params['locale'] = $foursquare->ClientLanguage;
                //$test= $foursquare->Request($url,$params,HTTP_GET);
                //echo $test;
		//return $foursquare->Request($url,$params,HTTP_GET);
        
              // if($type == HTTP_GET){ $url = $foursquare->MakeUrl($url,$params);}
                $url = $foursquare->MakeUrl($url,$params);
              //  echo $url;
		// borrowed from Andy Langton: http://andylangton.co.uk/
		$ch = curl_init();
		curl_setopt($ch, CURLOPT_URL,$url);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER,1);
		if ( isset($_SERVER['HTTP_USER_AGENT']) ) {
			curl_setopt($ch, CURLOPT_USERAGENT, $_SERVER['HTTP_USER_AGENT'] );
		} else {
			// Handle the useragent like we are Google Chrome
			curl_setopt($ch, CURLOPT_USERAGENT, 'Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.X.Y.Z Safari/525.13.');
		}
		curl_setopt($ch, CURLOPT_TIMEOUT, 30);
		curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true);
		$acceptLanguage[] = "Accept-Language:" . $foursquare->ClientLanguage;
		curl_setopt($ch, CURLOPT_HTTPHEADER, $acceptLanguage); 
		// Populate the data for POST
		//if($type == HTTP_POST) {
			//curl_setopt($ch, CURLOPT_POST, 1); 
			//if($params) curl_setopt($ch, CURLOPT_POSTFIELDS, $params);
		//}
               
		$result=curl_exec($ch);
                echo $result;
		$info=curl_getinfo($ch);
		
                curl_close($ch);
        
        
            $username="root";
            $password="";
            $database="point_of_interest";
            
            $connection = mysql_connect ("localhost", $username, $password);
            
            $db_selected = mysql_select_db($database, $connection);
        
           
            $query = "SELECT * FROM poi_table WHERE 1";
            
            $result2 = mysql_query($query);
            
            while ($row = @mysql_fetch_assoc($result2)){
            
                $data =  json_encode($row['poi_data']);
              // echo   $row['poi_data'];
                
            }
            
            $data2= str_replace('\n', ' ', $data);
            $data3= str_replace('\\', ' ', $data2);
            
            $data4=  substr($data3, 1,  strlen($data3)-2);
            
           // echo $data4;
            //$data4 =  json_encode($data3);
            
            echo $data4;
	    echo "<p> |</p>";
            
            $venues = json_decode($data4,true);
            
            print_r($venues);
            
             echo "<p> |</p>";
            print_r($venues['results']);
           // echo $venues->response->venues;
            var_dump($venues);
            
            
             echo "<p> |</p>";
            
            foreach ($venues as $key => $value) { 
               echo "<p>$key | $value</p>";
               foreach ($value as $k => $v) { 
                //echo "$k | $v <br />"; 
                }
              
            }

            $phpArray = json_decode($data4, true);
            foreach ($phpArray as $key => $value) { 
               // echo "<h2>$key</h2>";
            foreach ($value as $k => $v) { 
                //echo "$k | $v <br />"; 
                }
            }

            
            foreach($venues->results->address_components as $venue): 
               echo $venue->formatted_address."test";
               
            endforeach;
      
?>
	
		<?php foreach($venues->response->venues as $venue): ?>
			<div class="venue">
				<?php 
					

					if(isset($venue->categories['0']))
					{
						echo '<image class="icon" src="'.$venue->categories['0']->icon->prefix.'88.png"/>';
					}
					else
						echo '<image class="icon" src="https://foursquare.com/img/categories/building/default_88.png"/>';
					echo '<a href="https://foursquare.com/v/'.$venue->id.'" target="_blank"/><b>';
					echo $venue->name;
					echo "</b></a><br/>";
					
					
						
                    if(isset($venue->categories['0']))
                    {
						if(property_exists($venue->categories['0'],"name"))
						{
							echo ' <i> '.$venue->categories['0']->name.'</i><br/>';
						}
					}
					
					if(property_exists($venue->hereNow,"count"))
					{
							echo ''.$venue->hereNow->count ." people currently here <br/> ";
					}

                    echo '<b><i>History</i></b> :'.$venue->stats->usersCount." visitors , ".$venue->stats->checkinsCount." visits ";
					
				?>
			
			</div>
			
		<?php endforeach; ?>
	
</body>
</html>
