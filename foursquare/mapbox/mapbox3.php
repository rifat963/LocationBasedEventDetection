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
  <script>
  var map =  L.mapbox.map('map', 'iamdangavin.map-seme9ull');
	map.scrollWheelZoom.disable();
	
  //var features, markerLayer, interaction, container, items;

	navigator.geolocation.getCurrentPosition(function(data) {
		var lat = data['coords']['latitude'];
		var lng = data['coords']['longitude'];

		map.setView( [ lat, lng ], 12);

		$(function(){
			loadMarkers();
			map.on('dragend', function(e){
				map.markerLayer.clearLayers();
				loadMarkers();
			});
		});

		function loadMarkers() {
			map.markerLayer.clearLayers();
			bounds = map.getBounds();
			centerPoint = map.getSize().divideBy(2), targetLatLng = map.containerPointToLatLng(centerPoint);
			var latitude = targetLatLng.lat;
			var longitude = targetLatLng.lng;
			bounds = bounds._northEast.lat + ',' + bounds._northEast.lng + ',' + bounds._southWest.lat + ',' + bounds._southWest.lng;
			$.ajax({
				cache: true,
				url: 'https://api.foursquare.com/v2/venues/explore?ll='+latitude+','+longitude+'&radius=9999&query=burger&oauth_token=1JB3POF44JPEC5FDIENCBZRSIUD3SM0YUS5CTV3E2245LNHW&v=20120101&limit=999&bounds='+ bounds+'&callback=?',
				dataType: 'json',
					beforeSend : function(){
						$('.map-controls').append('<div class="loading">\
			                    <img src="http://upload.wikimedia.org/wikipedia/commons/d/de/Ajax-loader.gif?p' + new Date().getTime()+ '" />\
			                    </div>');
			        $loading = $('.loading');
			        $loading.hide();
			        $loading.fadeIn(201, function(){
				        loading = true;
							});
		      },
		      success: function(data){

						venues = data.response.groups[0].items;
						// Place marker for each venue.

						var features = [];

						for (var i = 0; i < venues.length; i++) {
					  	var foursquareIcon = 'https://foursquare.com/img/categories_v2/food/burger_64.png';
					    var latitude = venues[i].venue.location.lat;
					    var longitude = venues[i].venue.location.lng;

					    features.push({
						  	type: 'Feature',
						  	'geometry': {
							  	type: 'Point',
							  	coordinates: [longitude, latitude]
						  	},
						  	'properties': {
							  	'marker-color': '#000',
							  	'marker-symbol': '',
							  	'title': venues[i].venue.name,
							  	'link': venues[i].venue.canonicalUrl,
							  	'twitter': venues[i].venue.contact.twitter,
							  	'phone': venues[i].venue.location.formattedPhone,
							  	'address': venues[i].venue.location.address,
							  	'city': venues[i].venue.location.city,
							  	'state': venues[i].venue.location.state,
							  	'zip': venues[i].venue.location.zip,
							  	'here-now': venues[i].venue.hereNow,
						  	}
					    });

					  }
					  // Add them!
						map.markerLayer.setGeoJSON(features);

						// Cycle through markers once geoJson is ready.
						// Add custom popups to each using our custom feature properties
						map.markerLayer.on('ready', function(e) {
						    this.eachLayer(function(marker) {
						
						        var feature = marker.feature;
						
						        // Create custom popup content
						        var popupContent =  '<a target="_blank" class="popup" href="' + feature.properties.link + '">' +
						                                '<img src="' + foursquareIcon + '">' +
						                            '   <h2>' + feature.properties.name + '</h2>' +
						                            '</a>';
						
						        // http://leafletjs.com/reference.html#popup
						        marker.bindPopup(popupContent,{
						            closeButton: false,
						            minWidth: 320
						        });
						
						    });
						});

					},
					complete: function(){
						$('.loading').fadeOut().remove();
					},
					error: function(jqXHR, textStatus, errorThrown){
						console.log(jqXHR + " :: " + textStatus + " :: " + errorThrown);
					}
				});
			}
	});
  </script>
  


</head>
<body>
<div class="map-controls"></div>

        <div id="map" style="width: 100%; height: 500px;">
</div>
</body>
</html>
 
</html>
