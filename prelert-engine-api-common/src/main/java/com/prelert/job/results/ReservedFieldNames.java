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
package com.prelert.job.results;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.prelert.job.AnalysisConfig;
import com.prelert.job.AnalysisLimits;
import com.prelert.job.DataCounts;
import com.prelert.job.DataDescription;
import com.prelert.job.Detector;
import com.prelert.job.JobDetails;
import com.prelert.job.ModelDebugConfig;
import com.prelert.job.ModelSizeStats;
import com.prelert.job.ModelSnapshot;
import com.prelert.job.SchedulerConfig;
import com.prelert.job.quantiles.Quantiles;
import com.prelert.job.transform.TransformConfig;
import com.prelert.job.usage.Usage;


/**
 * Defines the field names that we use for our results.
 * Fields from the raw data with these names are not added to any result.  Even
 * different types of results will not have raw data fields with reserved names
 * added to them, as it could create confusion if in some results a given field
 * contains raw data and in others it contains some aspect of our output.
 */
public final class ReservedFieldNames
{
    /**
     * jobId isn't in this package, so redefine.
     */
    private static final String JOB_ID_NAME = "jobId";

    /**
     * @timestamp isn't in this package, so redefine.
     */
    private static final String ES_TIMESTAMP = "@timestamp";

    /**
     * This array should be updated to contain all the field names that appear
     * in any documents we store in our results index.  (The reason it's any
     * documents we store and not just results documents is that Elasticsearch
     * 2.x requires mappings for given fields be consistent across all types
     * in a given index.)
     */
    private static final String[] RESERVED_FIELD_NAME_ARRAY = {

        AnalysisConfig.BUCKET_SPAN,
        AnalysisConfig.BATCH_SPAN,
        AnalysisConfig.LATENCY,
        AnalysisConfig.PERIOD,
        AnalysisConfig.SUMMARY_COUNT_FIELD_NAME,
        AnalysisConfig.CATEGORIZATION_FIELD_NAME,
        AnalysisConfig.DETECTORS,
        AnalysisConfig.INFLUENCERS,
        AnalysisConfig.OVERLAPPING_BUCKETS,
        AnalysisConfig.RESULT_FINALIZATION_WINDOW,
        AnalysisConfig.MULTIVARIATE_BY_FIELDS,

        AnalysisLimits.MODEL_MEMORY_LIMIT,
        AnalysisLimits.CATEGORIZATION_EXAMPLES_LIMIT,

        AnomalyCause.PROBABILITY,
        AnomalyCause.OVER_FIELD_NAME,
        AnomalyCause.OVER_FIELD_VALUE,
        AnomalyCause.BY_FIELD_NAME,
        AnomalyCause.BY_FIELD_VALUE,
        AnomalyCause.CORRELATED_BY_FIELD_VALUE,
        AnomalyCause.PARTITION_FIELD_NAME,
        AnomalyCause.PARTITION_FIELD_VALUE,
        AnomalyCause.FUNCTION,
        AnomalyCause.FUNCTION_DESCRIPTION,
        AnomalyCause.TYPICAL,
        AnomalyCause.ACTUAL,
        AnomalyCause.INFLUENCERS,
        AnomalyCause.FIELD_NAME,

        AnomalyRecord.DETECTOR_INDEX,
        AnomalyRecord.PROBABILITY,
        AnomalyRecord.BY_FIELD_NAME,
        AnomalyRecord.BY_FIELD_VALUE,
        AnomalyRecord.CORRELATED_BY_FIELD_VALUE,
        AnomalyRecord.PARTITION_FIELD_NAME,
        AnomalyRecord.PARTITION_FIELD_VALUE,
        AnomalyRecord.FUNCTION,
        AnomalyRecord.FUNCTION_DESCRIPTION,
        AnomalyRecord.TYPICAL,
        AnomalyRecord.ACTUAL,
        AnomalyRecord.IS_INTERIM,
        AnomalyRecord.INFLUENCERS,
        AnomalyRecord.FIELD_NAME,
        AnomalyRecord.OVER_FIELD_NAME,
        AnomalyRecord.OVER_FIELD_VALUE,
        AnomalyRecord.CAUSES,
        AnomalyRecord.ANOMALY_SCORE,
        AnomalyRecord.NORMALIZED_PROBABILITY,
        AnomalyRecord.INITIAL_NORMALIZED_PROBABILITY,
        AnomalyRecord.BUCKET_SPAN,

        Bucket.ANOMALY_SCORE,
        Bucket.BUCKET_SPAN,
        Bucket.MAX_NORMALIZED_PROBABILITY,
        Bucket.IS_INTERIM,
        Bucket.RECORD_COUNT,
        Bucket.EVENT_COUNT,
        Bucket.RECORDS,
        Bucket.BUCKET_INFLUENCERS,
        Bucket.INFLUENCERS,
        Bucket.INITIAL_ANOMALY_SCORE,

        BucketInfluencer.BUCKET_TIME,
        BucketInfluencer.INFLUENCER_FIELD_NAME,
        BucketInfluencer.INITIAL_ANOMALY_SCORE,
        BucketInfluencer.ANOMALY_SCORE,
        BucketInfluencer.RAW_ANOMALY_SCORE,
        BucketInfluencer.PROBABILITY,

        CategoryDefinition.CATEGORY_ID,
        CategoryDefinition.TERMS,
        CategoryDefinition.REGEX,
        CategoryDefinition.EXAMPLES,

        DataCounts.BUCKET_COUNT,
        DataCounts.PROCESSED_RECORD_COUNT,
        DataCounts.PROCESSED_FIELD_COUNT,
        DataCounts.INPUT_BYTES,
        DataCounts.INPUT_RECORD_COUNT,
        DataCounts.INPUT_FIELD_COUNT,
        DataCounts.INVALID_DATE_COUNT,
        DataCounts.MISSING_FIELD_COUNT,
        DataCounts.OUT_OF_ORDER_TIME_COUNT,
        DataCounts.FAILED_TRANSFORM_COUNT,
        DataCounts.EXCLUDED_RECORD_COUNT,
        DataCounts.LATEST_RECORD_TIME,

        DataDescription.FORMAT,
        DataDescription.TIME_FIELD_NAME,
        DataDescription.TIME_FORMAT,
        DataDescription.FIELD_DELIMITER,
        DataDescription.QUOTE_CHARACTER,

        Detector.DETECTOR_DESCRIPTION,
        Detector.FUNCTION,
        Detector.FIELD_NAME,
        Detector.BY_FIELD_NAME,
        Detector.OVER_FIELD_NAME,
        Detector.PARTITION_FIELD_NAME,
        Detector.USE_NULL,

        Influence.INFLUENCER_FIELD_NAME,
        Influence.INFLUENCER_FIELD_VALUES,

        Influencer.PROBABILITY,
        Influencer.INFLUENCER_FIELD_NAME,
        Influencer.INFLUENCER_FIELD_VALUE,
        Influencer.INITIAL_ANOMALY_SCORE,
        Influencer.ANOMALY_SCORE,

        // JobDetails.DESCRIPTION is not reserved because it is an analyzed string
        // JobDetails.STATUS is not reserved because it is an analyzed string
        JobDetails.DATA_DESCRIPTION,
        JobDetails.SCHEDULER_STATUS,
        JobDetails.SCHEDULER_CONFIG,
        JobDetails.FINISHED_TIME,
        JobDetails.LAST_DATA_TIME,
        JobDetails.COUNTS,
        JobDetails.TIMEOUT,
        JobDetails.RENORMALIZATION_WINDOW_DAYS,
        JobDetails.BACKGROUND_PERSIST_INTERVAL,
        JobDetails.MODEL_SNAPSHOT_RETENTION_DAYS,
        JobDetails.RESULTS_RETENTION_DAYS,
        JobDetails.ANALYSIS_CONFIG,
        JobDetails.ANALYSIS_LIMITS,
        JobDetails.TRANSFORMS,
        JobDetails.MODEL_DEBUG_CONFIG,
        JobDetails.IGNORE_DOWNTIME,
        JobDetails.CUSTOM_SETTINGS,

        ModelDebugConfig.WRITE_TO,
        ModelDebugConfig.BOUNDS_PERCENTILE,
        ModelDebugConfig.TERMS,

        ModelDebugOutput.PARTITION_FIELD_NAME,
        ModelDebugOutput.PARTITION_FIELD_VALUE,
        ModelDebugOutput.OVER_FIELD_NAME,
        ModelDebugOutput.OVER_FIELD_VALUE,
        ModelDebugOutput.BY_FIELD_NAME,
        ModelDebugOutput.BY_FIELD_VALUE,
        ModelDebugOutput.DEBUG_FEATURE,
        ModelDebugOutput.DEBUG_LOWER,
        ModelDebugOutput.DEBUG_UPPER,
        ModelDebugOutput.DEBUG_MEAN,
        ModelDebugOutput.ACTUAL,

        ModelSizeStats.MODEL_BYTES,
        ModelSizeStats.TOTAL_BY_FIELD_COUNT,
        ModelSizeStats.TOTAL_OVER_FIELD_COUNT,
        ModelSizeStats.TOTAL_PARTITION_FIELD_COUNT,
        ModelSizeStats.BUCKET_ALLOCATION_FAILURES_COUNT,
        ModelSizeStats.MEMORY_STATUS,
        ModelSizeStats.LOG_TIME,

        // ModelSnapshot.DESCRIPTION is not reserved because it is an analyzed string
        ModelSnapshot.RESTORE_PRIORITY,
        ModelSnapshot.SNAPSHOT_ID,
        ModelSnapshot.SNAPSHOT_DOC_COUNT,
        ModelSizeStats.TYPE,
        ModelSnapshot.LATEST_RECORD_TIME,
        ModelSnapshot.LATEST_RESULT_TIME,

        Quantiles.QUANTILE_STATE,

        SchedulerConfig.DATA_SOURCE,
        SchedulerConfig.DATA_SOURCE_COMPATIBILITY,
        SchedulerConfig.QUERY_DELAY,
        SchedulerConfig.FREQUENCY,
        SchedulerConfig.FILE_PATH,
        SchedulerConfig.TAIL_FILE,
        SchedulerConfig.BASE_URL,
        // SchedulerConfig.USERNAME, is not reserved because it is an analyzed string
        SchedulerConfig.ENCRYPTED_PASSWORD,
        SchedulerConfig.INDEXES,
        SchedulerConfig.TYPES,
        SchedulerConfig.QUERY,
        SchedulerConfig.RETRIEVE_WHOLE_SOURCE,
        SchedulerConfig.AGGREGATIONS,
        SchedulerConfig.AGGS,
        SchedulerConfig.SCRIPT_FIELDS,
        SchedulerConfig.SCROLL_SIZE,

        TransformConfig.TRANSFORM,
        TransformConfig.ARGUMENTS,
        TransformConfig.INPUTS,
        TransformConfig.OUTPUTS,

        Usage.INPUT_BYTES,
        Usage.INPUT_FIELD_COUNT,
        Usage.INPUT_RECORD_COUNT,

        JOB_ID_NAME,
        ES_TIMESTAMP
    };

    /**
     * A set of all reserved field names in our results.  Fields from the raw
     * data with these names are not added to any result.
     */
    public static final Set<String> RESERVED_FIELD_NAMES = new HashSet<>(Arrays.asList(RESERVED_FIELD_NAME_ARRAY));

    private ReservedFieldNames()
    {
    }
}
