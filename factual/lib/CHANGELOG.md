
## 1.5.4 (17 Oct 2013)
 * Assign error code to object on instantiation
 * Added addEntity() for resolving entity as array rather than key/value
 * Tested and built integration/regression test for quotes strings
 * Added getRawRequest() to obtain URL-encoded request string
 * Fixed issue in Geopulse Context URL generation
 * Removed monetize
 * Clarified 'not authorized' message on Diffs test: Diffs test returns true on 'not authorized'
 * Fixed bug in getDataAsJSON()
 * Added coded/unencoded request into same element in debug return
 * Added new, updated geopulse endpoint
 * Added unencoded request to debug mode
 * Added includes(), includesAny()
 * Added fetchrow()
 * Removed double index on SG
 * Updates to FactualTest.php
 * Changed JSON parse to isset()
 * Restructured postvars
 * Added two-line comment header for Wordpress …
 * Catching errors without message

## 1.5.3 (27 Jan 2013)
 * Addded Submit test
 * Modified error reporting for Diffs test; changed test table
 * Added clear support for Submit
 * Added strict mode compatibility to Submit
 * Added try/catch blocks to all remaining tests
 * Added South Africa and New Zealand to the test suite
 * Updated composer.json (thanks simonchrz)
 * Added getCommitID() method to get submit transaction handle
 * Removed removeValue() this is now part of the clear API
 * Test script now takes key and secret as parameters on command line
 * Fixed test for multi filter
 * Exposed curlinfo outside class for debug
 * Added removeValue() for submits
 * Added debug method to third-party geocode wrapper
 * Removed crosswalk tests as these are now normal reads
 * getRowCount() now alias of getTotalRowCount(). Previously returned null.
 * Fixed bug in rowcount var assignment (thanks rvsiqueira)
 
## 1.5.0 (13 Aug 2012)
 * Added Diffs API support
 * Added Submit API support
 * Added Flag API support
 * Added Match API support
 * Added Geopulse API support 
 * Factual::rawGet() now takes only array of key/values, and performs encoding
 * Added Factual::rawPost()
 * Factual::debug() now outputs exclusively to stderr 
 * Cleaned/Standardized Response Class inheritance  
 * Removed MulitResponse::getData(); now returns only array of Response objects
 * Removed syntactic sugar (the former, deprecated) version of Crosswalk. Use a normal table read
 * Moved documentation to [GitHub Wiki](https://github.com/Factual/factual-php-driver/wiki) from readme.md
 * Factual::resolve() shortcut formally supported; now returns ResolveResponse
 * Deprecated FactualQuery::only() for FactualQuery::select() for consistency with API parameter names
 * ReadResponse and descendant objects now preserve index of data
 * Published this Changelog
 
## 1.4.3
 * Deprecated syntactic sugar version of Crosswalk; now use regular table read
 * Added Monetize API support.
 * Added debug mode

## 1.4.0
 * Added Multi API support
 * Added Factual Reverse Geocode API support
 * Added World Geographies documentation
 * Improved autoload() compatibility

## 1.2.1
 * Added Facets
 * Moved exception detection and handling to class Factual (from the native Oauth class which failed to pass through important debug info)
 * Added Factual::rawGet()

## 1.0.2
 * Updated test CLI for Windows 

## 1.0.1
 * Filters now accept only arrays

## 1.0
 * Initial release
