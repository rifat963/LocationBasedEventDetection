<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="content-type" content="text/html; charset=UTF-8"> 
        <meta charset="utf-8">
        <title>Point of Interest</title>
       
        <meta name="generator" content="Bootply" />
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
        <link href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css" rel="stylesheet">
        
        <!--[if lt IE 9]>
          <script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->
        <link rel="shortcut icon" href="/bootstrap/img/favicon.ico">
        <link rel="apple-touch-icon" href="/bootstrap/img/apple-touch-icon.png">
        <link rel="apple-touch-icon" sizes="72x72" href="/bootstrap/img/apple-touch-icon-72x72.png">
        <link rel="apple-touch-icon" sizes="114x114" href="/bootstrap/img/apple-touch-icon-114x114.png">
        <link href="css/styles.css" rel="stylesheet">
         <script type="text/javascript" src="https://www.google.com/jsapi"></script>
        <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?libraries=places&sensor=false"></script>
        <script src="js/script3.js"></script>
       <script type='text/javascript' src="//ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
        <script type='text/javascript' src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>
        <script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
        <script src="http://d3js.org/d3.v3.min.js"></script>
        <script src="js/dndTree2.js"></script>
        
        
        <style type="text/css">
  
	.node {
    cursor: pointer;
  }

  .overlay{
      background-color:#EEE;
  }
   
  .node circle {
    fill: #fff;
    stroke: steelblue;
    stroke-width: 1.5px;
  }
   
  .node text {
    font-size:10px; 
    font-family:sans-serif;
  }
   
  .link {
    fill: none;
    stroke: #ccc;
    stroke-width: 1.5px;
  }

  .templink {
    fill: none;
    stroke: red;
    stroke-width: 3px;
  }

  .ghostCircle.show{
      display:block;
  }

  .ghostCircle, .activeDrag .ghostCircle{
       display: none;
  }

</style>
        
    </head>
    
   
    <body>
        
        <!-- begin template -->
        <div class="navbar navbar-custom navbar-fixed-top">
        <div class="navbar-header"><a class="navbar-brand" href="#">POI</a>
            <a class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>
    </div>
    <div class="navbar-collapse collapse">
      <ul class="nav navbar-nav">
        <li class="active"><a href="#">Home</a></li>
        <li><a href="index4.php">Decision Tree</a></li>
        <li><a href="allData.php">Rank Data</a></li>
        <li><a href="#">Statistics</a></li>
        <li>&nbsp;</li>
      </ul>
      <form class="navbar-form">
        <div class="form-group" style="display:inline;">
          <div class="input-group">
            <div class="input-group-btn">
              <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"><span class="glyphicon glyphicon-chevron-down"></span></button>
              <ul class="dropdown-menu">
                <li><a href="#">Facebook</a></li>
                <li><a href="#">Foursquare</a></li>
                <li><a href="#">Factual</a></li> 
              </ul>
            </div>
            <input type="text" class="form-control" placeholder="What are searching for?">
            <span class="input-group-addon"><span class="glyphicon glyphicon-search"></span> </span>
          </div>
        </div>
      </form>
    </div>
</div>

<div id="tree-container"></div>
<div class="container-fluid" id="main">
  <div class="row">
  	<div class="col-xs-4" id="left">
  
      <h2>Point Of Interest</h2>

      <hr>
      
      <div class="panel panel-default">
        <div class="panel-heading"><a href="">Decision Tree</a></div>
      </div>
      <p>
          <a href="index4.php" class="center-block btn btn-primary">Generate Decision Tree</a>
    
      </p>
      <p>
          <a href="index3.php" class="center-block btn btn-primary">Show Map Data</a>
    
      </p>
      
      <hr>
         <!-- item list -->
      <div class="panel panel-default">
        <div class="panel-heading"><a href="">Places Search</a></div>
      </div>
      <p>
      <div id="button2" class="center-block btn btn-primary" onclick="findallplaces(); return false;">Search all Object</div>
        </p>
      <p>
      <div id="button2" class="center-block btn btn-primary" onclick="foursquare(); return false;">Foursquare</div>
      </p>
      <p>
      <div id="button2" class="center-block btn btn-primary" onclick="facebook(); return false;">Facebook</div>
      </p>
      <p>
      <div id="button2" class="center-block btn btn-primary" onclick="factual(); return false;">Factual</div>
      </p>
      <p>
      <div id="button2" class="center-block btn btn-primary" onclick="clearmap(); return false;">Clear Map</div>
      </p>
      <hr>

    </div>
    <div class="col-xs-8"><!--map-canvas will be postioned here--></div>
    
  </div>
</div>
<!-- end template -->

        
        </script>
        
      
    </body>
</html>