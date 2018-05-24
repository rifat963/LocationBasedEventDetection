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

$School=0;
$Fraternity_House=0;
$University=0;
$Bakery=0;
$Cafe=0;
$Restaurant=0;
$Shop=0;
$Store=0;
$College=0;
$Library=0;




$count_no_of_category_matched_fb=0;
$count_no_of_category_matched_fq=0;
$count_no_of_category_matched_fb1=0;
$count_no_of_category_matched_fq1=0;
$no_of_matched_education=0;
$no_of_matched_local_business=0;

$no_of_nonmatched_education=0;
$no_of_nonmatched_local_business=0;
// Set the active MySQL database
$db_selected = mysql_select_db($database, $connection);
if (!$db_selected) {
  die ('Can\'t use db : ' . mysql_error());
}
        //fb database query
        $query_fb = "SELECT * FROM poi_facebook WHERE 1";
        //foursquare database query
        $query_foursquare = "SELECT * FROM poi_foursquare_extend WHERE 1";
        //fb database query
        $query_fb2 = "SELECT * FROM poi_facebook WHERE 1";
        //fb  database query
        $query_fb3 = "SELECT * FROM poi_facebook WHERE 1";
        
        //category table query
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
        
        /*
            Main loop to get data from database and map with the category listing
         *          */
        while ($row_fb = mysql_fetch_array($result_fb)) {
        
            $flag_education=0;
            $flag_local_business=0;
            $lat=$row_fb['lat'];
            $lng=$row_fb['lng'];
            $cct=$row_fb['category'];
          
            $nn=$row_fb['name'];
            
            $fb_place_name=$nn;
            $flag=0;
            $m_count=121;
            $n_count=253;
            /*Category Selecting*/
            
            if(in_array($cct, $cat_local_business)){
                $flag_local_business=1;
            }
            
            if(in_array($cct, $cat_education)){
                $flag_education=1;
            }
            
                                  
            if($flag_education){
                
            //Matching category
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
            echo "</br>Facebook category Matched:".$cct;
            echo "</br>Facebook Name:".$nn;
           
            //category matched
            $count_no_of_category_matched_fb++;
                
                
            $params_center = array("ll"=>"$lat,$lng","limit"=>"10");
     
            $response_center = $foursquare->GetPublic("venues/search",$params_center);
            $venues_center = json_decode($response_center);
            
            $s1=new LatLng($lat, $lng);
            
            $min = false;
            
            //Foursquare categories
            
            foreach($venues_center->response->venues as $venue):
                
                    if(isset($venue->categories['0']))
                    {
                        if(property_exists($venue->categories['0'],"name"))
                        {
                            $venue_category=$venue->categories['0']->name;
                            //echo "</br>".$venue_category;
                        }
                    }
                    
                    $flag1=0;
                    for ($index1 = 0; $index1 < count($cat_education); $index1++) {
                       // echo "</br>".$cat_local_business[$index1];
                        if(strpos($venue_category,$cat_education[$index1])!==FALSE){
                            
                            $flag1=1;
                            
                            if($venue_category=="School"){
                                
                                $School++;
                            }
                            if($venue_category=="University"){
                                
                                $University++;
                            }
                            if($venue_category=="College"){
                                $College++;
                                
                            }
                            if($venue_category=="Fraternity House"){
                                $Fraternity_House++;
                                
                            }
                            if($venue_category=="Library"){
                                $Library++;
                                
                            }
                            
                             //echo $venue_category;   
                        }
                    }
                    if($flag1)
                    {   
                    
                    echo "</br>Foursquare Category matched=>".$venue_category;
                    echo "</br>Foursquare Name=>".$venue->name;
                    
                    //category matched foursquare
                        
                    $count_no_of_category_matched_fq++;
                        
                    $latsq=$venue->location->lat;
                    $lngsq=$venue->location->lng;
                    $s2=new LatLng($latsq, $lngsq);
                    
                    //Distance
                    
                    $p= SphericalGeometry::computeDistanceBetween($s1, $s2);
                    echo "</br>".$p;
                    if ($p < $min || $min === false) 
                    {
                        $min = $p;
                    }
                    //echo "</br>".$min;
                   
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
                        
                        $no_of_matched_education++;
                        echo "</br>===============>";
                    
                        echo "</br>Levinstine Exact match found: $closest\n";
                    } else if ($shortest == 7) {
                        
                        $no_of_matched_education++;
                        echo "</br>===============>";
                    
                        echo "</br>Levinstine Exact match found: $closest\n";
                    } else {
                        
                        $no_of_nonmatched_education++;
                        echo "</br>Levinstine Did you mean: $closest?\n";
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
                
            
            $flag_fq=0;
            
            for ($index = 0; $index < count($cat_local_business); $index++) {
        
                if(strpos($cct,"/")){
                
                    $cct_temp=  explode("/", $cct);
                    for ($index2 = 0; $index2 < count($cct_temp); $index2++) {
                           
                        if(strpos($cat_local_business[$index],$cct_temp[$index2])!==FALSE){
                
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
            echo "</br>Facebook Matched:".$cct;  
            echo "</br>Facebook Name:".$nn;
           
            $count_no_of_category_matched_fb1++;
            
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
                            //echo "</br>".$venue_category;
                        }
                    }
                    
                    $flag1=0;
                    for ($index1 = 0; $index1 < count($cat_local_business); $index1++) {
                       // echo "</br>".$cat_local_business[$index1];
                        if(strpos($venue_category,$cat_local_business[$index1])!==FALSE){
                            $flag1=1;
                            
                            if($venue_category=="Restaurant"){
                                
                                $Restaurant++;
                            }
                            if($venue_category=="Café"){
                                
                                $Cafe++;
                            }
                            //if($venue_category=="Restaurant_Café_Bakery_Shop_Store_Pizza+Place_Plaza_Castle_Medical_Government")
                           
                            if($venue_category=="Bakery"){
                                
                                $Bakery++;
                            }
                            if($venue_category=="Shop"){
                                
                                $Shop++;
                            }
                            if($venue_category=="Store"){
                                
                                $Store++;
                            }
                             //echo $venue_category;   
                        }
                    }
                    if($flag1)
                    {   
                    echo "</br>Foursquare matched=>".$venue_category;
                    echo "</br>Foursquare Name=>".$venue2->name;
                        
                    $count_no_of_category_matched_fq1++;
                        
                        
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
                    
                    
                    /*Sub String matching*/
                    
                    /***********************************************/
                    
                    $sub_foursquare =  explode(" ", $venue2->name);
                    $sub_facebook =  explode(" ", $fb_place_name);
                    //print_r($sub_facebook);
                    //print_r($sub_foursquare);
                    $match_count_fq=0;
                    $match_count_fb=0;
                    
                    if(count($sub_foursquare)>=  count($sub_facebook)){
                    
                        for ($index4 = 0; $index4 < count($sub_foursquare); $index4++) {
                            
                            for ($index7= 0; $index7 < count($sub_facebook); $index7++) {
                                
                                if($sub_facebook[$index7]==$sub_foursquare[$index4]){
                
                                   $match_count_fq++;
                                }
                            }
                        }
                        
                        if((count($sub_foursquare)-1)>=$match_count_fq && count($sub_foursquare)<=$match_count_fq){
                           
                           // echo "</br>=>>".$match_count_fq;
                            
                        }
                        else {    
                            
                            //echo "</br>No Match=>>".$match_count_fq;
                        }
                        
                    }else{
                        
                        for ($index5 = 0; $index5 < count($sub_facebook); $index5++) {
                           
                            for ($index6 = 0; $index6 < count($sub_foursquare); $index6++) {
                        
                                if($sub_facebook[$index5]==$sub_foursquare[$index6]){
                
                                   $match_count_fb++;
                                }
                                
                            }
                        }
                        if((count($sub_facebook)-1)>=$match_count_fb && count($sub_facebook)<=$match_count_fb){
                           
                            //echo "</br>=>>".$match_count_fb;
                            
                        }
                        else {    
                            
                            //echo "</br>No Match=>>".$match_count_fb;
                        }
                        
                    }
                    
                    
                    
                    
                    
                    /*Levinstine distance*/
                    
                    /**********************************************/
                    
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
                    
                    //echo "</br>Input word: $input\n";
                    if ($shortest == 0) {
                        $no_of_matched_local_business++;
                        echo "</br>=======>";
                        echo "</br>Levinstine Exact match found: $closest\n";
                        
                    }elseif ($shortest <=7) {
                        $no_of_matched_local_business++;
                       // echo 'sss';
                        echo "</br>==##>";
                        echo "</br>Levinstine Near match found: $closest\n";
                        
                    }else {
                        echo "</br>Levinstine No Match Did you mean: $closest?\n";
                        $no_of_nonmatched_local_business++;
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
             
                }    
            }
            
            
           // echo "</br>".$min.$venue_name;
            if($count>50)
              break;
            $count++;
            
        }
        
       // echo "Fb category matched".$count_no_of_category_matched_fb;
       // echo "FQ category matched".$count_no_of_category_matched_fq;
        //echo "Fb category matched local business".$count_no_of_category_matched_fb1;
        //echo "FQ category matched local business".$count_no_of_category_matched_fq1;

        echo '</br>';
        
       // echo $School." ".$Fraternity_House." ".$University." ".$Bakery." ".$Cafe." ".$Restaurant." ".$Shop." ".$Store." ".$College." ".$Library;
        echo '</br>';
        //echo $no_of_matched_education."  ".$no_of_matched_local_business;
        //echo '</br>';
        //echo $no_of_nonmatched_education."  ".$no_of_nonmatched_local_business;
        
        echo 'catrgories matched total'.$m_count;
        echo '</br>';
        echo 'name matched total'.$N_count;
        
        
// End XML file
//echo '</markers>';

?>