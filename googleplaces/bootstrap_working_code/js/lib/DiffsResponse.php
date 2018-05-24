<?php


/**
 * The response from running a Diffs query
 * @author Tyler
 * @package Factual
 * @license Apache 2.0
 */
class DiffsResponse extends FactualResponse {

	protected $diffStart = null; //Start time. Unix timestamp in milliseconds
	protected $diffEnd = null; //Optional end time. Unix timestamp in milliseconds
	protected $duration = null; //Duration of Diffs window in microseconds
	protected $stats = array (); //summary statistics
	/**
	 * Gets Diffs start time in milliseconds
	 * @param bool human Human Readable (RFC 2822) rather than timestamp
	 * @return mixed diffs start time
	 */
	public function getStart($human = false) {
		if ($human) {
			return $this->timestampReadable($this->diffStart);
		}
		return $this->diffStart;
	}

	/**
	 * Gets Diffs end time in milliseconds
	 * @param bool human Human Readable (RFC 2822) rather than timestamp	 * 
	 * @return mixed diffs end time
	 */
	public function getEnd($human = false) {
		if ($human) {
			return $this->timestampReadable($this->diffEnd);
		}
		return $this->diffEnd;
	}

	protected function timestampReadable($timeStamp) {
		$timeStamp = $timeStamp / 1000;
		return date('r', $timeStamp);
	}

	/**
	 * Gets Diffs duration
	 * @param bool human Human Readable rather than milliseconds 
	 * @return mixed duration in milliseconds | H:m:s
	 */
	public function getDuration($human = false) {
		if ($human) {
			$mod = round($this->duration % 1000, 3);

			$seconds = $this->duration / 1000;
			$duration = $this->getHumanDuration($seconds);
			if ($mod) {
				$duration .= ", " . $mod . " ms";
			}
			return $duration;

		}
		return $this->duration;
	}

	/**
	 * Get all Diffs as a array
	 * @return array
	 */
	public function getDiffs() {
		return $this->getArrayCopy();
	}

	/**
	 * Parses response from CURL
	 * @param array apiResponse response from curl
	 * @return void
	 */
	protected function parseResponse($apiResponse) {
		parent :: parseResponse($apiResponse);
		//assign metadata specific to this call
		$this->diffStart = $apiResponse['diffsmeta']['start'];
		$this->diffEnd = $apiResponse['diffsmeta']['end'];
		$this->duration = $apiResponse['diffsmeta']['end'] - $apiResponse['diffsmeta']['start'];
	}

	/**
	 * Parses the server response from the API
	 * @param string json JSON returned from API
	 * @return void
	 */
	protected function parseJSON($json) {
		$allDiffs = explode("\n", trim($json)); //diffs reponses are discreet JSON, linebreak delimited
		foreach ($allDiffs as $diff) {
			$this[] = json_decode($diff, true);
		}
	}

	public function getStats() {
		if (empty ($this->stats)) {
			$this->generateStats();
		}
		return $this->stats;
	}

	/**
	 * Creates summary of all diffs
	 * @return void
	 */
	protected function generateStats() {

		$this->stats = array (
			'insert' => 0,
			'update' => 0,
			'delete' => 0,
			'deprecate' => 0,
			'total' => 0,
			'duration' => null
		);

		if (count($this) > 0){
			foreach ($this as $diff) {
				$this->stats[$diff['type']]++;
			}
		}	
		$this->stats['total'] = count($this);
		$this->stats['duration'] = $this->getDuration(true);
		$this->stats['start'] = $this->getStart(true);
		$this->stats['end'] = $this->getEnd(true);
	}

	/**
	 * Convert window duration to human readable format
	 * @internal From http://aidanlister.com/2004/04/making-time-periods-readable/
	 */
	protected function getHumanDuration($seconds) {
		// Define time periods
		$periods = array (
			'days' => 86400,
			'hours' => 3600,
			'minutes' => 60,
			'seconds' => 1
		);

		// Break into periods
		$seconds = (float) $seconds;
		$segments = array ();
		foreach ($periods as $period => $value) {
			$count = floor($seconds / $value);
			if ($count == 0) {
				continue;
			}
			$segments[strtolower($period)] = $count;
			$seconds = $seconds % $value;
		}
		// Build the string
		$string = array ();
		foreach ($segments as $key => $value) {
			$segment_name = substr($key, 0, -1);
			$segment = $value . ' ' . $segment_name;
			if ($value != 1) {
				$segment .= 's';
			}
			$string[] = $segment;
		}

		return implode(', ', $string);
	}

}
?>