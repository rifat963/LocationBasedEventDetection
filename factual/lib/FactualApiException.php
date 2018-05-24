<?php

/**
 * Represents an Exception that happened while communicating with Factual.
 * Includes information about the request that triggered the problem.
 * This is a refactoring of the Factual Driver by Aaron: https://github.com/Factual/factual-java-driver
 * @author Tyler
 * @package Factual
 * @license Apache 2.0
 */
class FactualApiException extends Exception {
	protected $info; //debug array
	protected $helpUrl = "https://github.com/Factual/factual-php-driver/wiki/Debugging-and-Support";

	public function getError(){
		return $this->info['error_type'];
	}

	public function __construct($info) {
		$this->info = $info;
		if (isset($info['message'])){
			$this->message = $info['message'];
			$this->code = $info['code'];
		} else {
			$this->message = "Unknown error; no message returned from server";
		}
	}

	public function debug(){
		return $this->info;
	}
}
?>