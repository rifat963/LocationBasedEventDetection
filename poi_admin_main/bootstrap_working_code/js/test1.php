<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
        $client_key = "M421SQPJS0YV5PZA2ZFSKZUTVSWKJAAOTNESIPXXMYCUSEBV"; // Your Client ID
	$client_secret = "ZG4FKJZYXLVQ2EDLQ3SSF3GPW5OGWTT0X33RI2MHGGQCPYQA"; // Your Client Secret
	

            $curlhandle = curl_init();
            curl_setopt($curlhandle, CURLOPT_URL, "https://api.foursquare.com/v2/venues/pavia?client_id=M421SQPJS0YV5PZA2ZFSKZUTVSWKJAAOTNESIPXXMYCUSEBV&client_secret=ZG4FKJZYXLVQ2EDLQ3SSF3GPW5OGWTT0X33RI2MHGGQCPYQA");
            curl_setopt($curlhandle, CURLOPT_RETURNTRANSFER, 1);

            $response = curl_exec($curlhandle);
            curl_close($curlhandle);

            $json = json_decode($response);
            foreach ($json->groups[0]->venues as $result)
            {
                echo $result->name.' - '.$result->address.' '.$result->city."<p />";
            } 

