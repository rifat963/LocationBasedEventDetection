<?php

/**
 * Represents the results of a Resolve Query
 * @author Tyler
 * @package Factual
 * @license Apache 2.0
 */
class ResolveResponse extends FactualResponse {

	protected $data = false; //contents of entire data response
	protected $resolved = false; //only resolved entity

	protected function parseJSON($json){
		$rootJSON = parent::parseJSON($json);
		//Check against rowcount and resolve flag
		if ($rootJSON['response']['included_rows'] == 1 || $rootJSON['response']['included_rows'] > 1){
			if ($rootJSON['response']['data'][0]['resolved']){
				$this->resolved = $rootJSON['response']['data'][0];
				unset($this->resolved['resolved']); //remove old flag
			}
		}
		$this->data = $rootJSON['response']['data'];
    	return $rootJSON;	
	}

	/**
	 * Checks whether query was resolved
	 * @return bool
	 */
	public function isResolved() {
		return (bool)$this->resolved;
	}

	/**
	 * Gets resolved entity as array
	 * @return array | false on no resolution
	 */
	public function getResolved() {
		return $this->resolved;
	}

	/**
	 * Gets complete data array
	 * @return array
	 */
	public function getData() {
		return $this->data;
	}


}
?>