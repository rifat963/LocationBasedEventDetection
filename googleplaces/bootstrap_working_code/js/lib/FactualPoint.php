<?php

/**
 * 
 * Represents a geographical point.
 * @author tyler[at]factual.com
 *
 */
class FactualPoint {
	private $lat;
	private $lon;

	/**
	 * Constructs a geographic Circle representation.
	 * @param real lat the latitude of the point
	 * @param real lon the longitude of the point
	 */
	public function __construct($lat, $lon) {
		if (!is_numeric($lat) || !is_numeric($lon)){
			throw new Exception("Cannot create FactualPoint: bad lat/lon parameters: lat='".$lat."',lon='".$lon."'");
			return false;
		}
		$this->lat = $lat;
		$this->lon = $lon;
	}

	/**
	 * Returns JSON component of point-radius query
	 * @return string 
	 */
	public function toJsonStr() {
		return "{\"\$point\":[" . $this->lat . "," . $this->lon . "]}";
	}
	
}
?>