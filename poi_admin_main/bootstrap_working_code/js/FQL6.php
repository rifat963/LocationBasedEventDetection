<?php

// Red Feather Lakes, Colorado
//$lat = '40.8078';
//$long = '-105.579';
// Karlsruhe, Germany
//$lat = '49.037868';
//$long = '8.350124';

function parseToXML($htmlStr) 
{ 
            $xmlStr=str_replace('<','&lt;',$htmlStr); 
            $xmlStr=str_replace('>','&gt;',$xmlStr); 
            $xmlStr=str_replace('"','&quot;',$xmlStr); 
            $xmlStr=str_replace("'",'&#39;',$xmlStr); 
            $xmlStr=str_replace("&",'&amp;',$xmlStr); 
            return $xmlStr; 
}
require 'facebook.php';
ini_set('max_execution_time', 300);


//Pavia Italy

$lat='45.1894664';
$long='9.163067';



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

$place_search2 = file_get_contents('https://graph.facebook.com/search?type=place&center=' . $lat . ',' . $long . '&distance=10000&access_token=' . $fb_token);
echo $place_search2;
//var_dump($place_search);
$place_search=json_decode($place_search2);
/*
header("Content-type: text/xml");

// Start XML file, echo parent node
echo '<markers>';
//echo $result->name.",".$result->id;
  //echo "test";
foreach($place_search->data as $result) {
    
    echo '<marker ';
    //    echo $result->name.",".$result->id;
    //  echo $result->location->latitude.",".$result->location->latitude;
    echo 'lat="' .$result->location->latitude. '" ';
    echo 'lng="' .$result->location->longitude. '" ';
    echo 'id="' .$result->id. '" ';
    echo 'name="' .parseToXML($result->name). '" ';
    echo 'street="' .parseToXML($result->location->street). '" ';
    echo 'category="' .parseToXML($result->category). '" ';
    
   // echo 'category="' .parseToXML($result->category). '" ';
   // $fql = 'SELECT checkin_count FROM place WHERE page_id=' .$result->id. '';
    //$result2 = json_decode(file_get_contents('https://graph.facebook.com/fql?q=' . rawurlencode($fql) . '&access_token=' . $fb_token));
    //echo 'checkin_count="' .parseToXML($result2->data->checkin_count). '" ';
    //print_r($result2);
    
  echo '/>';
}

echo '</markers>';

*/

