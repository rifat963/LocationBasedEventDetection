<?php

/**
 * Identical to a FactualResponse but contains additional methods/properties for working with returned data
 * @author Tyler
 * @package Factual
 * @license Apache 2.0
 */
class ReadResponse extends FactualResponse {
	
  protected $totalRowCount = null; //int
  protected $includedRows = null; //int
  
	/**
	 * Parses JSON as array and assigns object values
	 * @param string json JSON returned from API
	 * @return array structured JSON
	 */
	protected function parseJSON($json){
		$rootJSON = parent::parseJSON($json);
    	//assign total row count
    	if(isset($rootJSON['response']['total_row_count'])){
    		$this->totalRowCount = $rootJSON['response']['total_row_count'];
    	}
    	if(isset($rootJSON['response']['included_rows'])){
    		$this->includedRows = $rootJSON['response']['included_rows'];
    	}	
    	//assign data
    	$this->assignData($rootJSON['response']['data']);
    	
    	return $rootJSON;	
	}

	/**
	 * Assigns data element to object
	 * @param array data The data array from API response
	 */
	protected function assignData($data){
		if ($data){
		//assign data to iterator
    		foreach ($data as $index => $datum){
    			$this[$index] = $datum;
    		}
    	}
	}

  /**
   * Get the returned entities as an array 
   * @return array
   */
  public function getData() {
    return $this->getArrayCopy();
  }

  /**
   * Get the return entities as JSON 
   * @return the main data returned by Factual.
   */
  public function getDataAsJSON() {
    	return json_encode($this->getArrayCopy());
  }

	/**
   * Get total result count. Must be specifically requested via Query::includeRowCount()
   * @return int | null
   */
  public function getTotalRowCount() {
    return $this->totalRowCount;
  }

  /**
	* Alias of getTotalRowCount()
   * @return int | null
   */
  public function getRowCount() {
    return $this->getTotalRowCount();
  }

  /**
   * Get count of result rows returned in this response.
   * @return int 
   */
  public function getIncludedRowCount() {
  	if (!$this->includedRows){
  		$this->includedRows = count($this);
  	}
    return $this->includedRows;
  }
	

}
?>