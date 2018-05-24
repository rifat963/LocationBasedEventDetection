<?php
/**
 * Represents a Factual Diffs query.
 * 
 * @author Tyler
 *
 */
 
class DiffsQuery {

	protected $diffStart = null; // start time. Unix timestamp in milliseconds
	protected $diffEnd = null; // Optional end time. Unix timestamp in milliseconds
	const RESPONSETYPE = "DiffsResponse";

	/**
	 * Gets Diffs start time in milliseconds
	 * @return int diffs start time
	 */
	public function getStart(){
		return $this->diffStart;
	}

	/**
	 * Gets Diffs end time in milliseconds
	 * @return int diffs end time
	 */
	public function getEnd(){
		return $this->diffEnd;
	}

	/**
	 * The before time to create this diff against.
	 * @param timestamp Unix timestamp in milliseconds
	 * @return object This DiffsQuery
	 */
	public function setStart($timestamp) {
		if (!is_numeric($timestamp)){
			throw new Exception("Parameter must be millisecond timestamp");
		}
		$this->diffStart = $timestamp;
		return $this;
	}

	/**
	 * The after time to create this diff against.
	 * @param timestamp Unix timestamp in milliseconds
	 * @return object This DiffsQuery
	 */
	public function setEnd($timestamp) {
		if (!is_numeric($timestamp)){
			throw new Exception("Parameter must be millisecond timestamp");
		}
		$this->diffEnd = $timestamp;
		return $this;
	}
	
	public function toUrlQuery() {
		//assign implied end time
		if (empty($this->diffEnd)){
 			$this->diffEnd = $this->getTimestamp();
 		}
		return "start-date=".$this->diffStart."&end-date=".$this->diffEnd;
	}
	
 	/**
 	 * Validate Parameters
 	 * @return Bool
 	 */
 	public function isValid(){
 		//use implied end time to validate, but do not set the time itself
 		if (empty($this->diffEnd)){
 			$end = $this->getTimestamp();
 		} else {
 			$end = $this->diffEnd;
 		}
 		if ( empty($this->diffStart)| $this->diffStart >= $end ){
 			return false;
 		} 
 		return true;
 	}
 	
 	protected function getTimestamp(){
 		return floor(microtime(true)*1000);
 	}
 	
}
?>
