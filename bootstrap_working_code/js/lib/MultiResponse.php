<?php

/**
 * Response object from a multi-request. Contains array of result objects.
 * @author Tyler
 * @package Factual
 * @license Apache 2.0
 */
class MultiResponse extends FactualResponse {
  protected $JSONresponses = array(); //individual JSON responses
  protected $version = array(); //array of version reponses
  protected $status = array();  //array of status responses
  protected $responseTypes = array(); //array of repsonse types of objects

  /**
   * Constructor, parses return values from CURL in factual::request() 
   * @param array response The JSON response String returned by Factual.
   */
  public function __construct($apiResponse,$responseTypes) {
    $this->responseTypes = $responseTypes; //pass response types from query. Do this before parent
  	parent::__construct($apiResponse);
  }

	/**
	 * Parses JSON as array and assigns object values
	 * @internal Cannot use parent for this call; status/version different
	 * @param string json JSON returned from API
	 * @return array structured JSON
	 */
	protected function parseJSON($rootJSON){
		$rootJSON = json_decode($rootJSON,true);
		foreach ($rootJSON as $idx => $response){
			$this->JSONresponses[$idx] = json_encode($response); //get separate reponse component		
			//assign status and version as array (cannot use parent method for these)
    		$this->version[$idx] = $response['version'];	
    		$this->status[$idx] = $response['status'];
    		//assign JSON and other attributes to emulate proper return
			$singleResponse['body'] = $this->JSONresponses[$idx];
			$singleResponse['headers'] = $this->responseHeaders;
			$singleResponse['code'] = $this->responseCode;	
			//add response object to iterator
			$this[$idx]	= new $this->responseTypes[$idx]($singleResponse); 	
		}
    	return $rootJSON;	
	}

	/**
	 * Gets type of response objects
	 * @return array
	 */
	public function getResponseTypes(){
		return $this->responseTypes;
	}

	/**
	 * Gets response objects
	 * @return array
	 */	
	public function getResponses(){
		$this->getArrayCopy();
	}

}
?>