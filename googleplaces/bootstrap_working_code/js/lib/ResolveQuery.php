<?php
/**
 * Represents a Factual Resolve query.
 * This is a refactoring of the Factual Driver by Aaron: https://github.com/Factual/factual-java-driver
 * @author Tyler
 */
class ResolveQuery extends FactualQuery {
  const RESPONSETYPE = "ResolveResponse";
  protected $debug = false;  
  protected $values = array();
  
  	/**
	 * Whether this lib must perform URL encoding.
	 * Set to avoid double or absent encoding
	 */
	const URLENCODE = true;

	/**
	 * Adds name/key pair to query for eventual resolution
	 * @param string key Attribute name
	 * @param mixed val Attribute value
	 * $return object This query object 
	 */
  public function add($key, $val) {
  	$this->values[$key]=$val;
    return $this;
  }

	/**
	 * Adds factual entity to query for resolution.
	 * Use for refreshing chached entityies that do not redirect
	 * @param array entity Factual entity with Factual attribute names as key
	 * $return object This query object 
	 */  
  public function addEntity($entity){
  	foreach ($entity as $key => $val){
		  	$this->values[$key]=$val; 		
  	}
  	return $this; 
  }
  
	/**
	 * Turns on debugging and multiple results
	 */
	public function debug() {
		$this->debug = true;
	}

	/**
	 * @return string
	 */
  public function toUrlQuery() {
  	if ($this->debug){
    	return $this->urlPair("values", $this->toJsonStr($this->values))."&debug=true";
  	} else {
    	return $this->urlPair("values", $this->toJsonStr($this->values));  		
  	}
  }

	/**
	 * @return string
	 */
  protected function toJsonStr($var) {
    try {
      return json_encode($this->values);
    } catch (Exception $e) {
      throw new Exception($e);
    } 
  }

	protected function urlPair($name, $val) {
		if ($val != null) {
			try {		
				if (self::URLENCODE){	
					return $name."=".urlencode($val);
				} else {
					return $name."=".$val;
				}
			} catch (Exception $e) {
				throw $e;
			}
		} else {
			return null;
		}
	}

}

?>
