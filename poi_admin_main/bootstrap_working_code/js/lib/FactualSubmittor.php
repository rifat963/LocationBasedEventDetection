<?php
/**
 * Factual Submittor vars collector for adding, updating, and clearing values
 * @author Tyler
 * @package Factual
 * @license Apache 2.0
 */
 require_once("FactualPost.php");
 class FactualSubmittor extends FactualPost{
	//Required Params	(uses API parameter names)
	public $values = array();	//values to be submitted
	protected $strict = false;		//strict mode (accept submissions with only valid attributes)
	protected $clear_blanks = false; //clear all empty values
		
	 /**
	  * Turns strict mode (reject submission of contains invalid attribute names) on/off
	  * @param bool safe
	  */		
	public function strictMode($strict=true){
		$this->strict = $strict;
		return $this->strict;
	} 		

	 /**
	  * Tells Submit API to interpret empty string values as 'cleared'
	  * Use with care
	  * @param bool clear
	  */		
	public function clearBlanks($blanks=true){
		$this->clear_blanks = $blanks;
		return $this->clear_blanks;
	} 		 		
	 		 	 		
 	/**
 	 * Overrides parent class
 	 * @return array
 	 */	
 	public function getPostVars(){
 		$postVars = array("values");
 		if ($this->strict){
 			$postVars[] = "strict";	
 		}
 		if ($this->clear_blanks){
 			$postVars[] = "clear_blanks";	
 		} 		
 		return array_merge(parent::getPostVars(),$postVars);
 	}
 	
 	/**
 	 * Checks whether required params are included before writing
 	 * @return bool
 	 */
 	public function isValid(){
 		//all submits require a table name, a user token, and values
 		if (empty($this->tableName)| empty($this->user)|empty($this->values)){
 			return false;
 		} else {
 			return true;
 		}	
 	} 	
 	
 	/**
 	 * Adds array of key/value pairs to object
 	 * @param array data key/value pairs to add/update
 	 * @return array set values
 	 */
 	public function setValues($data){
 		if (!is_array($data)){
 			throw new exception (__METHOD__." Parameter must be assoc. array: key = attribute name, value = attribute value");
 		}
 		foreach ($data as $key => $value){
 			$this->values[$key] = $value;	
 		}
 		return $this->values;
 	}
 }
?>
