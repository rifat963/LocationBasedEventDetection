<?php
/**
 * The Boost API enables you to signal to Factual that a specific row returned
 * by full-text search in a read API call should be a prominent result for that
 * search.
 * @author Tyler
 * @package Factual
 * @license Apache 2.0
 */
require_once("FactualPost.php");
class FactualBoost extends FactualPost {
	
	protected $q = null; //The full text search itself
	protected $factual_id = null; //factual ID getting boosted
	//protected $query = null; //The query object
	
	public function setQueryString($var){
		$this->q = $var;
	}
	
	public function setFactualID($var){
		$this->factual_id = $var;
	}

 	//override
  	protected function getPostVars(){
 		return array("user","q","factual_id");
 	}
	
}



?>