<?php

require_once("FoursquareAPI.class.php");
$foursquare = new FoursquareAPI("MZFQBTLYJS31EUXBFEP5NWRFZX4MZUFSNTYUDORU5NADQ0FP", "2OA10MR3KRBYOTR4PYQBI2IRNXNMOTT0ZKZ3XBXTU5UOYHVD");

// Searching for venues nearby Montreal, Quebec
$endpoint = "venues/search";

// Prepare parameters
$params = array("near"=>"Montreal, Quebec");

// Perform a request to a public resource
$response = $foursquare->GetPublic($endpoint,$params);

// Returns a list of Venues
// $POST defaults to false
//$venues = $api->GetPublic($endpoint [,$params], $POST=false);

// Note: You don't need to add client_id, client_secret, or version to $params

// Setting the access token to enable private requests
// See examples/tokenrequest.php for how to retrieve this
$auth_token = "<your auth token>";
$foursquare->SetAccessToken($auth_token);

// Request a private endpoint (Requires Acting User)
$endpoint_private = "users/self";

// Returns a single user object
$me = $foursquare->GetPrivate($endpoint_private);

echo $me;
// Note: You don't need to add oauth_token to $params
?>

