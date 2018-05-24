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
        <script src="js/script2.js"></script>
       
        <script type='text/javascript' src="//ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
        <script type='text/javascript' src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>
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
        <li><a href="#">Rank Data</a></li>
        <li><a href="allData.php">All Data</a></li>
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
             <input id="pac-input" type="text" class="form-control" placeholder="What are searching for?">
             <span class="input-group-addon"><span class="glyphicon glyphicon-search"></span> </span>
          </div>
        </div>
      </form>
    </div>
</div>

        <?php
        
        $username='root';
        $password='';
        $database='point_of_interest';
        
        // Opens a connection to a MySQL server
        $connection=mysql_connect ('localhost', $username, $password);
        if (!$connection) {
             die('Not connected : ' . mysql_error());
        }

        // Set the active MySQL database
        $db_selected = mysql_select_db($database, $connection);
        if (!$db_selected) {
             die ('Can\'t use db : ' . mysql_error());
        }

        // Select all the rows in the markers table
        $query = "SELECT * FROM poi_facebook WHERE 1";
        $result = mysql_query($query);
        if (!$result) {
            die('Invalid query: ' . mysql_error());
        }

        
        
        ?>
        
        
        
        <div id="tree-container">
     
            <div class="table-responsive">
                <table class="table">
                    <?php
                    while ($row = @mysql_fetch_assoc($result)){
                    ?>
                    <tr>
                        <td>
                            <?php  $row['fb_id']?>
                        </td>
                        <td>
                            <?php  $row['name']?>
                        </td>
                        <td>
                            <?php  $row['name']?>
                        </td>
                        <td>
                            <?php  $row['name']?>
                        </td>
                        <td>
                            <?php  $row['name']?>
                        </td>
                        <td>
                            <?php  $row['name']?>
                        </td>
                        <td>
                            <?php  $row['name']?>
                        </td>
                    </tr>
                    <?php  } ?>
                </table>
            </div>
            
           <div class="table-responsive">
                <table class="table">
                    <tr>
                        <td>
                            test's
                        </td>
                        <td>
                            test's
                        </td>
                    </tr>
                    <tr>
                         <td>
                            test's
                        </td>
                        <td>
                            test's
                        </td>
                    </tr>
                    
                </table>
            </div>
            
        </div>


<div class="container-fluid" id="main">
  <div class="row">
  	
      <div class="col-xs-4" id="left">
    
      <h2>Point Of Interest</h2>
      
      <hr>
         <!-- item list -->
      <div class="panel panel-default">
        <div class="panel-heading"><a href="">Places Data</a></div>
      </div>
      <p>
      <div id="button2" class="center-block btn btn-primary" onclick="allobject(); return false;">All data</div>
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
      <div id="button2" class="center-block btn btn-primary" onclick="clearmap(); return false;">Reset</div>
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