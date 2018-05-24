<?php

// Red Feather Lakes, Colorado
//$lat = '40.8078';
//$long = '-105.579';
// Karlsruhe, Germany
//$lat = '49.037868';
//$long = '8.350124';
ini_set('max_execution_time', 300);
//Pavia Italy
$lat='45.1894664';
$long='9.163067';

require 'facebook.php';

$facebook = new Facebook(array(
	'appId' => '640569522699707',
	'secret' => 'c882b6c6e0793cb82e7c7312901a3a85'
));

$fb_token = 'CAACEdEose0cBADTDjGuBfZBVfB3OH0Vy5KqQUroDXPNDKlnqN26fzTWkwPSxQmgHk4QzaS1ZAwfS4ynVCvNNyu1YGyaueXNJhU2XUfdEIRu4nXnjkvEIHQrUbCbKRqKLW0ZAljUnZAOrr2zGt8dIKaCR7elFmcVke863gGYE3uKxLngmmAsQO6skaUCmzyzPiNgX7qjx7wZDZD';
//$fb_token=$facebook->getAccessToken();

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
echo $place_search2;
//var_dump($place_search);
$place_search=json_decode($place_search2);
//print_r($place_search);
foreach($place_search->data as $result) {
   
    //echo $result->name.",".$result->id;
    //echo $result->location->latitude.",".$result->location->latitude;
   // $fql = 'SELECT checkin_count FROM place WHERE page_id=' .$result->id. '';
  
    //$result2 = json_decode(file_get_contents('https://graph.facebook.com/fql?q=' . rawurlencode($fql) . '&access_token=' . $fb_token));
  
    //print_r($result2);
  
    
  
  //if ($result->location->city) {
   // $city = $result->location->city;
   // echo $city."</br>"; 
   // $state = $result->location->state;
   // echo $state."</br>"; 
 
   // $country = $result->location->country;
   // echo $country."</br>"; 
    
 // }
}

//echo $result->id;  
  
 // $fql = 'SELECT checkin_count FROM place WHERE page_id=' .$result->id. '';
  
 // $result2 = json_decode(file_get_contents('https://graph.facebook.com/fql?q=' . rawurlencode($fql) . '&access_token=' . $fb_token));
  
  //print_r($result2);
  
 // foreach ($result2->data as $vv) {
 // echo $vv->checkin_count;
      
  //}

//$city_name="108581069173026";

//$fql = 'SELECT name FROM place WHERE page_id=' .$city_name. '';
//$result = json_decode(file_get_contents('https://graph.facebook.com/fql?q=' . rawurlencode($fql) . '&access_token=' . $fb_token));
//$result = $facebook->api(array('method' => 'fql.query','query' => $query));
//$rr=$facebook->api('/fql?q='.urlencode($fql));
//$data = $facebook->api(array('method' => 'fql.query', 'query' => 'SELECT pid, object_id, src, src_width, src_height, src_big, src_big_width, src_big_height, modified FROM photo WHERE aid = "100002647632588_19303" ORDER BY modified DESC LIMIT 0, 9' ));
//echo $data;
//print_r($result);

/*foreach($place_search->data as $result) {
  if ($result->location->city) {
    $city = $result->location->city;
    echo $city."</br>"; 
    $state = $result->location->state;
    $country = $result->location->country;
    if ($country=='United States') {
        
      $city_name = $city . ', ' . $states_arr[$state]; // e.g. 'Chicago, Illinois'
    }
    else {
      $city_name = $city . ', ' . $country; // e.g. 'Rome, Italy'
    }
    $fql = 'SELECT * FROM page WHERE type="CITY" and name="' .$city_name. '"';
    $result = json_decode(file_get_contents('https://graph.facebook.com/fql?q=' . rawurlencode($fql) . '&access_token=' . $fb_token));
    if (count($result->data)>0) {
      // We found it!
      echo '<pre>';
        print_r($result);
        echo '</pre>';
      break;
    }
    else {
      // No luck, try the next place
      print ("Couldn't find " . $city_name . "\n");
    }
  }
}*/
