<?php

/**
 * Represents the response from running a fetch request against Factual, such as
 * a geolocation based query for specific places entities.
 * This is a refactoring of the Factual Driver by Aaron: https://github.com/Factual/factual-java-driver
 * @author Tyler
 * @package Factual
 * @license Apache 2.0
 */
class MatchResponse extends FactualResponse {

	protected $factualID = false;
	
	protected function parseJSON($json){
		$rootJSON = parent::parseJSON($json);
		//set factual ID
		if ($rootJSON['response']['included_rows'] == 1){
			$this->factualID = $rootJSON['response']['data'][0]['factual_id'];
			
		}
    	return $rootJSON;	
	}

	/**
	 * Checks whether query was resolved
	 * @return bool
	 */
	public function isMatched() {
		return (bool) $this->factualID;
	}

	/**
	 * Gets resolved entity as array
	 * @return mxed Factual ID | false on no match
	 */
	public function getMatched() {
		return $this->factualID;
	}
	
}
?>