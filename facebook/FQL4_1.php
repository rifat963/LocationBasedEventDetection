<?php

// Red Feather Lakes, Colorado
//$lat = '40.8078';
//$long = '-105.579';
// Karlsruhe, Germany
//$lat = '49.037868';
//$long = '8.350124';

//Pavia Italy
$lat='45.1894664';
$long='9.163067';
ini_set('max_execution_time', 300);
require 'facebook.php';

$facebook = new Facebook(array(
	'appId' => '727778983940058',
	'secret' => '6c4ffdc9aefa0b5ad73590009d2996d7'
));

$fb_token = 'CAAKV6TZCQD9oBAL6ZCJwiknwp5wyTsXn15RUwZC1aRrU9UJgF7OCyAaaBhzZCAjMQTdukBdaWwloCwtp3ERimJUE2ZAwGM2vHGfHpvf1wl33hGiVjr5E4fe30JIKTNZAQJLFCFSftZBhJax2d4KQ6M8mnKs08Ls8F63VsxWp3uit3Ug4HAZAFE5AcgatrTGmp7QZD';
//$fb_token=$facebook->getAccessToken()

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
$count=0;

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

// Insert new row with user data
$query_del=  sprintf("DELETE FROM `poi_facebook` WHERE 1");
$test_2 = mysql_query($query_del);

foreach($place_search->data as $result) {
   
  $count++;
   
  $fql = 'SELECT checkin_count FROM place WHERE page_id=' .$result->id. '';
  
  $result2 = json_decode(file_get_contents('https://graph.facebook.com/fql?q=' . rawurlencode($fql) . '&access_token=' . $fb_token));
  
  //print_r($result2);
  
  foreach ($result2->data as $vv) {
  
      $ch_count= $vv->checkin_count;
     // echo $ch_count;
  }
  
  
  
  $query = sprintf("INSERT INTO poi_facebook " .
         " (id, name, fb_id, lat, lng,category,address,checkin_count) " .
         " VALUES (NULL, '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
         mysql_real_escape_string($result->name),
         mysql_real_escape_string($result->id),
         mysql_real_escape_string($result->location->latitude),
         mysql_real_escape_string($result->location->longitude),
         mysql_real_escape_string($result->category),
         mysql_real_escape_string($result->location->street),
         mysql_real_escape_string($ch_count));

        $test = mysql_query($query);
  
  
  
  
 // if ($result->location->city) {
 //   $city = $result->location->city;
   // echo $city."</br>"; 
  //  $state = $result->location->state;
   // echo $state."</br>"; 
 
   // $country = $result->location->country;
   // echo $country."</br>"; 
    
 // }
}

echo $count;
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
