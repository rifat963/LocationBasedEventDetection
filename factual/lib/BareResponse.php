<?php
/**
 * Header-only response from Factual API
 * @author Tyler
 * @package Factual
 * @license Apache 2.0
 */
class BareResponse {

  protected $version = null; //string
  protected $status = null; //string
  protected $tableName = null; //table getting queried
  protected $responseHeaders = array();
  protected $responseCode = null;
  protected $request = null;

  /**
   * Constructor, parses return values from CURL in factual::request() 
   * @param array response The JSON response String returned by Factual.
   */
  public function __construct($apiResponse) {
    try {
    	$this->parseResponse($apiResponse);
    } catch (Exception $e) {
    	//add note about json encoding borking here
      throw $e ();
    }
  }

	/**
	 * Parses the entire response from cURL, incl metadata
	 * @param array apiResponse response from curl
	 * @return void
	 */
	protected function parseResponse($apiResponse){
		if (isset($apiResponse['request'])){$this->request = $apiResponse['request'];}		
		if (isset($apiResponse['tablename'])){$this->tableName = $apiResponse['tablename'];}
		$this->responseHeaders = $apiResponse['headers'];
		$this->responseCode = $apiResponse['code'];
	}

	/**
	 * Parses the server response from the API
	 * @param string json JSON returned from API
	 * @return array structured JSON
	 */
	protected function parseJSON($json){
    	$rootJSON = json_decode($json,true);
    	//assign status value
    	$this->status = $rootJSON['status'];
    	//assign version
    	$this->version = $rootJSON['version'];
    	return $rootJSON;	
	}

	/**
	 * Get response headers sent by Factual
	 * @return array
	 */
	public function getResponseHeaders(){
		return $this->responseHeaders;
	}

	/**
	 * Get HTTP response code
	 * @return int
	 */
	public function getResponseCode(){
		return $this->responseCode;
	}

	/**
	 * Gets table name call was made against
	 * @return string
	 */
	protected function getTableName(){
		return $this->tableName;
	}

	/**
	 * Test for success (n 200 status return)
	 */
	 public function success(){
	 	if ($this->status = 200){
	 		return true;
	 	} else {
	 		return false;
	 	}
	 }

  /**
   * Get the status returned by the Factual API server, e.g. "ok".
   * @return string
   */
  public function getStatus() {
    return $this->status;
  }

  /**
   * Get the version returned by the Factual API server, e.g. "3".
   * @return numeric
   */
  public function getVersion() {
    return $this->version;
  }

  /**
   * Get url-decoded request string, does not include auth.
   * @return string
   */
  public function getRequest(){
  	return urldecode($this->request);
  }

  /**
   * Get url-encoded request string, does not include auth.
   * @return string
   */
  public function getRawRequest(){
  	return $this->request;
  }
  
  /**
   * Get table name queried
   * @return string
   */
  public function getTable(){
  	return $this->tableName;
  }  
  
   /**
   * Get http headers returned by Factual
   * @return string
   */
  public function getHeaders(){
  	return $this->responseHeaders;
  }   
  
   /**
   * Get http status code returned by Factual
   * @return string
   */
  public function getCode(){
  	return $this->responseCode;
  }    

}
?>