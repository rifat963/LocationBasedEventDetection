<?php

/*  
Plugin Name: Factual
Description: Factual API Wordpress Plugin
*/
/**
 * Requires PHP5, php5-curl, SPL (for autoloading)
 */

//Oauth libs (from http://code.google.com/p/oauth-php/)
require_once ('oauth-php/library/OAuthStore.php');
require_once ('oauth-php/library/OAuthRequester.php');

/**
 * Represents the public Factual API. Supports running queries against Factual
 * and inspecting the response. Supports the same levels of authentication
 * supported by Factual's API.
 * This is a refactoring of the Factual Driver by Aaron: https://github.com/Factual/factual-java-driver
 * @author Tyler
 * @package Factual
 * @license Apache 2.0
 */
class Factual {

	protected $factHome; //string assigned from config
	protected $signer; //OAuthStore object
	protected $config; //array from config.ini file on construct
	protected $geocoder; //geocoder object (unsupported, experimental)
	protected $configPath = "config.ini"; //where the config file is found: path + file
	protected $lastTable = null; //last table queried
	protected $fetchQueue = array (); //array of queries teed up for multi
	protected $debug = false; //debug flag
	protected $curlTimeout = 0; //maximum number of seconds for the network function to execute (0 = no timeout)
	protected $connectTimeout = 0; //maximum number of seconds to connect to the server (0 = no timeout)

	/**
	 * Constructor. Creates authenticated access to Factual.
	 * @param string key your oauth key.
	 * @param string secret your oauth secret.
	 */
	public function __construct($key, $secret) {
		//load configuration
		$this->loadConfig();
		$this->factHome = $this->config['factual']['endpoint']; //assign endpoint
		//create authentication object
		$options = array (
			'consumer_key' => $key,
			'consumer_secret' => $secret
		);
		$this->signer = OAuthStore :: instance("2Leg", $options);
		//register autoloader
		spl_autoload_register(array (
			get_class(),
			'factualAutoload'
		));
	}

	/**
	 * Sets location of config file at runtime
	 * @param string path path+filename
	 * @return void
	 */
	protected function setConfigPath($path) {
		$this->configPath = $path;
	}

	/**
	 * Loads config file from ini
	 * @return void
	 */
	protected function loadConfig() {
		if (!$this->config) {
			try {
				$this->config = parse_ini_file($this->configPath, true);
			} catch (Exception $e) {
				throw new Exception("Failed parsing config file");
			}
		}
	}

	/**
	 * Turns on debugging for output to stderr
	 */
	public function debug() {
		$this->debug = true;
	}

	/**
	 * Change the base URL at which to contact Factual's API. This
	 * may be useful if you want to talk to a test or staging
	 * server withou changing config
	 * Example value: <tt>http://staging.api.v3.factual.com/t/</tt>
	 * @param urlBase the base URL at which to contact Factual's API.
	 * @return void
	 */
	public function setFactHome($urlBase) {
		$this->factHome = $urlBase;
	}

	/**
	 * Factual Fetch Abstraction
	 * @param string tableName The name of the table you wish to query (e.g., "places")
	 * @param obj query The query to run against <tt>table</tt>.
	 * @return object ReadResponse object with result of running <tt>query</tt> against Factual.
	 */
	public function fetch($tableName, $query) {
		$this->lastTable = $tableName; //assign table name to object for logging
		switch (get_class($query)) {
			case "FactualQuery" :
				$res = new ReadResponse($this->request($this->urlForFetch($tableName, $query)));
				break;
			case "ResolveQuery" :
				$res = new ResolveResponse($this->request($this->urlForResolve($tableName, $query)));
				break;
			case "FacetQuery" :
				$res = new ReadResponse($this->request($this->urlForFacets($tableName, $query)));
				break;
			case "MatchQuery" :
				$res = new MatchResponse($this->request($this->urlForMatch($tableName, $query)));
				break;				
			case "FacetQuery" :
				$res = new ReadResponse($this->request($this->urlForFacets($tableName, $query)));
				break;
			case "DiffsQuery" :
				//validate parameters
				if (!$query->isValid()){
					if ($query->getStartTime()){
						throw new Exception("Start time (".$query->getStartTime().") must be earlier than end time (".$query->getEndTime().")");						
					} else {
						throw new Exception("Query must have start time set");
					}
					return false;					
				}
				//add metadata about this call to the response object
				$apiResponse = $this->request($this->urlForDiffs($tableName, $query));
				$apiResponse['diffsmeta']['start'] = $query->getStart();
				$apiResponse['diffsmeta']['end'] = $query->getEnd();
				$res = new DiffsResponse($apiResponse);
				break;
			default :
				throw new Exception(__METHOD__ . " class type '" . get_class($query) . "' not recognized");
				$res = false;
		}
		
		return $res;
	}

	/**
	* Build query string without running fetch
	* @param string tableName The name of the table you wish to query (e.g., "places")
	* @param obj query The query to run against <tt>table</tt>.
	* @return string
	*/
	public function buildQuery($tableName, $query) {
		switch (get_class($query)) {
			case "FactualQuery" :
				$res = $this->urlForFetch($tableName, $query);
				break;
			case "ResolveQuery" :
				$res = $this->urlForResolve($tableName, $query);
				break;
			case "MatchQuery" :
				$res = $this->urlForMatch($tableName, $query);
				break;
			case "FacetQuery" :
				$res = $this->urlForFacets($tableName, $query);
				break;
			case "DiffsQuery" :
				$res = $this->urlForDiffs($tableName, $query);
				break;
			default :
				throw new Exception(__METHOD__ . " class type '" . get_class($query) . "' not recognized");
				$res = false;
		}
		return $res;
	}
	
	/**
	 * Resolves and returns resolved entity or null (shortcut method)
	 * @param string tableName Table name
	 * @param array vars Attributes of entity to be matched in key=>value pairs
	 * @return object ResolveResponse
	 */
	public function resolve($tableName, $vars) {
		$query = new ResolveQuery();
		foreach ($vars as $key => $value) {
			$query->add($key, $value);
		}
		$res = new ResolveResponse($this->request($this->urlForResolve($tableName, $query)));
		return $res;
	}


	/**
   * Runs a read <tt>query</tt> against the specified Factual table.
   * 
   * @param string $tableName
   *          the name of the table you wish to query (e.g., "places")
   * @param factualId
   *          the factual id
   * @param query
   *          the read query to run against <tt>table</tt>.
   * @return the response of running <tt>query</tt> against Factual.
   */
  public function fetchRow($tableName, $factualID) {
    return new ReadResponse($this->request($this->urlForFetchRow($tableName, $factualID)));
  }	

	/**
	 * Matches entity to Factual ID (shortcut method)
	 * @param string tableName Table name
	 * @param array vars Attributes of entity to be matched in key=>value pairs
	 * @return object MatchResponse 
	 */
	public function match($tableName, $vars) {
		$query = new MatchQuery();
		foreach ($vars as $key => $value) {
			$query->add($key, $value);
		}
		return new MatchResponse($this->request($this->urlForMatch($tableName, $query)));
	}

	/**
	 * Improves search results by associating query with selected record
	 * @param object factualBoost object
	 * @return object 
	 */
	public function boost($factualBoost) {
		//check parameter type
		if (!$factualBoost instanceof FactualBoost) {
			throw new Exception("FactualBoost object required as parameter of " . __METHOD__);
			return false;
		}
		return new BareResponse($this->request($this->urlForBoost($factualBoost->getTableName()),"POST",$factualBoost->toUrlParams()));
	}
	
	/**
	 * @return object SchemaResponse object
	 */
	public function schema($tableName) {
		return new SchemaResponse($this->request($this->urlForSchema($tableName)));
	}
	
	protected function urlForSchema($tableName) {
		return $this->factHome . "t/" . $tableName . "/schema";
	}

	protected function urlForResolve($tableName, $query) {
		return $this->factHome . $tableName . "/resolve?" . $query->toUrlQuery();
	}

	protected function urlForMatch($tableName, $query) {
		return $this->factHome . $tableName . "/match?" . $query->toUrlQuery();
	}

	protected function urlForFetch($tableName, $query) {
		if (!stripos("$tableName","/")){
			$tableName = "t/" . $tableName; //do not assume raw table if path is added
		}
		return $this->factHome . $tableName . "?" . $query->toUrlQuery();
	}

	protected function urlForFacets($tableName, $query) {		
		return $this->factHome . "t/" . $tableName . "/facets?" . $query->toUrlQuery();
	}

	protected function urlForMulti() {
		$homeLen = strlen($this->factHome) - 1;
		foreach ($this->fetchQueue as $index => $mQuery) {
			$call = rawurlencode(substr($this->buildQuery($mQuery['table'], $mQuery['query']), $homeLen));
			$queryStrings[] = "\"" . $index . "\":" . "\"" . $call . "\"";
			$res['response'][$index] = $mQuery['query']->getResponseType();
		}
		$res['url'] = $this->factHome . "multi?queries={" . implode(",", $queryStrings) . "}";
		return $res;
	}

	protected function urlForGeopulse($query) {
		return $this->factHome . "geopulse/context?" . $query->toUrlQuery();
	}

	protected function urlForGeocode($tableName, $query) {
		return $this->factHome . $tableName . "/geocode?" . $query->toUrlQuery();
	}

	protected function urlForFlag($tableName, $factualID) {
		return $this->factHome . "t/" . $tableName . "/" . $factualID . "/flag";
	}

	protected function urlForDiffs($tableName, $query) {
		return $this->factHome . "t/" . $tableName . "/diffs?" . $query->toUrlQuery();
	}

	protected function urlForSubmit($tableName, $factualID = null) {
		if ($factualID) {
			return $this->factHome . "t/" . $tableName . "/" . $factualID . "/submit";
		} else {
			return $this->factHome . "t/" . $tableName . "/submit";
		}
	}

	protected function urlForBoost($tableName) {
			return $this->factHome . "t/" . $tableName . "/boost";
	}
		
	protected function urlForClear($tableName, $factualID) {
			return $this->factHome . "t/" . $tableName . "/" . $factualID . "/clear";
	}	

	protected function urlForFetchRow($tableName, $factualID) {
		return $this->factHome . "t/" . $tableName . "/" . $factualID;
	}	
	
	/**
	   * Flags entties as problematic
	   * @param object FactualFlagger object
	   * @return object Flag Response object
	   */
	public function flag($flagger) {
		//check parameter type
		if (!$flagger instanceof FactualFlagger) {
			throw new Exception("FactualFlagger object required as parameter of " . __METHOD__);
			return false;
		}
		//check that flaggger has required attributes set
		if (!$flagger->isValid()) {
			throw new Exception("Parameter must have userToken, tableName, and factualID set");
			return false;
		}
		return new FlagResponse($this->request($this->urlForFlag($flagger->getTableName(), $flagger->getFactualID()), "POST", $flagger->toUrlParams()));
	}

	/**
	   * Submit data to Factual
	   * @param object FactualSubmittor object
	   * @return object Submit Response object
	   */
	public function submit($submittor) {
		//check parameter type
		if (!$submittor instanceof FactualSubmittor) {
			throw new Exception("FactualSubmittor object required as parameter of " . __METHOD__);
			return false;
		}
		//check that object has required attributes set
		if (!$submittor->isValid()) {
			throw new Exception("table name, values, and user token required to submit data"); //return string is error message
			return false;
		}
		return new SubmitResponse($this->request($this->urlForSubmit($submittor->getTableName(), $submittor->getFactualID()), "POST", $submittor->toUrlParams()));
	}

	/**
	   * Clear a/n attribute/s from a Factual entity
	   * @param object FactualClearor object
	   * @return object Submit Response object
	   */
	public function clear($clear) {
		//check parameter type
		if (!$clear instanceof FactualClearor) {
			throw new Exception("FactualClearor object required as parameter of " . __METHOD__);
			return false;
		}
		//check that object has required attributes set
		if (!$clear->isValid()) {
			throw new Exception("Factual ID is required"); //return string is error message
			return false;
		}
		return new SubmitResponse($this->request($this->urlForClear($clear->getTableName(), $clear->getFactualID()), "POST", $clear->toUrlParams()));
	}

	/**
	  * Reverse geocodes by returning a response containing the address nearest a given point.
	  * @param obj point The point for which the nearest address is returned
	  * @param string tableName Optional. The tablenae to geocode against.  Currently only 'places' is supported.
	  * @return the response of running a reverse geocode query for <tt>point</tt> against Factual.
	  */
	public function factualReverseGeocode($point, $tableName = "places") {
		$query = new FactualQuery;
		$query->at($point);
		return new ReadResponse($this->request($this->urlForGeocode($tableName, $query)));
	}

	/**
	 * Queue a request for inclusion in a multi request.
	 * @param string table The name of the table you wish to query (e.g., "places")
	 * @param obj query Query object to run against <tt>table</tt>.
	 * @param string handle Arbitrary name of this query used to distinguish return values
	 */
	public function multiQueue($table, $query, $handle) {
		if (isset($this->fetchQueue[$handle])){
			throw new Exception("Query with handle '".$handle."' already exists in queue. Handles must be unique.");
			return false;
		}
		$this->fetchQueue[$handle] = array (
			'query' => $query,
			'table' => $table
		);
		return $this->fetchQueue;
	}

	/**
	  * Use this to send all queued reads as a multi request
	  * @return response for a multi request
	  */
	public function multiFetch() {
		$res = $this->urlForMulti();
		return new MultiResponse($this->request($res['url']), $res['response']);
	}

	/**
	 * Runs a geopulse <tt>query</tt>
	 * @param query The FactualQuery object
	 * @return object The ReadResponse object
	 */
	public function geopulse($query) {
		return new ReadResponse($this->request($this->urlForGeopulse($query)));
	}

/**
  * Runs a GET request against the specified endpoint path, using the given
  * parameters and your OAuth credentials. Returns the raw response body
  * returned by Factual. The necessary URL base will be automatically prepended to <tt>path</tt>. If 
  * you need to change it, e.g. to make requests against a development instance of
  * the Factual service, use Factual::setFactHome().
  * @param string path The endpoint path to run the request against. example: "t/places"
  * @param array params The query string parameters to send with the request. Escape, but do not encode, the values. 
  * @return string JSON response body from the Factual API.
  */
	public function rawGet($path,$params) {
		$queryString = $this->toQueryString($params);
		$urlStr = $this->factHome.$path.$queryString;	
		$res = $this->request($urlStr);
		return $res['body'];
	}

/**
  * Runs a GET request against the specified endpoint path, using the given
  * parameters and your OAuth credentials. Returns the raw response body
  * returned by Factual. The necessary URL base will be automatically prepended to <tt>path</tt>. If 
  * you need to change it, e.g. to make requests against a development instance of
  * the Factual service, use Factual::setFactHome().
  * @param string path The endpoint path to run the request against. example: "t/places"
  * @param array body key/value pairs of POST body. Escape, but do not encode.
  * @param array params Optional Key/Value query string parameters to send with the request. Escape, but do not encode.
  * @return string JSON response body from the Factual API.
  */
	public function rawPost($path,$body,$params=null) {
		$queryString = $this->toQueryString($params);
		$urlStr = $this->factHome.$path.$queryString;	
		$res = $this->request($urlStr,"POST",$body);
		return $res['body'];
	}


    /**
     * Sign the request, perform a curl request and return the results
     *
     * @param string $urlStr unsigned URL request
     * @param string $requestMethod
     * @param null $params
     * @param array $curlOptions
     * @return array ex: array ('code'=>int, 'headers'=>array(), 'body'=>string)
     * @throws FactualApiException
     */
    protected function request($urlStr, $requestMethod="GET", $params = null, $curlOptions = array()) {
		//custom headers
		$curlOptions[CURLOPT_HTTPHEADER] = array ();
		$curlOptions[CURLOPT_HTTPHEADER][] = "X-Factual-Lib: " . $this->config['factual']['driverversion'];
		if ($requestMethod == "POST") {
			$curlOptions[CURLOPT_HTTPHEADER][] = "Content-Type: " . "application/x-www-form-urlencoded";
		}
		//other curl options
		$curlOptions[CURLOPT_CONNECTTIMEOUT] = $this->connectTimeout; //connection timeout
		$curlOptions[CURLOPT_TIMEOUT] = $this->curlTimeout; //execution timeout
		// Build request with OAuth request params
		$request = new OAuthRequester($urlStr, $requestMethod, $params);
		//check & flag debug
		if ($this->debug) {
			$request->debug = true; //set debug on oauth request object for curl output
		}
		//Make request
		try {
			$callStart = microtime(true);
			$result = $request->doRequest(0, $curlOptions);
			$callEnd = microtime(true);
		} catch (Exception $e) {
			//catch client exception
			$info['request']['encoded'] = $urlStr;
			$info['request']['unencoded'] = urldecode($urlStr);
			$info['driver'] = $this->config['factual']['driverversion'];
			$info['method'] = $requestMethod;
			$info['message'] = "Service exception (likely a problem on the server side). Client did not connect and returned '" . $e->getMessage() . "'";
			$factualE = new FactualApiException($info);
			throw $factualE;
		}
		$result['request'] = $urlStr; //pass request string onto response
		$result['tablename'] = $this->lastTable; //pass table name to result object (not available with rawGet())
		//catch server exception & load up on debug data
		if ($result['code'] >= 400 | $this->debug) {
			$body = json_decode($result['body'], true);
			//get a boatload of debug data
			$info['code'] = $result['code'];
			$info['version'] = $body['version'];
			$info['status'] = $body['status'];
			$info['returnheaders'] = $result['headers'];
			$info['driver'] = $this->config['factual']['driverversion'];
			$info['method'] = $requestMethod;
			if (isset($body['error_type'])){$info['error_type'] = $body['error_type'];}
			if (isset($body['message'])){$info['message'] = $body['message'];}
			if (isset($result['request'])){
				$info['request']['encoded'] = $result['request'];
				$info['request']['unencoded'] = urldecode($result['request']);
			}
			if (isset($result['tablename'])){$info['tablename'] = $result['tablename'];}			
			//add post body to debug
			if ($params) {
				$info['body'] = $params;
			}
			//add execution time
			$info['time'] = $callEnd - $callStart;
			//write debug info to stderr if debug mode on
			if ($this->debug) {
				//add only select curl debug information
				unset($request->curlInfo['url']);
				unset($request->curlInfo['content_type']);
				unset($request->curlInfo['certinfo']);
				unset($request->curlInfo['redirect_time']);
				unset($request->curlInfo['redirect_time']);
				unset($request->curlInfo['filetime']);
				unset($request->curlInfo['ssl_verify_result']);
				$info['curl'] = $request->curlInfo; 

				$info = array_filter($info); //remove empty elements for readability
				file_put_contents('php://stderr', "Debug " . print_r($info, true));
			}			
			//chuck exception
			if ($result['code'] >= 400){
				$factualE = new FactualApiException($info);
				throw $factualE;
			}
		}
		//check for deprecation, add to stdout
		if ($result['code'] == 301){
			file_put_contents('php://stderr', "Entity is deprecated");
		}
		return $result;
	}

	/**
	 * Converts and encodes parameter array to a query string
	 * @return string
	 */
	protected function toQueryString($parameters){
		if (count($parameters) > 0){
			foreach ($parameters as $key => $value){
				if (is_bool($value)){ //convert bool to string
					$value = var_export($value, true);
				}
				$temp[] = $key."=".rawurlencode($value);	
			}
			return "?".implode("&", $temp);
		} else {
			return "";
		}
	}

	/**
	 * Gets driver version
	 * @return string
	 */
	public function version() {
		return $this->config['factual']['driverversion'];
	}

	/**
	 * Autoloader for file dependencies
	 * Called by spl_autoload_register() to avoid conflicts with autoload() methods from other libs
	 */
	public static function factualAutoload($className) {
		$filename = dirname(__FILE__) . "/" . $className . ".php";
		// don't interfere with other classloaders
		if (!file_exists($filename)) {
			return;
		}
		include $filename;
	}

	/**
	 * Sets maximum number of seconds to connect to the server before bailing
	 * @param int secs timeout in seconds
	 */
	public function setConnectTimeout($secs){
		$this->connectTimeout = $secs;
		return $this;
	}

	/**
	 * Sets maximum number of seconds to the network function to execute
	 * @param int secs timeout in seconds
	 */
	public function setCurlTimeout($secs){
		$this->curlTimeout = $secs;
		return $this;
	}

	//The following methods are included as handy convenience; unsupported and experimental
	//They rely on a loosely-coupled third-party service that can be easily swapped out
	// BIG NOTE: these shim the Yahoo Geocoder, which has changed its TOS since implementation

	/**
	* Geocodes address string or placename
	* @param string q 
	* @return array
	*/
	public function geocode($address) {
		return $this->getGeocoder()->geocode($address);
	}
	/**
	* Reverse geocodes long/lat to the smallest bounding WOEID
	* @param real long Decimal Longitude
	* @param real lat Decimal Latitude
	* @return array single result
	*/
	public function reverseGeocode($lon, $lat) {
		return $this->getGeocoder()->reversegeocode($lon, $lat);
	}

	public function geocoderDiagnostics(){
		$this->getGeocoder();
		$this->geocoder->diagnostics();
		return true;
	}

	/**
	* Geocodes address string or placename
	* @param string q 
	* @return array
	*/
	protected function getGeocoder() {
		if (!$this->geocoder) {
			$this->geocoder = new GeocoderWrapper;
		}
		return $this->geocoder;
	}
}
?>
