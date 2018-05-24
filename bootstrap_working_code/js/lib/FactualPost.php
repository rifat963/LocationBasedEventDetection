<?php
/**
 * Factual Submittor vars collector
 * @author Tyler
 * @package Factual
 * @license Apache 2.0
 */
 class FactualPost{
 	
	//Required Params
	public $tableName = null; //The name of the table in which the entites is found you wish to flag (e.g. "places")
	public $factualID = null; //The Factual ID
	public $user = null; //Arbitrary User token
	//Optional Params
	public $comment = null;
	public $reference = null;
	public $values = null;
	
 	//Getters
 	public function getTableName(){
 		return $this->tableName;
 	} 	
 	public function getFactualID(){
 		return $this->factualID;
 	} 
 	//Setters
 	public function setTableName($var){
 		$this->tableName = $var;
 	}
 	public function setFactualID($var){
 		$this->factualID = $var;
 	} 	
 	public function setUserToken($var){
 		$this->user = $var;
 	} 	
 	public function setComment($var){
 		$this->comment = $var;
 	} 	 	
 	public function setReference($var){
 		$this->reference = $var;
 	} 	 	
 	 	
 	//vars to post will depend on child class
  	protected function getPostVars(){
 		return array("user","reference");
 	}

 	/**
 	 * Adds key/value pairs to object
 	 * @param string key Field/Column name
 	 * @param mixed value Value to add/edit/update
 	 * @return array set values
 	 */
 	public function setValue($key,$value){
 		$this->values[$key] = $value;
 		return $this->values;
 	}
 	
 	/**
 	 * Returns key/value pairs. JSON-encodes any arrays
 	 * @internal we'd usually do a reawurlencode() here. Note however that the oauth lib performs this function.
 	 * @internal may need to re-visit the way we handle empty values only here
 	 */
 	public function toUrlParams(){
 		$params = $this->getPostVars();
 		$temp = array();
 		foreach ($params as $var){
 			if ($this->$var){ //non empty values only
 				$val = $this->$var;
 				if (is_array($val)){ //json encode arrays
 					$val = json_encode($val);
 				}
 				$temp[$var] = $val;
 			}
 		}  		 		
 		return $temp;		
 	}

 	/**
 	 * Returns single URL string
 	 */ 	
 	public function toURLString(){
 		$temp = $this->toUrlParams();
 		foreach ($temp as $key => $value){
 				$temp2[] = $key."=".$value;
 		} 	 		
 		return implode("&", $temp2);
 	}
 	
 	/**
 	 * Dumps vars
 	 */
 	public function dump(){
 		return get_object_vars($this);
  	}
 	 	
 	/**
 	 * Clears the object
 	 */
 	public function clear(){
 		foreach (get_object_vars($this) as $var){
 			$this->$var = null;
 		}
 		return true;
 	}

 	
 }
 
?>
