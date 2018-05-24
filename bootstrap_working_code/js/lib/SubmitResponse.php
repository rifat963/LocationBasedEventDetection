<?php

/**
 * The response from running a Submit query
 * @author Tyler
 * @package Factual
 * @license Apache 2.0
 */
class SubmitResponse extends FactualResponse {

	protected $newEntity = false;
	protected $factualID = null;
	protected $commitID = null;

	/**
	 * Parses JSON as array and assigns object values
	 * @param string json JSON returned from API
	 * @return array structured JSON
	 */
	protected function parseJSON($json){
		$rootJSON = parent::parseJSON($json);
		$this->commitID = $rootJSON['response']['commit_id'];
		if (isset($this->newEntity)){$this->newEntity = (bool)$rootJSON['response']['new_entity'];}
		if (isset($rootJSON['response']['factual_id'])){
			$this->factualID = $rootJSON['response']['factual_id'];
		}
		return $rootJSON;
	}

	/**
	 * Is this a new entity (inserted) or extant entity (updated)
	 * @return bool
	 */
	public function isNew(){
		return $this->newEntity;
	}	
	
	/**
	 * Get the Factual ID (extant entity only)
	 * @return string | null on no ID
	 */
 	public function getFactualID(){
 		return $this->factualID;
 	} 

	/**
	 * Get the Commit ID (transaction identifier)
	 * @return string | null on no ID
	 */
 	public function getCommitID(){
 		return $this->commitID;
 	} 

}
?>