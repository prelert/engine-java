/****************************************************************************
 *                                                                          *
 * Copyright 2015-2016 Prelert Ltd                                          *
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
package com.prelert.job.errorcodes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

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

public enum ErrorCodes
{
    /**
     * Represents unknown errors, typically those caused by
     * internal server errors
     */
    UNKNOWN_ERROR(0),

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
     * @deprecated since version 2.0.0, this functionality has been removed
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
     * @see java.time.format.DateTimeFormatter
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

    /**
     * The key for which an update was requested is invalid.
     */
    INVALID_UPDATE_KEY(10115),

    /**
     * The JSON configuration supplied to validate a detector
     * could not be parsed. The JSON is invalid.
     */
    DETECTOR_PARSE_ERROR(10116),

    /**
     * The detector JSON contains a field that isn't recognised
     */
    DETECTOR_UNKNOWN_FIELD_ERROR(10117),

    /**
     * Failed to encrypt password.
     */
    ENCRYPTION_FAILURE_ERROR(10118),

    /**
     * The 'excludeFrequent' option is set to an invalid value.
     */
    INVALID_EXCLUDEFREQUENT_SETTING(10119),

    /**
     * The transform name isn't recognised
     */
    UNKNOWN_TRANSFORM(10201),

    /**
     * The transform does not have the correct number of inputs
     */
    TRANSFORM_INVALID_INPUT_COUNT(10202),

    /**
     * If none of the outputs of a particular transform are used
     * in the analysis then the transform's configuration is in error
     */
    TRANSFORM_OUTPUTS_UNUSED(10203),

    /**
     * The transform has a circular dependency
     */
    TRANSFORM_HAS_CIRCULAR_DEPENDENCY(10204),

    /**
     * 2 or more transforms have the same output fieldname
     */
    DUPLICATED_TRANSFORM_OUTPUT_NAME(10205),

    /**
     * Some transforms such as Regex require an argument when
     * first constructed
     */
    TRANSFORM_INVALID_ARGUMENT_COUNT(10206),

    /**
     * When the data format is single line, transforms are required.
     */
    DATA_FORMAT_IS_SINGLE_LINE_BUT_NO_TRANSFORMS(10207),

    /**
     * The transform must have a valid condition
     */
    TRANSFORM_REQUIRES_CONDITION(10208),

    /**
     * Condition operator is not recognised
     */
    UNKNOWN_OPERATOR(10209),

    /**
     * Invalid condition argument
     */
    CONDITION_INVALID_ARGUMENT(10210),

    /**
     * Some transforms such as Regex require an argument when
     * first constructed
     */
    TRANSFORM_INVALID_OUTPUT_COUNT(10211),

    /**
     * Transform inputs cannot be empty strings.
     */
    TRANSFORM_INPUTS_CANNOT_BE_EMPTY_STRINGS(10212),

    /**
     * Transform outputs cannot be empty strings.
     */
    TRANSFORM_OUTPUTS_CANNOT_BE_EMPTY_STRINGS(10213),

    /**
     * The transform argument is invalid
     */
    TRANSFORM_INVALID_ARGUMENT(10214),

    /**
     * The JSON configuration supplied to validate a transform
     * could not be parsed. The JSON is invalid.
     */
    TRANSFORM_PARSE_ERROR(10215),

    /**
     * The transform JSON contains a field that isn't recognised
     */
    TRANSFORM_UNKNOWN_FIELD_ERROR(10216),

    /**
     * The scheduler has been told to pull data from an unknown data source.
     */
    SCHEDULER_UNKNOWN_DATASOURCE(10301),

    /**
     * A field in the scheduler config is not supported for the chosen data
     * source.
     */
    SCHEDULER_FIELD_NOT_SUPPORTED_FOR_DATASOURCE(10302),

    /**
     * A scheduler option that <em>is</em> appropriate for the chosen data
     * source has an invalid value.
     */
    SCHEDULER_INVALID_OPTION_VALUE(10303),

    /**
     * When scheduler is configured the bucket span has to be explicitly set.
     */
    SCHEDULER_REQUIRES_BUCKET_SPAN(10304),

    /**
     * A job configured with an ELASTICSEARCH scheduler cannot support latency.
     */
    SCHEDULER_ELASTICSEARCH_DOES_NOT_SUPPORT_LATENCY(10305),

    /**
     * A job configured with aggregations must use a summaryCountField.
     */
    SCHEDULER_AGGREGATIONS_REQUIRES_SUMMARY_COUNT_FIELD(10306),

    /**
     * A job configured with an ELASTICSEARCH scheduler must use the data format ELASTICSEARCH.
     */
    SCHEDULER_ELASTICSEARCH_REQUIRES_DATAFORMAT_ELASTICSEARCH(10307),

    /**
     * Both username and password must be specified if either is.
     */
    SCHEDULER_INCOMPLETE_CREDENTIALS(10308),

    /**
     * Both plain text and encrypted passwords were provided.
     */
    SCHEDULER_MULTIPLE_PASSWORDS(10309),

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

    /**
     * The input JSON data is malformed.
     */
    MALFORMED_JSON(30107),

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

    /**
     * The name of the log file contains "\\" or "/"
     * or it is not a subdirectory of the logs directory
     */
    INVALID_LOG_FILE_PATH(50103),


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
     * The timeout argument is not a valid number or is a negative number
     */
    INVALID_TIMEOUT_ARGUMENT(60105),

    /**
     * Parameters to flush end-point are invalid
     */
    INVALID_FLUSH_PARAMS(60106),

    /**
     * When time range is expected, the end date cannot be before the start date
     */
    END_DATE_BEFORE_START_DATE(60107),

    /**
     * Reset range parameters to the data/dataload end-points are invalid
     */
    INVALID_BUCKET_RESET_RANGE_PARAMS(60108),

    /**
     * Bucket reset has been requested but no latency was specified.
     */
    BUCKET_RESET_NOT_SUPPORTED(60109),

    /**
     * Pagination skip parameter cannot be < 0.
     */
    INVALID_SKIP_PARAM(60110),

    /**
     * Pagination take parameter cannot be < 0.
     */
    INVALID_TAKE_PARAM(60111),

    /**
     * Alert request on an invalid alert type
     */
    UNKNOWN_ALERT_TYPE(60112),

    /**
     * Influencers cannot alert on the probability
     */
    CANNOT_ALERT_ON_PROB(60113),

    /**
     * A job's scheduler cannot be started.
     */
    CANNOT_START_JOB_SCHEDULER(60114),

    /**
     * A job's scheduler cannot be stopped.
     */
    CANNOT_STOP_JOB_SCHEDULER(60115),

    /**
     * There is no such scheduled job.
     */
    NO_SUCH_SCHEDULED_JOB(60116),

    /**
     * The requested action is not allowed for a scheduled job.
     */
    ACTION_NOT_ALLOWED_FOR_SCHEDULED_JOB(60117),

    /**
     * The revert to snapshot parameters are invalid.
     */
    INVALID_REVERT_PARAMS(60118),

    /**
     * No model snapshot exists that matches the reversion request.
     */
    NO_SUCH_MODEL_SNAPSHOT(60119),

    /**
     * Can only revert a model snapshot for a job that is in the CLOSED state.
     */
    JOB_NOT_CLOSED(60120),

    /**
     * The change snapshot description parameters are invalid.
     */
    INVALID_DESCRIPTION_PARAMS(60121),

    /**
     * The snapshot description has already been used for this job.
     */
    DESCRIPTION_ALREADY_USED(60122),

    /**
     * The job cannot be paused.
     */
    CANNOT_PAUSE_JOB(60123),

    /**
     * The job cannot be resumed.
     */
    CANNOT_RESUME_JOB(60124),

    /**
     * Deleting the highest priority model snapshot is not allowed.
     */
    CANNOT_DELETE_HIGHEST_PRIORITY(60125),

    /**
     * A job's scheduler cannot be stopped.
     */
    CANNOT_UPDATE_JOB_SCHEDULER(60126),

    // Support bundle errors
    /**
     * Error running the support bundle script
     */
    SUPPORT_BUNDLE_EXECUTION_ERROR(70101);


    private long m_ErrorCode;
    private String m_ValueString;

    private ErrorCodes(long code)
    {
        m_ErrorCode = code;
        m_ValueString = Long.toString(code);
    }

    @JsonValue
    public long getValue()
    {
        return m_ErrorCode;
    }

    public String getValueString()
    {
        return m_ValueString;
    }

    @JsonCreator
    public static ErrorCodes fromCode(@JsonProperty("errorCode") long errorCode)
    {
        for (ErrorCodes e : ErrorCodes.values())
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
