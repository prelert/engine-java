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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Static error codes returned in response to internal errors in the API.
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
public enum ErrorCode
{
	// job create errors
	/**
	 * The JSON configuration supplied to create a job
	 * could not be parsed. The JSON is invalid.
	 */
	JOB_CONFIG_PARSE_ERROR(10101),

	/**
	 * The job configuration JSON contains a field that isn't
	 * recognised
	 */
	JOB_CONFIG_UNKNOWN_FIELD_ERROR(10102),

	/**
	 * When creating a new job from an existing job this error
	 * is returned if the reference job id is not known
	 */
	UNKNOWN_JOB_REFERENCE(10103),

	/**
	 * The value provided in one of the job configuration fields
	 * is not allowed for example some fields cannot be a number < 0.
	 */
	INVALID_VALUE(10104),

	/**
	 * The function argument is not recognised as one of
	 * the valid list of functions.
	 * @see com.prelert.job.Detector#ANALYSIS_FUNCTIONS
	 */
	UNKNOWN_FUNCTION(10105),

	/**
	 * In the {@link com.prelert.job.Detector} some combinations
	 * of fieldName/byFieldName/overFieldName and function are invalid.
	 */
	INVALID_FIELD_SELECTION(10106),

	/**
	 * The job configuration is not fully defined.
	 */
	INCOMPLETE_CONFIGURATION(10107),

	/**
	 * The date format pattern cannot be interpreted as a valid
	 * Java date format pattern.
	 * @see java.text.SimpleDateFormat
	 */
	INVALID_DATE_FORMAT(10108),

	/**
	 * The job will not be created because it violates one or
	 * more license constraints.
	 */
	LICENSE_VIOLATION(10109),

	/**
	 * The job could not be created because the supplied Job Id
	 * is not unique or is already used by another job.
	 */
	JOB_ID_TAKEN(10110),

	/**
	 * The job id contains an upper case character or one of the
	 * following invalid characters:
	 * [\\, /, *, ?, \", <, >, |,  , ,]
	 */
	PROHIBITIED_CHARACTER_IN_JOB_ID(10111),

	/**
	 * The submitted job id contains too many characters.
	 */
	JOB_ID_TOO_LONG(10112),

	/**
	 * fieldnames including over, by and partition fields cannot
	 * contain any of these characters:
	 * [, ], (, ), =, ", \, -
	 */
	PROHIBITIED_CHARACTER_IN_FIELD_NAME(10113),

	/**
	 * The function argument is not allowed in conjunction with
	 * the other job parameters.
	 */
	INVALID_FUNCTION(10114),


	// Data store errors
	/**
	 * A generic exception from the data store
	 */
	DATA_STORE_ERROR(20001),

	/**
	 * The job cannot be found in the data store
	 */
	MISSING_JOB_ERROR(20101),

	// data upload errors
	/**
	 * Generic data error
	 */
	DATA_ERROR(30001),

	/**
	 * In the case of CSV data if a job is configured to use
	 * a certain field and that field isn't present in the CSV
	 * header this error is returned.
	 */
	MISSING_FIELD(30101),

	/**
	 * Data was defined to be gzip encoded ('Content-Encoding:gzip')
	 * but isn't actually.
	 */
	UNCOMPRESSED_DATA(30102),

	/**
	 * As a proportion of all the records sent too many
	 * are either missing a date or the date cannot be parsed.
	 */
	TOO_MANY_BAD_DATES(30103),

	/**
	 * As a proportion of all the records sent a high number are
	 * missing required fields or cannot be parsed.
	 */
	TOO_MANY_BAD_RECORDS(30104),

	/**
	 * As a proportion of all the records sent a high number are not
	 * in chronological order.
	 */
	TOO_MANY_OUT_OF_ORDER_RECORDS(30105),

	 /**
     * User is trying to upload data for a job while there are too
     * many jobs running concurrently.
     */
    TOO_MANY_JOBS_RUNNING_CONCURRENTLY(30106),


	// native process errors
	/**
	 * An unknown error has occurred in the Native process
	 */
	NATIVE_PROCESS_ERROR(40001),

	/**
	 * An error occurred starting the native process
	 */
	NATIVE_PROCESS_START_ERROR(40101),

	/**
	 * An error occurred writing data to the native process
	 */
	NATIVE_PROCESS_WRITE_ERROR(40102),

	/**
	 * The native autodetect process will only accept a single stream
	 * of data at a time. It is an error to try to upload data to the same
	 * job through multiple connections. Additionally certain operations such
	 * as delete cannot be applied to a job that is actively processing data
	 * and will result in this error condition.
	 */
	NATIVE_PROCESS_CONCURRENT_USE_ERROR(40103),

	/**
	 * A flush command to the native autodetect process was interrupted.
	 * Data received prior to the flush may or may not have been processed if a
	 * flush request returns with this error code.
	 */
	NATIVE_PROCESS_FLUSH_INTERRUPTED(40104),


	// Log file reading errors
	/**
	 * The log directory does not exist
	 */
	CANNOT_OPEN_DIRECTORY(50101),

	/**
	 * The log file cannot be read
	 */
	MISSING_LOG_FILE(50102),


	// Rest API errors
	/**
	 * The date query parameter is un-parsable as a date
	 */
	UNPARSEABLE_DATE_ARGUMENT(60101),

	/**
	 * The argument to the sort query parameter is not a
	 * recognised sort field
	 */
	INVALID_SORT_FIELD(60102),

	/**
	 * Cannot alert on a Job that isn't running
	 */
	JOB_NOT_RUNNING(60103),

	/**
	 * The threshold parameters supplied to the alert are invalid
	 */
	INVALID_THRESHOLD_ARGUMENT(60104),

	/**
	 *
	 */
	INVALID_TIMEOUT_ARGUMENT(60105);


	private long m_ErrorCode;
	private String m_ValueString;

	private ErrorCode(long code)
	{
		m_ErrorCode = code;
		m_ValueString = Long.toString(code);
	}

	public long getValue()
	{
		return m_ErrorCode;
	}

	public String getValueString()
	{
		return m_ValueString;
	}

	@JsonCreator
	public static ErrorCode fromCode(@JsonProperty("errorCode") long errorCode)
	{
		for (ErrorCode e : ErrorCode.values())
		{
			if (errorCode == e.getValue())
			{
				return e;
			}
		}

		throw new IllegalArgumentException("The code " + errorCode +
				" is not a valid error code");
	}
}
