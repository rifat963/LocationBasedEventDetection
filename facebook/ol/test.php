<?php

require '../src/facebook.php';
$facebook = new Facebook(array(
          'appId'  => '293889024111663',
          'secret' => 'c1a539eb1e5eb3e9f4ecfd344ffe842e',
          'cookie' => true,
        ));
$loginUrl = $facebook->getLoginUrl(
        array('scope' => 'status_update,publish_stream,publish_checkins,user_checkins,user_location,user_status,user_checkins')
);

// Session based API call
if ($user) {
    try {
            $me = $facebook->api('/me');
            if($me)
            {
                $_SESSION['fbID'] = $me['id'];                
            }
        } catch (FacebookApiException $e) {
          error_log($e);   
        }
}
else {
   echo "<script type='text/javascript'>top.location.href='$loginUrl';</script>";
   exit;
}

function checkin($fb)
{
    try
    {
        $tryCatch = $fb->api('/'.$_SESSION['fbID'].'/checkins', 'POST', array(
            'access_token' => $fb->getAccessToken(), //corrected
            'place' => '165122993538708',
            'message' =>'I went to placename today',
            'coordinates' => json_encode(array(
                'latitude'  => '1.3019399200902',
                'longitude' => '103.84067653695'
            ))
        ));
    }
    catch(FacebookApiException $e)
    {
        $tryCatch=$e->getMessage();
    }
    return $tryCatch;
}

checkin($facebook); //calling the function and passing facebook object to function
