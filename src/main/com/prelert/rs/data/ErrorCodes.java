/****************************************************************************
 *                                                                          *
 * Copyright 2014 Prelert Ltd                                               *
 *                                                                          *
 * Licensed under the Apache License, Version 2.0 (the "License");          *
 * you may not use this file except in compliance with the License.         *
 * You may obtain a copy of the License at                                  *
 *                                                                          *
 *    http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                          *
 * Unless required by applicable law or agreed to in writing, software      *
 * distributed under the License is distributed on an "AS IS" BASIS,        *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and      *
 * limitations under the License.                                           *
 *                                                                          *
 ***************************************************************************/

package com.prelert.rs.data;

/**
 * Static error codes returned in reponse to internal errors in the API.
 * The codes are grouped in the following way:
 * <ul>
 * <li>10XXX Codes are related to job creation</li>
 * <li>20XXX Codes are related to data store errors</li>
 * <li>30XXX Codes are related to data upload errors</li>
 * <li>40XXX Codes are related to running the native process errors</li>
 * <li>50XXX Codes are related to reading log files</li> 
 * <li>60XXX Codes are related to errors from the REST API</li> 
 * </ul>
 */
public class ErrorCodes 
{
	// job create errors
	/**
	 * The JSON configuration supplied to create a job 
	 * could not be parsed. The JSON is invalid. 
	 */
	final public static int JOB_CONFIG_PARSE_ERROR = 10101;
	
	/**
	 * The job configuration JSON contains a field that isn't
	 * recognised
	 */
	final public static int JOB_CONFIG_UNKNOWN_FIELD_ERROR = 10102; 
	
	/**
	 * When creating a new job from an existing job this error
	 * is returned if the reference job id is not known
	 */
	final public static int UNKNOWN_JOB_REFERENCE = 10103;
	
	/**
	 * The value provided in one of the job configuration fields
	 * is not allowed for example some fields cannot be a number < 0.
	 */
	final public static int INVALID_VALUE = 10104;
	
	/**
	 * The function argument is not recognised as one of
	 * the valid list of functions.
	 * @see com.prelert.job.Detector#ANALYSIS_FUNCTIONS
	 */
	final public static int UNKNOWN_FUNCTION = 10105;
		
	/**
	 * In the {@link com.prelert.job.Detector} some combinations
	 * of fieldName/byFieldName/overFieldName and function are invalid.
	 */
	final public static int INVALID_FIELD_SELECTION = 10106;
	
	/**
	 * The job configuration is not fully defined.
	 */
	final public static int INCOMPLETE_CONFIGURATION = 10107;
	
	/**
	 * The date format pattern cannot be interpreted as a valid 
	 * Java date format pattern.
	 * @see java.text.SimpleDateFormat
	 */
	final public static int INVALID_DATE_FORMAT = 10108;
	
	
	// Data store errors
	/**
	 * A generic exception from the data store
	 */
	final public static int DATA_STORE_ERROR = 20001;
	
	/**
	 * The job cannot be found in the data store
	 */
	final public static int MISSING_JOB_ERROR = 20101;
	
	/**
	 * The persisted detector state cannot be recovered
	 */
	final public static int MISSING_DETECTOR_STATE = 20102;	
	
	
	
	// data upload errors
	/**
	 * Generic data error
	 */
	final public static int DATA_ERROR = 30001;
	
	/**
	 * In the case of CSV data if a job is configured to use
	 * a certain field and that field isn't present in the CSV
	 * header this error is returned.
	 */
	final public static int MISSING_FIELD = 30101;
	
	/**
	 * Data was defined to be gzip encoded ('Content-Encoding:gzip') 
	 * but isn't actually.
	 */
	final public static int UNCOMPRESSED_DATA = 30102;
	
	
	
	// native process errors
	/**
	 * An unknown error has occurred in the Native process
	 */
	final public static int NATIVE_PROCESS_ERROR = 40001;

	/**
	 * An error occurred starting the native process
	 */
	final public static int NATIVE_PROCESS_START_ERROR = 40101;
	
	/**
	 * An error occurred writing data to the native process
	 */
	final public static int NATIVE_PROCESS_WRITE_ERROR = 40102;
	
	/**
	 * Certain operations e.g. delete cannot be applied 
	 * to running jobs.
	 */
	final public static int NATIVE_PROCESS_RUNNING_ERROR = 40103;
	
	
	// Log file reading errors
	/**
	 * The log directory does not exist
	 */
	final public static int CANNOT_OPEN_DIRECTORY = 50101;
	
	/**
	 * The log file cannot be read
	 */
	final public static int MISSING_LOG_FILE = 50102;
	
	// Rest API errors
	/**
	 * The date query parameter is un-parsable as a date
	 */
	final public static int UNPARSEABLE_DATE_ARGUMENT = 60101; 
}
