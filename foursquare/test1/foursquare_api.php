<?php

require_once("FoursquareAPI.class.php");
	// Set your client key and secret
	$client_key = "M421SQPJS0YV5PZA2ZFSKZUTVSWKJAAOTNESIPXXMYCUSEBV";
	$client_secret = "ZG4FKJZYXLVQ2EDLQ3SSF3GPW5OGWTT0X33RI2MHGGQCPYQA";

$foursquare = new FoursquareAPI($client_key, $client_secret);

// Searching for venues nearby Montreal, Quebec
$endpoint = "venues/search";

// Prepare parameters
$params = array("near"=>"Montreal, Quebec");

// Perform a request to a public resource
$response = $foursquare->GetPublic($endpoint,$params);
echo $response;
// Returns a list of Venues
// $POST defaults to false
//$venues = $api->GetPublic($endpoint [,$params], $POST=false);

// Note: You don't need to add client_id, client_secret, or version to $params

// Setting the access token to enable private requests
// See examples/tokenrequest.php for how to retrieve this
//$auth_token = "<your auth token>";
//$foursquare->SetAccessToken($auth_token);

// Request a private endpoint (Requires Acting User)
//$endpoint_private = "users/self";

// Returns a single user object
//$me = $foursquare->GetPrivate($endpoint_private);
// Note: You don't need to add oauth_token to $params

?>