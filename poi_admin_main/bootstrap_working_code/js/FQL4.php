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

ini_set('max_execution_time', 300);
require 'facebook.php';
//Pavia Italy
$lat='45.1894664';
$long='9.163067';

 

//$facebook = new Facebook(array(
//	'appId' => '640569522699707',
//	'secret' => 'c882b6c6e0793cb82e7c7312901a3a85'
//));

//$fb_token = 'CAACEdEose0cBADTDjGuBfZBVfB3OH0Vy5KqQUroDXPNDKlnqN26fzTWkwPSxQmgHk4QzaS1ZAwfS4ynVCvNNyu1YGyaueXNJhU2XUfdEIRu4nXnjkvEIHQrUbCbKRqKLW0ZAljUnZAOrr2zGt8dIKaCR7elFmcVke863gGYE3uKxLngmmAsQO6skaUCmzyzPiNgX7qjx7wZDZD';
//$fb_token=$facebook->getAccessToken();


$facebook = new Facebook(array(
	'appId' => '239559759570873',
	'secret' => 'de90ffc918b69894c07e601a685fef78'
));

$fb_token = 'CAACEdEose0cBAJ3FRwfiZBZApsjspiQ1A10sFTlQiZB8orF4FO1bSVXlRuKaYpyjLiB4oSKptWHOHtUZCuZBX4Fh2bF3VHtGKgdWDp7ZCJFPs3AE6gUIVAEkuHzu7NFN21tC08aJZCxrFVvDhIWXnibBTH1zt2MnTHy4EZAgxLJ4Ff1uDH7j0RflErFYgDwTXpapghLYmglbgQZDZD';


if (!$facebook->getUser())
{
    $login_url = $facebook->getLoginUrl(array(
            'scope' => 'publish_stream' // Permissions goes here
        ) 
    );
}
//echo $login_url;
//$states_arr  = array('AL'=>"Alabama",'AK'=>"Alaska",'AZ'=>"Arizona",'AR'=>"Arkansas",'CA'=>"California",'CO'=>"Colorado",'CT'=>"Connecticut",'DE'=>"Delaware",'FL'=>"Florida",'GA'=>"Georgia",'HI'=>"Hawaii",'ID'=>"Idaho",'IL'=>"Illinois", 'IN'=>"Indiana", 'IA'=>"Iowa",  'KS'=>"Kansas",'KY'=>"Kentucky",'LA'=>"Louisiana",'ME'=>"Maine",'MD'=>"Maryland", 'MA'=>"Massachusetts",'MI'=>"Michigan",'MN'=>"Minnesota",'MS'=>"Mississippi",'MO'=>"Missouri",'MT'=>"Montana",'NE'=>"Nebraska",'NV'=>"Nevada",'NH'=>"New Hampshire",'NJ'=>"New Jersey",'NM'=>"New Mexico",'NY'=>"New York",'NC'=>"North Carolina",'ND'=>"North Dakota",'OH'=>"Ohio",'OK'=>"Oklahoma", 'OR'=>"Oregon",'PA'=>"Pennsylvania",'RI'=>"Rhode Island",'SC'=>"South Carolina",'SD'=>"South Dakota",'TN'=>"Tennessee",'TX'=>"Texas",'UT'=>"Utah",'VT'=>"Vermont",'VA'=>"Virginia",'WA'=>"Washington",'DC'=>"Washington D.C.",'WV'=>"West Virginia",'WI'=>"Wisconsin",'WY'=>"Wyoming");

$place_search2 = file_get_contents('https://graph.facebook.com/search?type=place&center=' . $lat . ',' . $long . '&distance=10000&access_token=' . $fb_token);
//echo $place_search2;
//var_dump($place_search);
$place_search=json_decode($place_search2);
//print_r($place_search);

header("Content-type: text/xml");

// Start XML file, echo parent node
echo '<markers>';

foreach($place_search->data as $result) {
   
    echo '<marker ';
    //  echo $result->name.",".$result->id;
    //  echo $result->location->latitude.",".$result->location->latitude;
    
    echo 'lat="' .$result->location->latitude. '" ';
    echo 'lng="' .$result->location->longitude. '" ';
    echo 'id="' .$result->id. '" ';
    echo 'name="' .parseToXML($result->name). '" ';
    echo 'street="' .parseToXML($result->location->street). '" ';
    echo 'category="' .parseToXML($result->category). '" ';
    
    //echo 'country="' .parseToXML($result->category). '" ';
    
    //echo 'city="' .parseToXML($result->location->city). '" ';
    
    //$fql = 'SELECT checkin_count FROM place WHERE page_id=' .$result->id. '';
  
   // $result2 = json_decode(file_get_contents('https://graph.facebook.com/fql?q=' . rawurlencode($fql) . '&access_token=' . $fb_token));
  
    //print_r($result2);
  
    echo '/>'; 
}

echo '</markers>';

