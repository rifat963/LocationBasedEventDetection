<!DOCTYPE html>
<html>
<head>
<meta charset=utf-8 />
<title>Places from Foursquare</title>

<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no' />
<script src='https://api.tiles.mapbox.com/mapbox.js/v1.6.2/mapbox.js'></script>
<link href='https://api.tiles.mapbox.com/mapbox.js/v1.6.2/mapbox.css' rel='stylesheet' />

<style>
  body { margin:0; padding:0; }
  #map { position:absolute; top:0; bottom:0; width:100%; }
</style>
</head>
<body>
<script src='http://code.jquery.com/jquery-1.10.1.min.js'></script>
<div id='map'></div>
<script>
var map = L.mapbox.map('map', 'examples.map-i86nkdio', {
        attributionControl: true
    })
    .setView([45.185714,9.156319], 14);

// Credit Foursquare for their wonderful data
map.attributionControl
    .addAttribution('<a href="https://foursquare.com/">Places data from Foursquare</a>');

// Create a Foursquare developer account:
// https://developer.foursquare.com/
// AND CHANGE THESE VALUES TO YOUR OWN:
// otherwise they can be cycled or deactivated with zero notice.
var CLIENT_ID = 'M421SQPJS0YV5PZA2ZFSKZUTVSWKJAAOTNESIPXXMYCUSEBV';
var CLIENT_SECRET = 'ZG4FKJZYXLVQ2EDLQ3SSF3GPW5OGWTT0X33RI2MHGGQCPYQA';

// https://developer.foursquare.com/start/search
var API_ENDPOINT = 'https://api.foursquare.com/v2/venues/search' +
  '?client_id=CLIENT_ID' +
  '&client_secret=CLIENT_SECRET' +
  '&v=20130815' +
  '&ll=LATLON' +
  '&radius=9999'+
  '&limit=50'
  '&callback=?';

// Keep our place markers organized in a nice group.
var foursquarePlaces = L.layerGroup().addTo(map);

// Use jQuery to make an AJAX request to Foursquare to load markers
// data.
$.getJSON(API_ENDPOINT
    .replace('CLIENT_ID', CLIENT_ID)
    .replace('CLIENT_SECRET', CLIENT_SECRET)
    .replace('LATLON', map.getCenter().lat +
        ',' + map.getCenter().lng), function(result, status) {

    if (status !== 'success') return alert('Request to Foursquare failed');

    // Transform each venue result into a marker on the map.
    for (var i = 0; i < result.response.venues.length; i++) {
      var venue = result.response.venues[i];
      var latlng = L.latLng(venue.location.lat, venue.location.lng);
      var marker = L.marker(latlng)
        .bindPopup('<h2><a href="https://foursquare.com/v/' + venue.id + '">' +
            venue.name + '</a></h2>')
        .addTo(foursquarePlaces);
    }

});
</script>
</body>
</html>