var geocoder;
var map;
var markers = Array();
var infos = Array();

function initialize() {
    // prepare Geocoder
    geocoder = new google.maps.Geocoder();

    // set initial position (New York)
    var myLatlng = new google.maps.LatLng(45.1894664,9.163067);

    var myOptions = { // default map options
        zoom: 14,
        center: myLatlng,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById('map-canvas'), myOptions);
    
    
     var input = /** @type {HTMLInputElement} */(
      document.getElementById('pac-input'));


  var autocomplete = new google.maps.places.Autocomplete(input);
  autocomplete.bindTo('bounds', map);

  var infowindow = new google.maps.InfoWindow();
  var marker = new google.maps.Marker({
    map: map,
    anchorPoint: new google.maps.Point(0, -29)
  });

  google.maps.event.addListener(autocomplete, 'place_changed', function() {
    infowindow.close();
    marker.setVisible(false);
    var place = autocomplete.getPlace();
    if (!place.geometry) {
      return;
    }

    // If the place has a geometry, then present it on a map.
    if (place.geometry.viewport) {
      map.fitBounds(place.geometry.viewport);
    } else {
      map.setCenter(place.geometry.location);
      map.setZoom(17);  // Why 17? Because it looks good.
    }
    marker.setIcon(/** @type {google.maps.Icon} */({
      url: place.icon,
      size: new google.maps.Size(71, 71),
      origin: new google.maps.Point(0, 0),
      anchor: new google.maps.Point(17, 34),
      scaledSize: new google.maps.Size(35, 35)
    }));
    marker.setPosition(place.geometry.location);
    marker.setVisible(true);

    var address = '';
    if (place.address_components) {
      address = [
        (place.address_components[0] && place.address_components[0].short_name || ''),
        (place.address_components[1] && place.address_components[1].short_name || ''),
        (place.address_components[2] && place.address_components[2].short_name || '')
      ].join(' ');
    }

    infowindow.setContent('<div><strong>' + place.name + '</strong><br>' + address);
    infowindow.open(map, marker);
  });

  // Sets a listener on a radio button to change the filter type on Places
  // Autocomplete.
  function setupClickListener(id, types) {
    var radioButton = document.getElementById(id);
    google.maps.event.addDomListener(radioButton, 'click', function() {
      autocomplete.setTypes(types);
    });
  }

  setupClickListener('changetype-all', []);
  setupClickListener('changetype-establishment', ['establishment']);
  setupClickListener('changetype-geocode', ['geocode']);
}

function clearmap(){
    clearOverlays();
    clearInfos();
}
// clear overlays function
function clearOverlays() {
    if (markers) {
        for (i in markers) {
            markers[i].setMap(null);
        }
        markers = [];
        infos = [];
    }
}

// clear infos function
function clearInfos() {
    if (infos) {
        for (i in infos) {
            if (infos[i].getMap()) {
                infos[i].close();
            }
        }
    }
}

// find address function
function findAddress() {
    var address = document.getElementById("gmap_where").value;

    // script uses our 'geocoder' in order to find location by address name
    geocoder.geocode( { 'address': address}, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) { // and, if everything is ok

            // we will center map
            var addrLocation = results[0].geometry.location;
            map.setCenter(addrLocation);

            // store current coordinates into hidden variables
            document.getElementById('lat').value = results[0].geometry.location.lat();
            document.getElementById('lng').value = results[0].geometry.location.lng();

            // and then - add new custom marker
            var addrMarker = new google.maps.Marker({
                position: addrLocation,
                map: map,
                title: results[0].formatted_address,
                icon: 'marker.png'
            });
        } else {
            alert('Geocode was not successful for the following reason: ' + status);
        }
    });
}

// find custom places function
function findPlaces() {

    // prepare variables (filter)
    var type = document.getElementById('gmap_type').value;
    var radius = document.getElementById('gmap_radius').value;
    var keyword = document.getElementById('gmap_keyword').value;

    var lat = document.getElementById('lat').value;
    var lng = document.getElementById('lng').value;
    
    
    var cur_location = new google.maps.LatLng(lat, lng);

    // prepare request to Places
    var request = {
        location: cur_location,
        radius: radius,
        types: [type]
    };
    if (keyword) {
        request.keyword = [keyword];
    }

    // send request
    service = new google.maps.places.PlacesService(map);
    service.search(request, createMarkers);
}

function allobject(){
    
    facebook();
    foursquare();
    factual();
    
}

// find custom all places function
function findallplaces() {

    // prepare variables (filter)
   // var type = document.getElementById('gmap_type').value;
    //var radius = document.getElementById('gmap_radius').value;
    //var keyword = document.getElementById('gmap_keyword').value;
    var radius=500;
   // var lat = document.getElementById('lat').value;
   // var lng = document.getElementById('lng').value;
    var lat = 45.1894664;
    var lng = 9.163067;
    var cur_location = new google.maps.LatLng(lat, lng);
    
    
    // prepare request to Places
    var request = {
        location: cur_location,
        radius: radius,
        type:'finance'
        
    };
  //  if (keyword) {
   //     request.keyword = [keyword];
   // }

    // send request
    service = new google.maps.places.PlacesService(map);
    service.search(request, createMarkers);
}


// create markers (from 'findPlaces' function)
function createMarkers(results, status) {
    if (status == google.maps.places.PlacesServiceStatus.OK) {

        // if we have found something - clear map (overlays)
        clearOverlays();

        // and create new markers by search result
        for (var i = 0; i < results.length; i++) {
            createMarker(results[i]);
        }
    } else if (status == google.maps.places.PlacesServiceStatus.ZERO_RESULTS) {
        alert('Sorry, nothing is found');
    }
}

// creare single marker function
function createMarker(obj) {

    // prepare new Marker object
    var mark = new google.maps.Marker({
        position: obj.geometry.location,
        map: map,
        title: obj.name
    });
    markers.push(mark);

    // prepare info window
    var infowindow = new google.maps.InfoWindow({
        content: '<img src="' + obj.icon + '" /><font style="color:#000;">' + obj.name +
        '<br />Rating: ' + obj.rating + '<br />Vicinity: ' + obj.vicinity + '</font>'
    });

    // add event handler to current marker
    google.maps.event.addListener(mark, 'click', function() {
        clearInfos();
        infowindow.open(map,mark);
    });
    infos.push(infowindow);
}


var customIcons = {
      restaurant: {
        icon: 'http://labs.google.com/ridefinder/images/mm_20_blue.png'
      },
      bar: {
        icon: 'http://labs.google.com/ridefinder/images/mm_20_red.png'
      }
    };
var infoWindow = new google.maps.InfoWindow;


    function foursquare(){
      // Change this depending on the name of your PHP file
      downloadUrl("js/xml_foursquare_bound.php", function(data) {
        var xml = data.responseXML;
        var markers = xml.documentElement.getElementsByTagName("marker");
        for (var i = 0; i < markers.length; i++) {
          var name = markers[i].getAttribute("venue_name");
          var venue_categories = markers[i].getAttribute("venue_categories");
          var venue_herenow_count = markers[i].getAttribute("venue_herenow_count");
          var venue_stats_userscount=markers[i].getAttribute("venue_stats_userscount");
          var venue_stats_checkinscount=markers[i].getAttribute("venue_stats_checkinscount");
          var point = new google.maps.LatLng(parseFloat(markers[i].getAttribute("lat")),parseFloat(markers[i].getAttribute("lng")));
          var html = "<b>venue: " + name + "</b> <br/><b>venue categories: " + venue_categories+"</b><br/><b>venue stats userscount: "+venue_stats_userscount+"</b><br/><b>venue stats checkinscount :"+venue_stats_checkinscount+"</b>";
          var icon = customIcons[venue_herenow_count] || {};
          //var iconBase = 'https://maps.google.com/mapfiles/kml/shapes/';
            var marker = new google.maps.Marker({
            map: map,
            position: point,
            icon: icon.icon//iconBase //+ 'schools_maps.png'
          });
          bindInfoWindow(marker, map, infoWindow, html);
        }
      });
    }
   
    function facebook(){
      // Change this depending on the name of your PHP file
      downloadUrl("js/FQL_database.php", function(data) {
        var xml = data.responseXML;
        var markers = xml.documentElement.getElementsByTagName("marker");
        for (var i = 0; i < markers.length; i++) {
          var name = markers[i].getAttribute("name");
          var street = markers[i].getAttribute("street");
          var category = markers[i].getAttribute("category");
          var id=markers[i].getAttribute("id");
          var checkin=markers[i].getAttribute("checkin_count");
         // var checkincount=markers[i].getAttribute("checkinscount");
          var point = new google.maps.LatLng(parseFloat(markers[i].getAttribute("lat")),parseFloat(markers[i].getAttribute("lng")));
          var html = "<b>venue: " + name + "</b> <br/><b>Street:" + street+"</b><br/><b>category: "+category+"</b><br/><b>Fb page id :"+id+"</b>"+"</b><br/><b>checkin :"+checkin+"</b>";
          var icon = customIcons[category] || {};
          //var iconBase = 'https://maps.google.com/mapfiles/kml/shapes/';
            var marker = new google.maps.Marker({
            map: map,
            position: point,
            icon: icon.icon//iconBase //+ 'schools_maps.png'
          });
          bindInfoWindow(marker, map, infoWindow, html);
        }
      });
    }
    
    function matched(){
      // Change this depending on the name of your PHP file
      downloadUrl("js/POI_database_xml.php", function(data) {
        var xml = data.responseXML;
        var markers = xml.documentElement.getElementsByTagName("marker");
        for (var i = 0; i < markers.length; i++) {
          var name = markers[i].getAttribute("name");
          var street = markers[i].getAttribute("street");
          var category = markers[i].getAttribute("category");
          var description = markers[i].getAttribute("category");
          var id=markers[i].getAttribute("id");
          var checkin=markers[i].getAttribute("checkin_count");
         // var checkincount=markers[i].getAttribute("checkinscount");
          var point = new google.maps.LatLng(parseFloat(markers[i].getAttribute("lat")),parseFloat(markers[i].getAttribute("lng")));
          var html ="<b>Place Name: " + name + "</b> <br/><b>Address:" + street+"</b><br/><b>category: "+category+"</b><br/><b>checkin :"+checkin+"</b>";
          var icon = customIcons[category] || {};
          //var iconBase = 'https://maps.google.com/mapfiles/kml/shapes/';
            var marker = new google.maps.Marker({
            map: map,
            position: point,
            icon: icon.icon//iconBase //+ 'schools_maps.png'
          });
          bindInfoWindow(marker, map, infoWindow, html);
        }
      });
    }
    
    function factual(){
      // Change this depending on the name of your PHP file
      downloadUrl("js/factual.php", function(data) {
        var xml = data.responseXML;
        var markers = xml.documentElement.getElementsByTagName("marker");
        for (var i = 0; i < markers.length; i++) {
          var name = markers[i].getAttribute("name");
          var street = markers[i].getAttribute("address");
          var category = markers[i].getAttribute("admin_region");
          var id=markers[i].getAttribute("category_id");
         // var checkincount=markers[i].getAttribute("checkinscount");
          var point = new google.maps.LatLng(parseFloat(markers[i].getAttribute("lat")),parseFloat(markers[i].getAttribute("lng")));
          var html = "<b>venue: " + name + "</b> <br/><b>description: " + street+"</b><br/><b>category: "+category+"</b><br/><b>id :"+id+"</b>";
          var icon = customIcons[category] || {};
          //var iconBase = 'https://maps.google.com/mapfiles/kml/shapes/';
            var marker = new google.maps.Marker({
            map: map,
            position: point,
            icon: icon.icon//iconBase //+ 'schools_maps.png'
          });
          bindInfoWindow(marker, map, infoWindow, html);
        }
      });
    }
    

    function bindInfoWindow(marker, map, infoWindow, html) {
      google.maps.event.addListener(marker, 'click', function() {
        infoWindow.setContent(html);
        infoWindow.open(map, marker);
      });
    }

    function downloadUrl(url, callback) {
      var request = window.ActiveXObject ?
          new ActiveXObject('Microsoft.XMLHTTP') :
          new XMLHttpRequest;

      request.onreadystatechange = function() {
        if (request.readyState == 4) {
          request.onreadystatechange = doNothing;
          callback(request, request.status);
        }
      };

      request.open('GET', url, true);
      request.send(null);
    }

   function doNothing() {}



// initialization
google.maps.event.addDomListener(window, 'load', initialize);

