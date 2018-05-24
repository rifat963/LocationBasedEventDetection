<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


define('YOUR_API_KEY','AIzaSyAzpjIr3JJPX-_3051t-PyMwAPfsanKVJs');
$searchString = "pavia";
$url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=$searchString&sensor=false&key=AIzaSyAzpjIr3JJPX-_3051t-PyMwAPfsanKVJs";
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_PROXYPORT, 3128);
curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, 0);
$response = curl_exec($ch);
curl_close($ch);
$responseData = json_decode($response);
print_r($responseData); //data will be here

?>