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
	$response = $foursquare->GetPublic("venues/search",$params);
       // echo $foursquare->value;
        
            $username="root";
            $password="";
            $database="point_of_interest";
            
            $connection = mysql_connect ("localhost", $username, $password);
            
            $db_selected = mysql_select_db($database, $connection);
        
           
            $query = "SELECT * FROM poi_data_json WHERE 1";
            
            $result2 = mysql_query($query);
           // echo $result2;
            $query1 = "DELETE FROM poi_data_json";
            $result = mysql_query($query1);
            
	    $venues = json_decode($result2);
            
          //  echo $venues->response->venues;
        
        
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
