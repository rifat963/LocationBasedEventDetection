<!DOCTYPE html>
<html>
  <head>
    <title>Place searches</title>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <style>
      html, body, #map-canvas {
        height: 100%;
        margin: 0px;
        padding: 0px
      }
    </style>
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=true&libraries=places"></script>
    <script>
var map;
var infowindow;

function initialize() {
  var pyrmont = new google.maps.LatLng(45.1894664,9.163067);

  map = new google.maps.Map(document.getElementById('map-canvas'), {
    center: pyrmont,
    zoom: 15
  });

  var request1 = {
    location: pyrmont,
    radius: 5000,
    types: ['store']
  };
  infowindow = new google.maps.InfoWindow();
  var service1 = new google.maps.places.PlacesService(map);
  service1.nearbySearch(request1, callback);
  
   var request2 = {
    location: pyrmont,
    radius: 5000,
    types: ['restaurant']
  };
  infowindow = new google.maps.InfoWindow();
  var service2 = new google.maps.places.PlacesService(map);
  service2.nearbySearch(request2, callback);
  
   var request3 = {
    location: pyrmont,
    radius: 5000,
    types: ['bar']
  };
  infowindow = new google.maps.InfoWindow();
  var service3 = new google.maps.places.PlacesService(map);
  service3.nearbySearch(request3, callback);
  
   var request4 = {
    location: pyrmont,
    radius: 5000,
    types: ['cafe','hotels']
  };
  infowindow = new google.maps.InfoWindow();
  var service4 = new google.maps.places.PlacesService(map);
  service4.nearbySearch(request4, callback);
  
  
}

function callback(results, status) {
  if (status == google.maps.places.PlacesServiceStatus.OK) {
    for (var i = 0; i < results.length; i++) {
      createMarker(results[i]);
    }
  }
}

function createMarker(place) {
  var placeLoc = place.geometry.location;
  var marker = new google.maps.Marker({
    map: map,
    position: place.geometry.location
  });

  google.maps.event.addListener(marker, 'click', function() {
    infowindow.setContent(place.name);
    infowindow.open(map, this);
  });
}

google.maps.event.addDomListener(window, 'load', initialize);

    </script>
  </head>
  <body>
    <div id="map-canvas"></div>
  </body>
</html>
