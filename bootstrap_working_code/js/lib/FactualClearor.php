<?php
/**
 * Factual Submittor vars collector for clearing values
 * @author Tyler
 * @package Factual
 * @license Apache 2.0
 */
 require_once("FactualPost.php");
 class FactualClearor extends FactualPost{
	//Required Params	
	protected $fields = array();	//values to be cleared	
	 		
 	/**
 	 * Checks whether required params are included before writing
 	 * @return bool
 	 */
 	public function isValid(){
 		//all submits require a table name, a user token, and Factual ID
 		if (empty($this->tableName)| empty($this->user)|empty($this->factualID)){
			return false;
 		} 
 		return true;
 	}

 	/**
 	 * Clears attribute of Factual entity
 	 * @param string key Field/Column name
 	 * @return array cleared values
 	 */
 	public function ClearValue($key){
 		$this->fields[] = $key;
 		return $this->fields;
 	} 	
 	
 	/**
 	 * Clears numerous attributes of a Factual entity
 	 * @param array data array of attribute names to clear
 	 * @return array set values
 	 */
 	public function clearValues($data){
 		if (!is_array($data)){
 			throw new exception (__METHOD__." Parameter must be array of attribute names to clear");
 		}
 		foreach ($data as $value){
 			$this->fields[] = $value;	
 		}
 		return $this->fields;
 	} 	 	
 	
 	/**
 	 * Overrides parent class
 	 * @return array
 	 */	
 	public function getPostVars(){
 		$postVars = array("fields");		
 		return array_merge(parent::getPostVars(),$postVars);
 		//$temp = array_merge(parent::getPostVars(),$postVars);
 		//print_r($temp);
 		//exit;
 	}	
 }
?>
