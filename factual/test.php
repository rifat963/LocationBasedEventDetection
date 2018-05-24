<?php

require_once('lib\Factual.php');
$factual = new Factual("xoTuroWBWXqA8nQrwOqDfj3ukGjgaJfvrvblbchH","W3kEHsD0stAvZNcUQw22k51I4ItmOEP0ZXMiRFCA");

// Find entities that match a full text search for Sushi in Santa Monica:
//$query = new FactualQuery;
//$query->search("pavia italy");

$query = new FactualQuery;
$query->search("pavia italy");
$query->limit(50);
//$query->add("latitude", 45.1894664);
//$query->add("longitude", 9.163067);

//$query = new ResolveQuery();
//$query->add("name", "pavia italy");
//$query->add("latitude", 45.1894664);
//$query->add("longitude", 9.163067);
//$query->limit(50);
$res = $factual->fetch("places", $query);  

//$res = $factual->fetch("places", $query);
print_r($res->getData());


//$res = $factual->fetch("places", $query);
//$query = new FactualQuery;
//$query->limit(10);
//$query->sortAsc("region");
//$query->sortAsc("locality");
//$query->sortDesc("name");
//$query->sortAsc("placerank");


//$res = $factual->fetch("places", $query);
//$res = $res->getJson();
//echo $res;
//print_r($res->getData());
//print_r($res->getData());

// Build a query to find places whose name field starts with "Starbucks"
//$query = new FactualQuery;
//$query->field("name")->beginsWith("Starbucks");
//$res = $factual->fetch("places", $query);
//print_r($res->getData());  

// Build a query to find places with a blank telephone number
//$query = new FactualQuery;
//$query->field("tel")->blank();
//$res = $factual->fetch("places", $query);
//print_r($res->getData());


//$query = new FactualQuery;
//$query->field("rating")->greaterThanOrEqual(7.5);
//$query->field("region")->in(array("MA", "VT", "NH", "RI", "CT"));
//$query->field("tel")->blank();
//$query->field("rating")->greaterThan(7.5);
//$res = $factual->fetch("places", $query);
//$res = $factual->fetchRow($tableName, $factualID);
//print_r($res->getData());
//$factualID = "03c26917-5d66-4de9-96bc-b13066173c65";
//$tableName = "places";
//fetch row
//$res = $factual->fetchRow($tableName, $factualID,$gt);
 
//$query = new FactualQuery;
//$query->limit(10);
//$query->sortAsc("name");
//$res = $factual->fetch("places", $query);
//print_r($res->getData()); 
?>

