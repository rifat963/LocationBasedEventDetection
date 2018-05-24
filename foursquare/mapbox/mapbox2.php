<!DOCTYPE html>
<html>
<head>
  <meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no' />
  <script src='http://api.tiles.mapbox.com/mapbox.js/v1.2.0/mapbox.js'></script>
  <link href='http://api.tiles.mapbox.com/mapbox.js/v1.2.0/mapbox.css' rel='stylesheet' />
  <!--[if lte IE 8]>
    <link href='http://api.tiles.mapbox.com/mapbox.js/v1.3.1/mapbox.ie.css' rel='stylesheet' >
  <![endif]-->
  <script src='http://code.jquery.com/jquery-1.8.3.min.js'></script>
  <style>
    body { margin:0; padding:0; }
    #map { position:absolute; top:0; bottom:0; width:100%; }
  </style>
</head>
<body>
<div id='map'></div>
<script type='text/javascript'>


var map = L.mapbox.map('map', 'examples.map-vyofok3q').setView([3.15873,101.69742], 17);

var latlng = map.getCenter();

$.ajax({
    type: "GET",
    dataType: "json",
    cache: false,
    url: 'https://api.foursquare.com/v2/venues/explore?ll=' + latlng.lat + ',' + latlng.lng + '&limit=30&client_id=M421SQPJS0YV5PZA2ZFSKZUTVSWKJAAOTNESIPXXMYCUSEBV&client_secret=ZG4FKJZYXLVQ2EDLQ3SSF3GPW5OGWTT0X33RI2MHGGQCPYQA',
    success: function(data){
    	var base = data.response.groups[0].items

    	$.each(base, function(index){

    		var url = base[index].venue.url;
    		var llat = base[index].venue.location.lat;
    		var llng = base[index].venue.location.lng;
    		var name = base[index].venue.name;
    		var icon = base[index].venue.categories[0].icon;

    		var myIcon = L.icon({
    		    iconUrl: icon,
    		    iconSize: [20, 20],
    		    iconAnchor: [10, 10],
    		    className: '4sq-icon'
    		});

    		var marker = L.marker([llat, llng], {icon: myIcon}).addTo(map);
    		var content = '<a href="' + url + '" />' + name + '</a>'
    		marker.bindPopup(content)
    	})
    }
});
</script>
</body>
</html>
 
</html>
