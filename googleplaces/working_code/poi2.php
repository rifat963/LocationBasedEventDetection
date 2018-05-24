<!DOCTYPE html>
<html lang="en" >
    <head>
        <meta charset="utf-8" />
        <title>Google Places API</title>
        <link href="css/main.css" rel="stylesheet" type="text/css" />
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
        <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?libraries=places&sensor=false"></script>
        <script src="js/script.js"></script>
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <script src="js/bootstrap.min.js"></script>
        
    </head>
    <body>
        <!-- begin template -->
<div class="navbar navbar-custom navbar-fixed-top">
 <div class="navbar-header"><a class="navbar-brand" href="#"></a>
      <a class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </a>
    </div>
    <div class="navbar-collapse collapse">
   
      <form class="navbar-form">
        <div class="form-group" style="display:inline;">
          <div class="input-group">
            <div class="input-group-btn">
              <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"><span class="glyphicon glyphicon-chevron-down"></span></button>
              <ul class="dropdown-menu">
                <li><a href="#">Category 1</a></li>
                <li><a href="#">Category 2</a></li>
                <li><a href="#">Category 3</a></li>
                <li><a href="#">Category 4</a></li>
                <li><a href="#">Category 5</a></li> 
              </ul>
            </div>
            <input type="text" class="form-control" placeholder="What are searching for?">
            <span class="input-group-addon"><span class="glyphicon glyphicon-search"></span> </span>
          </div>
        </div>
      </form>
    </div>
</div>
        
        <div id="container" class="container-fluid">
            <div class="row">    
            <div class="col-xs-8">
                
            <div id="gmap_canvas"></div>
            
            </div>
            
            <div class="actions">
                <div class="button">
                    <label for="gmap_where">Where:</label>
                    <input id="gmap_where" type="text" name="gmap_where" /></div>
                <div id="button2" class="button" onclick="findAddress(); return false;">Search for address</div>
                <div class="button">
                    <label for="gmap_keyword">Keyword (optional):</label>
                    <input id="gmap_keyword" type="text" name="gmap_keyword" /></div>
                <div class="button">
                    <label for="gmap_type">Type:</label>
                    <select id="gmap_type">
                        <option value="art_gallery">art_gallery</option>
                        <option value="atm">atm</option>
                        <option value="bank">bank</option>
                        <option value="bar">bar</option>
                        <option value="cafe">cafe</option>
                        <option value="food">food</option>
                        <option value="hospital">hospital</option>
                        <option value="police">police</option>
                        <option value="store">store</option>
                    </select>
                </div>
                <div class="button">
                    <label for="gmap_radius">Radius:</label>
                    <select id="gmap_radius">
                        <option value="500">500</option>
                        <option value="1000">1000</option>
                        <option value="1500">1500</option>
                        <option value="5000">5000</option>
                    </select>
                </div>
                <input type="hidden" id="lat" name="lat" value="40.7143528" />
                <input type="hidden" id="lng" name="lng" value="-74.0059731" />
                <div id="button1" class="button" onclick="findPlaces(); return false;">Search for objects</div>
                <div id="button2" class="button" onclick="findallplaces(); return false;">Search all Object</div>
          
            </div>
            </div>
            
        </div>
    </body>
</html>