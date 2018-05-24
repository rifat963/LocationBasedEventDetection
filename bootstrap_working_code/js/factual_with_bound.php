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
require_once('lib\Factual.php');
ini_set('max_execution_time', 300);
include 'spherical-geometry.class.php'; 
$factual = new Factual("xoTuroWBWXqA8nQrwOqDfj3ukGjgaJfvrvblbchH","W3kEHsD0stAvZNcUQw22k51I4ItmOEP0ZXMiRFCA");

$query = new FactualQuery;



        $event=new LatLng(45.1894664, 9.163067);
   
        /* Code for calculating bounds   */
  
        $radius=500;
        $LatLng=$event;
        $latRadiansDistance = $radius / 6378137;
        $latDegreesDistance = rad2deg($latRadiansDistance);
        $lngDegreesDistance = rad2deg($latRadiansDistance / cos(deg2rad($LatLng->getLat())));
    
        // SW point
        $swLat = $LatLng->getLat() - $latDegreesDistance;
        $swLng = $LatLng->getLng() - $lngDegreesDistance;
        $sw = new LatLng($swLat, $swLng);
       // echo $sw->getLat();
        //echo $sw->getLng();
        // NE point
        $neLat = $LatLng->getLat() + $latDegreesDistance;
        $neLng = $LatLng->getLng() + $lngDegreesDistance;
        $ne = new LatLng($neLat, $neLng);

        //echo $ne->getLat();
        //echo $ne->getLng();
       // echo '</br>';
        // SE point
        $neLat = $LatLng->getLat() - $latDegreesDistance;
        $neLng = $LatLng->getLng() + $lngDegreesDistance;
        $se = new LatLng($neLat, $neLng);

        //echo $se->getLat();
        //echo $se->getLng();
       
        //NW
        $neLat = $LatLng->getLat() + $latDegreesDistance;
        $neLng = $LatLng->getLng() - $lngDegreesDistance;
        $nw = new LatLng($neLat, $neLng);
 
        
$query->within(new FactualCircle(doubleval($event->getLat()),  doubleval($event->getLng()), 500));

//$query->search("pavia italy");
$query->limit(50);

$res = $factual->fetch("places", $query);  

$res2 = $res->getJson();
//echo $res2;
$data2=json_decode($res2);

//print_r($data2);
//$res3=$res->getData();

//print_r($res3);

//header("Content-type: text/xml");

// Start XML file, echo parent node
//echo '<markers>';
foreach ($data2->response->data as $value) {
    
    //echo '<marker ';
    echo 'lat="' .$value->latitude. '" ';
    echo 'lng="' .$value->longitude. '" ';

  //  echo 'name="' .$value->name. '" ';
    //echo 'admin_region="' .parseToXML($value->admin_region). '" ';
    //echo 'address="' .parseToXML($value->address). '" ';
    //echo 'category_id="' .$value->category_ids[0]. '" ';
    //echo 'category_id="' .parseToXML($value->category_labels[0][1]). '" ';
  //  echo '/>';
}
//echo '</markers>';
$query2 = new FactualQuery;

$query2->within(new FactualCircle(doubleval($ne->getLat()),  doubleval($ne->getLng()), 500));

//$query->search("pavia italy");
$query2->limit(50);

$res_ne = $factual->fetch("places", $query);  

$res2_ne = $res_ne->getJson();
//echo $res2;
$data2_ne=json_decode($res2_ne);

//print_r($data2);
//$res3=$res->getData();

//print_r($res3);

//header("Content-type: text/xml");

// Start XML file, echo parent node
//echo '<markers>';
foreach ($data2_ne->response->data as $value) {
    
    //echo '<marker ';
    echo 'lat="' .$value->latitude. '" ';
    echo 'lng="' .$value->longitude. '" ';

  //  echo 'name="' .$value->name. '" ';
    //echo 'admin_region="' .parseToXML($value->admin_region). '" ';
    //echo 'address="' .parseToXML($value->address). '" ';
    //echo 'category_id="' .$value->category_ids[0]. '" ';
    //echo 'category_id="' .parseToXML($value->category_labels[0][1]). '" ';
  //  echo '/>';
}




//echo '</markers>';






        