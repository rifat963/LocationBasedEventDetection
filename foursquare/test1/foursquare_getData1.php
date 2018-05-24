<?php
/*
 * Foursquare latest checkin widget example.
 * Example of a webpage that display a widget that uses the foursquare.php class to
 * connect to foursquare and retrive your last checkin details and the coresponding
 * google map. Use the Foursquare API v2
 * Version: 0.2
 * License: GPL v3
 * Author: Elie Bursztein (fourlocfetcher@elie.im)
 * URL: http://code.google.com/p/foursquare-php/
 * License: GPL v3
 * Version: 0.2
 */

require_once 'foursquare.php';                                             //loading the class
$fq = new fourSquare("DS*****************");  //fetching the check-in data
?>
    
    <div id="foursquare" style="text-align:center">
    <h2>Last known location:</h2>

    <!--displaying the foursquare logo for the venue type-->
    <img src="<?php echo $fq->venueIcon ?>" />

    <!--displaying the venue name and the venue type-->
    <?php echo $fq->venueName ?> (<?php echo $fq->venueType ?>)<br/>

    <!-- displaying the venue address -->
    <?php echo $fq->venueAddress . ", " . $fq->venueCity . ", " . $fq->venueState . ", " . $fq->venueCountry ?><br/>
    <!--Displaying the map-->
    <img src="<?php echo $fq->getMapUrl(250, 250) ?>" /><br/>

    <!-- displaying the user comment-->
    <i><?php echo $fq->comment ?></i><br>
</div>