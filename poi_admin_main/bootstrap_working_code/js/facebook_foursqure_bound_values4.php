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
        $query_fb3 = "SELECT * FROM poi_facebook WHERE 1";
        
        $query_category="SELECT * FROM category_combine WHERE 1";
        
        $result_fb = mysql_query($query_fb);
        $result_fb2 = mysql_query($query_fb2);
        $result_fb3 = mysql_query($query_fb3);
        $result_foursquare = mysql_query($query_foursquare);
        
        $result_category=mysql_query($query_category);

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
           // echo $value."</br>";
        }
        
        /*
         * Category Mapping
         *          */
        
        $cat_local_business=array();
        $cat_education=array();
        while ($row_cat = mysql_fetch_array($result_category)) {
        
            $cat=$row_cat['name'];
            $sub_cat=$row_cat['sub_name'];
            
            $cat2=  str_replace( "_", " ",$cat);
            $sub_cat2=  str_replace("_", " ", $sub_cat);
           // echo $sub_cat2;
            
            if($cat2=="Local business"){
                
                array_push($cat_local_business, $cat2);
                $sub_local=  explode(" ", $sub_cat2);
                
                for($i=0;$i<count($sub_local);$i++)
                {
                     $sub_local2=  str_replace("+", " ", $sub_local[$i]);
                     //echo "</br>".$sub_local2;
                     array_push($cat_local_business, $sub_local2);
                }
            }
            if($cat2=="Education"){
                
                array_push($cat_education, $cat2);
                $sub_local=  explode(" ", $sub_cat2);
                
                for($i=0;$i<count($sub_local);$i++)
                {
                     $sub_local2=  str_replace("+", " ", $sub_local[$i]);
                     //echo "</br>".$sub_local2;
                     array_push($cat_education, $sub_local2);
                }
            }
            
        }
        
        //Education Lopp
        while ($row_fb = mysql_fetch_array($result_fb)) {
        
            $flag_education=0;
            $flag_local_business=0;
            $lat=$row_fb['lat'];
            $lng=$row_fb['lng'];
            $cct=$row_fb['category'];
            //echo 'lat="' .$row_fb['lat']. '" '.'lng="' .$row_fb['lng']. '" ';
            //echo 'name="' .$row_fb['name']. '" ';
            $nn=$row_fb['name'];
            $fb_place_name=$nn;
            $flag=0;
  
            /*Category swapping*/
            
            if(in_array($cct, $cat_local_business)){
                $flag_local_business=1;
            }
            if(in_array($cct, $cat_education)){
                $flag_education=1;
            }
            
                                  
            if($flag_education){
            for ($index = 0; $index < count($cat_education); $index++) {
                
                if(strpos($cct,"/")){
                
                    $cct_temp=  explode("/", $cct);
                    for ($index2 = 0; $index2 < count($cct_temp); $index2++) {
                           
                        if(strpos($cat_education[$index],$cct_temp[$index2])!==FALSE){
                           // echo $cct_temp[$index2];
                            $flag=1;
                        }
               
                    }
                    
                }else {
                  
                  if(strpos($cat_education[$index],$cct)!==FALSE){
                    $flag=1;
                  }
                }
            }
           
            if($flag)
            {
            echo "<hr/>";  
            echo "</br>Matched:".$cct;
            echo "</br>Name:".$nn;
            
            $params_center = array("ll"=>"$lat,$lng","limit"=>"10");
     
            $response_center = $foursquare->GetPublic("venues/search",$params_center);
            $venues_center = json_decode($response_center);
            //echo $response_center;
            //print_r($venues_center);
            $s1=new LatLng($lat, $lng);
            $min = false;
            
            
            foreach($venues_center->response->venues as $venue):
                
                    if(isset($venue->categories['0']))
                    {
                        if(property_exists($venue->categories['0'],"name"))
                        {
                            $venue_category=$venue->categories['0']->name;
                            echo "</br>".$venue_category;
                        }
                    }
                    
                    $flag1=0;
                    for ($index1 = 0; $index1 < count($cat_education); $index1++) {
                       // echo "</br>".$cat_local_business[$index1];
                        if(strpos($venue_category,$cat_education[$index1])!==FALSE){
                            $flag1=1;
                             //echo $venue_category;   
                        }
                    }
                    if($flag1)
                    {   
                        
                    echo "</br>matched=>".$venue_category;
                    echo "</br>Name=>".$venue->name;
                    $latsq=$venue->location->lat;
                    $lngsq=$venue->location->lng;
                    $s2=new LatLng($latsq, $lngsq);
                    $p= SphericalGeometry::computeDistanceBetween($s1, $s2);
                    echo "</br>".$p;
                    if ($p < $min || $min === false) 
                    {
                        $min = $p;
                    }
                    echo "</br>".$min;
                   
                    /*Levinstine distance*/
                     $input= $venue->name;  
                     $words=$fb_place_name;
                     $shortest = -1;
                     $lev = levenshtein($input, $words);
                     if ($lev == 0) {
                         $closest = $words;
                         $shortest = 0;
                    }
 
                    if ($lev <= $shortest || $shortest < 0) {
                         $closest  = $words;
                         $shortest = $lev;
                    }

                    echo "</br>Input word: $input\n";
                    if ($shortest == 0) {
                        echo "</br>Exact match found: $closest\n";
                    } else {
                        echo "</br>Did you mean: $closest?\n";
                    }
                    
                    
                    if(isset($venue->location->address))
                            $address=$venue->location->address;
                        
                    if(isset($venue->url) && !empty($venue->url))
                    {
                        $venue_url=$venue->url;
                        $venue_name=$venue->name;
                       // echo $venue_name;
                        }else{
		        $venue_name=$venue->name;
                        $venue_url="";
                       // echo $venue_name;
                    }
		
                    
				
                    if(property_exists($venue->hereNow,"count"))
                    {
                        $venue_hereNow_count=$venue->hereNow->count;
                    }
           // echo 'venue stats userscount="' .$venue->stats->usersCount. '" '; 
         //   echo 'venue stats checkinscount="' .$venue->stats->checkinsCount. '" ';
                    }
            endforeach;
                    }
            }
            
            
            
            if($flag_local_business){
                
            //while ($row_fb3 = mysql_fetch_array($result_fb3)) {
            
            //$lat=$row_fb3['lat'];
            //$lng=$row_fb3['lng'];
            //$cct=$row_fb3['category'];
            //echo 'lat="' .$row_fb['lat']. '" '.'lng="' .$row_fb['lng']. '" ';
            //echo 'name="' .$row_fb['name']. '" ';
            //$nn=$row_fb3['name'];
            
            $flag_fq=0;
            
            for ($index = 0; $index < count($cat_local_business); $index++) {
                
                   
                
                if(strpos($cct,"/")){
                
                    $cct_temp=  explode("/", $cct);
                    for ($index2 = 0; $index2 < count($cct_temp); $index2++) {
                           
                        if(strpos($cat_local_business[$index],$cct_temp[$index2])!==FALSE){
                           // echo $cct_temp[$index2];
                            $flag_fq=1;
                        }
                    }
                    
                }else {
         
                    if(strpos($cat_local_business[$index],$cct)!==FALSE){
                    $flag_fq=1;
                  }
                }
            }
           
            if($flag_fq)
            {
            echo "<hr/>";  
            echo "</br>Matched:".$cct;  
            echo "</br>Name:".$nn;
           
            $params_center2 = array("ll"=>"$lat,$lng","limit"=>"10");
     
            $response_center2 = $foursquare->GetPublic("venues/search",$params_center2);
            $venues_center2 = json_decode($response_center2);
            //echo $response_center;
            //print_r($venues_center);
            $s1=new LatLng($lat, $lng);
            $min = false;
            
            
            foreach($venues_center2->response->venues as $venue2):
                
                    if(isset($venue2->categories['0']))
                    {
                        if(property_exists($venue2->categories['0'],"name"))
                        {
                            $venue_category=$venue2->categories['0']->name;
                            echo "</br>".$venue_category;
                        }
                    }
                    
                    $flag1=0;
                    for ($index1 = 0; $index1 < count($cat_local_business); $index1++) {
                       // echo "</br>".$cat_local_business[$index1];
                        if(strpos($venue_category,$cat_local_business[$index1])!==FALSE){
                            $flag1=1;
                             //echo $venue_category;   
                        }
                    }
                    if($flag1)
                    {   
                        
                    echo "</br>matched=>".$venue_category;
                    echo "</br>Name=>".$venue2->name;
                    $latsq=$venue2->location->lat;
                    $lngsq=$venue2->location->lng;
                    $s2=new LatLng($latsq, $lngsq);
                    $p= SphericalGeometry::computeDistanceBetween($s1, $s2);
                    //echo $p;
                    echo "</br>".$p;
                    if ($p < $min || $min === false) 
                    {
                        $min = $p;
                    }
                    /*Levinstine distance*/
                     $input= $venue2->name;  
                     $words=$fb_place_name;
                     $shortest = -1;
                     $lev = levenshtein($input, $words);
                     if ($lev == 0) {
                         $closest = $words;
                         $shortest = 0;
                    }
 
                    if ($lev <= $shortest || $shortest < 0) {
                         $closest  = $words;
                         $shortest = $lev;
                    }

                    echo "</br>Input word: $input\n";
                    if ($shortest == 0) {
                        echo "</br>Exact match found: $closest\n";
                    } else {
                        echo "</br>Did you mean: $closest?\n";
                    }
                    if(isset($venue2->location->address))
                            $address=$venue2->location->address;
                        
                    if(isset($venue2->url) && !empty($venue2->url))
                    {
                        $venue_url=$venue2->url;
                        $venue_name=$venue2->name;
                       // echo $venue_name;
                        }else{
		        $venue_name=$venue2->name;
                        $venue_url="";
                       // echo $venue_name;
                    }
		
                    
				
                    if(property_exists($venue2->hereNow,"count"))
                    {
                        $venue_hereNow_count=$venue2->hereNow->count;
                    }
           // echo 'venue stats userscount="' .$venue->stats->usersCount. '" '; 
         //   echo 'venue stats checkinscount="' .$venue->stats->checkinsCount. '" ';
                    }
            endforeach;
              //      }
            }    
            }
            
            
           // echo "</br>".$min.$venue_name;
            if($count>20)
              break;
            $count++;
            
        }

// End XML file
//echo '</markers>';

?>