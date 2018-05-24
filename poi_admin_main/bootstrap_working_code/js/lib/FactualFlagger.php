<?php
/**
 * Factual Flag vars collector
 * @author Tyler
 * @package Factual
 * @license Apache 2.0
 */
 require_once("FactualPost.php");
 class FactualFlagger extends FactualPost{
	//Required Params
	public $problem = null;   //duplicate|nonexistent|inaccurate|inappropriate|spam|other
	//Optional Params
	public $debug = null;     //bool flag telling service to only test the flagging process
 	
 	//Getters


 	//Setters	
 	public function debug(){
 		$this->debug = true;
 	} 	  	  	
 	public function setProblem($var){
 		$var = strtolower($var);
 		$validProblems = array(
		"duplicate",
		"nonexistent",
		"inaccurate",
		"inappropriate",
		"spam",
		"other");
 		if (in_array($var,$validProblems)){
 			$this->problem = $var;	
 			return true;
 		} else {
 			throw new Exception("Problem must be one of: ".implode("|",$validProblems));
 			return false;
 		}
 	}  	  	
 	
 	protected function getPostVars(){
 		return array_merge(parent::getPostVars(),array("problem","comment","debug"));
 	}	 	

 	/**
 	 * Checks whether required params are included
 	 */
 	public function isValid(){
 		if (empty($this->tableName)| empty($this->factualID)|empty($this->user)|empty($this->problem)){
 			return false;
 		} else {
 			return true;
 		}
 		
 	}
 	
 }
 
 
?>
