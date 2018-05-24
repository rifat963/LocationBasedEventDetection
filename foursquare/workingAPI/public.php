<?php 
	require_once("FoursquareAPI.class.php");
	$location = array_key_exists("location",$_GET) ? $_GET['location'] : "Pune";
?>
<!doctype html>
<html>
<head>
		<title>Foursquare API integration with PHP Site: devzone.co.in</title>
            <script>
                (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
                    (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
                     m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
                 })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

             ga('create', 'UA-43091346-1', 'devzone.co.in');
            ga('send', 'pageview');

</script>
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
	// Set your client key and secret
	$client_key = "M421SQPJS0YV5PZA2ZFSKZUTVSWKJAAOTNESIPXXMYCUSEBV"; // Your Client ID
	$client_secret = "ZG4FKJZYXLVQ2EDLQ3SSF3GPW5OGWTT0X33RI2MHGGQCPYQA"; // Your Client Secret
	// Load the Foursquare API library
	$foursquare = new FoursquareAPI($client_key,$client_secret);
	
	// Generate a latitude/longitude pair using Google Maps API
	list($lat,$lng) = $foursquare->GeoLocate($location);
	
	
	// Prepare parameters
	$params = array("ll"=>"$lat,$lng","limit"=>"10","radius"=>"200");
	
	// Perform a request to a public resource
	$response = $foursquare->GetPublic("venues/search",$params);
        //echo $response;
	$venues = json_decode($response);
        
     //   print_r($venues);
        
?>
	
		<?php 
				
		foreach($venues->response->venues as $venue): ?>
			<div class="venue">
				<?php 
					

					if(isset($venue->categories['0']))
					{
						echo '<image class="icon" src="'.$venue->categories['0']->icon->prefix.'88.png"/>';
                                      
					}
					else
						echo '<image class="icon" src="https://foursquare.com/img/categories/building/default_88.png"/>';
					
					if(isset($venue->url) && !empty($venue->url))
					{
					echo '<a href="'.$venue->url.'" target="_blank"/><b>';
					echo $venue->name;
					echo "</b></a><br/>";
                                     
					}else{
					echo $venue->name."<br>";	
					}
		
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
                    //echo $venue->likes->count;
				?>
			
			</div>
			
		<?php endforeach; ?>
	
</body>
</html>
