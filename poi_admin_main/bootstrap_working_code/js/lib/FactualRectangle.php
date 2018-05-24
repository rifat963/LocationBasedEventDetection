<?php

/**
 * Represents a geographic sub query confining results to a rectangle.
 * @author Tyler
 * @package Factual
 * @license Apache 2.0
 */
class FactualRectangle {
	private $latTL;
	private $lonTL;
	private $latBR;
	private $lonBR;

	/**
	 * Constructs a geographic Circle representation. Points are always ordered as [latitude, longitude]:
	 * @param real latTL the latitude of the top-left corner
	 * @param real lonTL the longitude of the top-left corner
	 * @param real latBR the latitude of the bottom-right corner
	 * @param real lonBR the longitude of the bottom-right corner
	 */
	public function __construct($latTL, $lonTL, $latBR, $lonBR) {
		if (!is_numeric($latTL) || !is_numeric($lonTL) || !is_numeric($latBR) || !is_numeric($lonBR)){
			throw new Exception("Cannot create FactualRectangle: lat/lon parameters are not numeric");
			return false;
		}
		$this->latTL = $latTL;
		$this->lonTL = $lonTL;
		$this->latBR = $latBR;
		$this->lonBR = $lonBR;		
	}

	/**
	 * Returns JSON component of query
	 * @return string 
	 */
	public function toJsonStr() {
		return "{\"\$rect\":[[" . $this->latTL . "," . $this->lonTL . "],[" . $this->latBR . "," . $this->lonBR . "]]}";
	}

}
?>
