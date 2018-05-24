<?php

$username='root';
$password='';
$database='point_of_interest';

function parseToXML($htmlStr) 
{ 
$xmlStr=str_replace('<','&lt;',$htmlStr); 
$xmlStr=str_replace('>','&gt;',$xmlStr); 
$xmlStr=str_replace('"','&quot;',$xmlStr); 
$xmlStr=str_replace("'",'&#39;',$xmlStr); 
$xmlStr=str_replace("&",'&amp;',$xmlStr); 
return $xmlStr; 
} 

include 'spherical-geometry.class.php';
require_once("FoursquareAPI.class.php");
ini_set('max_execution_time', 300);

// Opens a connection to a MySQL server
$connection=mysql_connect ('localhost', $username, $password);
if (!$connection) {
  die('Not connected : ' . mysql_error());
}

// Set the active MySQL database
$db_selected = mysql_select_db($database, $connection);
if (!$db_selected) {
  die ('Can\'t use db : ' . mysql_error());
}

        // Select all the rows in the markers table
        $query_fb = "SELECT * FROM poi_facebook WHERE 1";
        $query_foursquare = "SELECT * FROM poi_foursquare_extend WHERE 1";
        $query_fb2 = "SELECT * FROM poi_facebook WHERE 1";
        $result_fb = mysql_query($query_fb);
        $result_fb2 = mysql_query($query_fb2);
        $result_foursquare = mysql_query($query_foursquare);

        if (!$result_fb) {
            die('Invalid query: ' . mysql_error());
        }

	$client_key = "M421SQPJS0YV5PZA2ZFSKZUTVSWKJAAOTNESIPXXMYCUSEBV"; // Your Client ID
	$client_secret = "ZG4FKJZYXLVQ2EDLQ3SSF3GPW5OGWTT0X33RI2MHGGQCPYQA"; // Your Client Secret
	// Load the Foursquare API library
	$foursquare = new FoursquareAPI($client_key,$client_secret);
        $count=0;
        $array_test=array();
        $array_m=array();
        $c=0;
        while ($row_fb2 = mysql_fetch_array($result_fb2)) {
            $category=$row_fb2['category'];
            array_push($array_test, $category);
            $ss=array_unique(array_diff_assoc($array_test,array_unique($array_test)));
        }
        foreach ($ss as $value) {
            echo $value."</br>";
        }
        
        
        
       // print_r($ss);
        while ($row_fb = mysql_fetch_array($result_fb)) {
  
        //echo '<marker ';
        //echo 'lat="' .$row_fb['lat']. '" ';
        //echo 'lng="' .$row_fb['lng']. '" ';
 
        //echo 'name="' .parseToXML($row_fb['name']). '" ';
        //echo 'id="' .parseToXML($row_fb['fb_id']). '" ';       
        //echo 'street="' .parseToXML($row_fb['address']). '" ';
        //echo 'category="' .parseToXML($row_fb['category']). '" ';
        //echo 'checkin_count="' .$row_fb['checkin_count']. '" ';
        //echo 'venue_stats_userscount="' .$row_foursquare['venue_stats_userscount']. '" '; 
        //echo 'venue_stats_checkinscount="' .$row_foursquare['venue_stats_checkinscount']. '" ';
  
        //echo '/>';
            
            $lat=$row_fb['lat'];
            $lng=$row_fb['lng'];
            
            echo 'lat="' .$row_fb['lat']. '" '.'lng="' .$row_fb['lng']. '" ';
            echo 'name="' .$row_fb['name']. '" ';
            $nn=$row_fb['name'];
            $params_center = array("ll"=>"$lat,$lng","limit"=>"10");
     
            $response_center = $foursquare->GetPublic("venues/search",$params_center);
            $venues_center = json_decode($response_center);
            //echo $response_center;
            //print_r($venues_center);
            $s1=new LatLng($lat, $lng);
            $min = false;
            
            foreach($venues_center->response->venues as $venue):
					
                    $latsq=$venue->location->lat;
                    $lngsq=$venue->location->lng;
                    $s2=new LatLng($latsq, $lngsq);
                    $p= SphericalGeometry::computeDistanceBetween($s1, $s2);
                    echo $p;
                    if ($p < $min || $min === false) 
                    {
                         
                        $min = $p;
                        
                       // if(isset($venue->url) && !empty($venue->url))
                       // {
                           // $venue_url=$venue->url;
                           // $venue_name=$venue->name;
                           
                        //}else{
                           // $venue_name=$venue->name;
                           // $venue_url="";
                        //}
                    }
                    
                    
                   // $input = 'carrrot';
                     $input= $venue->name;  
// matrice di parole con cui verificare
                   // $words  = array('apple','pineapple','banana','orange',
                //'radish','carrot','pea','bean','potato');
                        $words=$nn;
// non ancora trovata la distanza più breve
                    $shortest = -1;
 
// loop su tutte le parole per trovare la più simile
                //foreach ($words as $word) {
 
    // calcola la distanza tra la parola di input
    // e la corrente
                    $lev = levenshtein($input, $words);
 
    // è la parola esatta?
                    if ($lev == 0) {
 
        // la più simile è questa
                         $closest = $word;
                         $shortest = 0;
 
        // esce dal loop, l'abbiamo trovata
                          //  break;
                    }
 
    // se la distanza è inferiore rispetto alla precendete più corta
    // o non ne abbiamo ancora trovata una
                    if ($lev <= $shortest || $shortest < 0) {
        // imposta la parola più simile e la distanza più breve
                        $closest  = $words;
                         $shortest = $lev;
                    }
              //  }
 
                echo "Input word: $input\n";
                if ($shortest == 0) {
                 echo "Exact match found: $closest\n";
                } else {
                    echo "Did you mean: $closest?\n";
                }
                    
                    //echo "</br>".$p."</br>". $latsq.",".$lngsq;    
                
                    if(isset($venue->location->address))
                            $address=$venue->location->address;
                    if(isset($venue->categories['0']))
                    {
                        //  echo 'icon="' .$venue->categories['0']->icon->prefix. '" ';
                        //echo '<image class="icon" src="'.$venue->categories['0']->icon->prefix.'88.png"/>'                          
                    }
                    else{
                        //echo '<image class="icon" src="https://foursquare.com/img/categories/building/default_88.png"/>';
                        // echo 'icon="' ."https://foursquare.com/img/categories/building/default_". '" ';
                    }
                        
                    if(isset($venue->url) && !empty($venue->url))
                    {
                        $venue_url=$venue->url;
                        $venue_name=$venue->name;
                        echo $venue_name;
                        }else{
		        $venue_name=$venue->name;
                        $venue_url="";
                        echo $venue_name;
                    }
		
                    if(isset($venue->categories['0']))
                    {
                        if(property_exists($venue->categories['0'],"name"))
                        {
             //           echo 'venue categories="' .$venue->categories['0']->name. '" ';
                            $venue_category=$venue->categories['0']->name;
                 //           echo $venue_category;
                	//echo ' <i> '.$venue->categories['0']->name.'</i><br/>';
                        }
                    }
				
                    if(property_exists($venue->hereNow,"count"))
                    {
		//echo ''.$venue->hereNow->count ." people currently here <br/> ";
           //     echo 'venue herenow count="' .$venue->hereNow->count. '" ';  
                //$venue_hereNow_count=$venue->hereNow->count;
                    }
           // echo 'venue stats userscount="' .$venue->stats->usersCount. '" '; 
         //   echo 'venue stats checkinscount="' .$venue->stats->checkinsCount. '" ';
            
            endforeach; 
            echo "</br>".$min.$venue_name;
            if($count==0)
              break;
            $count++;
        }

// End XML file
//echo '</markers>';

?>