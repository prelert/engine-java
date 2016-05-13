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

package com.prelert.job;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Job processed record counts.
 *
 * The getInput... methods return the actual number of
 * fields/records sent the the API including invalid records.
 * The getProcessed... methods are the number sent to the
 * Engine.
 *
 * The <code>inputRecordCount</code> field is calculated so it
 * should not be set in deserialisation but it should be read in
 * serialistion - hence the annotations and the private setter
 */

@JsonInclude(Include.NON_NULL)
public class DataCounts
{
    public static final String BUCKET_COUNT = "bucketCount";
    public static final String PROCESSED_RECORD_COUNT = "processedRecordCount";
    public static final String PROCESSED_FIELD_COUNT = "processedFieldCount";
    public static final String INPUT_BYTES = "inputBytes";
    public static final String INPUT_RECORD_COUNT = "inputRecordCount";
    public static final String INPUT_FIELD_COUNT = "inputFieldCount";
    public static final String INVALID_DATE_COUNT = "invalidDateCount";
    public static final String MISSING_FIELD_COUNT = "missingFieldCount";
    public static final String OUT_OF_ORDER_TIME_COUNT = "outOfOrderTimeStampCount";
    public static final String FAILED_TRANSFORM_COUNT = "failedTransformCount";
    public static final String LATEST_RECORD_TIME = "latestRecordTimeStamp";
    public static final String EXCLUDED_RECORD_COUNT = "excludedRecordCount";


    private Long m_BucketCount;
    private long m_ProcessedRecordCount;
    private long m_ProcessedFieldCount;
    private long m_InputBytes;
    private long m_InputFieldCount;
    private long m_InvalidDateCount;
    private long m_MissingFieldCount;
    private long m_OutOfOrderTimeStampCount;
    private long m_FailedTransformCount;
    private long m_ExcludedRecordCount;
    private Date m_LatestRecordTimeStamp;

    public DataCounts()
    {
        m_BucketCount = new Long(0);
    }

    public DataCounts(DataCounts lhs)
    {
        m_BucketCount = lhs.m_BucketCount;
        m_ProcessedRecordCount = lhs.m_ProcessedRecordCount;
        m_ProcessedFieldCount = lhs.m_ProcessedFieldCount;
        m_InputBytes = lhs.m_InputBytes;
        m_InputFieldCount = lhs.m_InputFieldCount;
        m_InvalidDateCount = lhs.m_InvalidDateCount;
        m_MissingFieldCount = lhs.m_MissingFieldCount;
        m_OutOfOrderTimeStampCount = lhs.m_OutOfOrderTimeStampCount;
        m_FailedTransformCount = lhs.m_FailedTransformCount;
        m_LatestRecordTimeStamp = lhs.m_LatestRecordTimeStamp;
        m_ExcludedRecordCount = lhs.m_ExcludedRecordCount;
    }


    /**
     * The number of bucket results
     * @return May be <code>null</code>
     */
    public Long getBucketCount()
    {
        return m_BucketCount;
    }

    public void setBucketCount(Long count)
    {
        m_BucketCount = count;
    }

    /**
     * Number of records processed by this job.
     * This value is the number of records sent passed on to
     * the engine i.e. {@linkplain #getInputRecordCount()} minus
     * records with bad dates or out of order
     * @return Number of records processed by this job {@code long}
     */
    public long getProcessedRecordCount()
    {
        return m_ProcessedRecordCount;
    }

    public void setProcessedRecordCount(long count)
    {
        m_ProcessedRecordCount = count;
    }

    public void incrementProcessedRecordCount(long additional)
    {
        m_ProcessedRecordCount += additional;
    }

    /**
     * Number of data points (processed record count * the number
     * of analysed fields) processed by this job. This count does
     * not include the time field.
     * @return Number of data points processed by this job {@code long}
     */
    public long getProcessedFieldCount()
    {
        return m_ProcessedFieldCount;
    }

    public void setProcessedFieldCount(long count)
    {
        m_ProcessedFieldCount = count;
    }

    public void calcProcessedFieldCount(long analysisFieldsPerRecord)
    {
        m_ProcessedFieldCount =
                (m_ProcessedRecordCount * analysisFieldsPerRecord)
                - m_MissingFieldCount;

        // processedFieldCount could be a -ve value if no
        // records have been written in which case it should be 0
        m_ProcessedFieldCount = (m_ProcessedFieldCount < 0) ? 0 : m_ProcessedFieldCount;
    }

    /**
     * Total number of input records read.
     * This = processed record count + date parse error records count
     * + out of order record count.
     *
     * Records with missing fields are counted as they are still written.
     * @return Total number of input records read {@code long}
     */
    @JsonProperty
    public long getInputRecordCount()
    {
        return m_ProcessedRecordCount + m_OutOfOrderTimeStampCount
                                + m_InvalidDateCount + m_ExcludedRecordCount;
    }

    /**
     * Only present to keep jackson serialisation happy.
     * This property should not be deserialised
     * @param count
     */
    @JsonIgnore
    private void setInputRecordCount(long count)
    {
        throw new IllegalStateException();
    }

    /**
     * The total number of bytes sent to this job.
     * This value includes the bytes from any  records
     * that have been discarded for any  reason
     * e.g. because the date cannot be read
     * @return Volume in bytes
     */
    public long getInputBytes()
    {
        return m_InputBytes;
    }

    public void setInputBytes(long volume)
    {
        m_InputBytes = volume;
    }

    public void incrementInputBytes(long additional)
    {
        m_InputBytes += additional;
    }

    /**
     * The total number of fields sent to the job
     * including fields that aren't analysed.
     * @return The total number of fields sent to the job
     */
    public long getInputFieldCount()
    {
        return m_InputFieldCount;
    }

    public void setInputFieldCount(long count)
    {
        m_InputFieldCount = count;
    }

    public void incrementInputFieldCount(long additional)
    {
        m_InputFieldCount += additional;
    }

    /**
     * The number of records with an invalid date field that could
     * not be parsed or converted to epoch time.
     * @return The number of records with an invalid date field
     */
    public long getInvalidDateCount()
    {
        return m_InvalidDateCount;
    }

    public void setInvalidDateCount(long count)
    {
        m_InvalidDateCount = count;
    }

    public void incrementInvalidDateCount(long additional)
    {
        m_InvalidDateCount += additional;
    }


    /**
     * The number of missing fields that had been
     * configured for analysis.
     * @return The number of missing fields
     */
    public long getMissingFieldCount()
    {
        return m_MissingFieldCount;
    }

    public void setMissingFieldCount(long count)
    {
        m_MissingFieldCount = count;
    }

    public void incrementMissingFieldCount(long additional)
    {
        m_MissingFieldCount += additional;
    }

    /**
     * The number of records with a timestamp that is
     * before the time of the latest record. Records should
     * be in ascending chronological order
     * @return The number of records with a timestamp that is before the time of the latest record
     */
    public long getOutOfOrderTimeStampCount()
    {
        return m_OutOfOrderTimeStampCount;
    }

    public void setOutOfOrderTimeStampCount(long count)
    {
        m_OutOfOrderTimeStampCount = count;
    }

    public void incrementOutOfOrderTimeStampCount(long additional)
    {
        m_OutOfOrderTimeStampCount += additional;
    }


    /**
     * The number of transforms that failed.
     * In theory this could be more than the number of records
     * if multiple transforms are applied to each record
     * @return The number of transforms that failed
     */
    public long getFailedTransformCount()
    {
        return m_FailedTransformCount;
    }

    public void setFailedTransformCount(long failedTransformCount)
    {
        this.m_FailedTransformCount = failedTransformCount;
    }

    public void incrementFailedTransformCount(long additional)
    {
        m_FailedTransformCount += additional;
    }

    /**
     * The number of records excluded by a transform
     * @return Number of excluded records
     */
    public long getExcludedRecordCount()
    {
        return m_ExcludedRecordCount;
    }

    public void setExcludedRecordCount(long excludedRecordCount)
    {
        m_ExcludedRecordCount = excludedRecordCount;
    }

    public void incrementExcludedRecordCount(long additional)
    {
        m_ExcludedRecordCount += additional;
    }

    /**
     * The time of the latest record seen.
     * @return Latest record time
     */
    public Date getLatestRecordTimeStamp()
    {
        return m_LatestRecordTimeStamp;
    }

    public void setLatestRecordTimeStamp(Date latestRecordTime)
    {
        m_LatestRecordTimeStamp = latestRecordTime;
    }

    public Map<String, Object> toObjectMap()
    {
        Map<String, Object> map = new HashMap<>();
        map.put(BUCKET_COUNT, m_BucketCount);
        map.put(PROCESSED_RECORD_COUNT, m_ProcessedRecordCount);
        map.put(PROCESSED_FIELD_COUNT, m_ProcessedFieldCount);
        map.put(INPUT_BYTES, m_InputBytes);
        map.put(INPUT_RECORD_COUNT, getInputRecordCount());
        map.put(INPUT_FIELD_COUNT, m_InputFieldCount);
        map.put(INVALID_DATE_COUNT, m_InvalidDateCount);
        map.put(MISSING_FIELD_COUNT, m_MissingFieldCount);
        map.put(OUT_OF_ORDER_TIME_COUNT, m_OutOfOrderTimeStampCount);
        map.put(FAILED_TRANSFORM_COUNT, m_FailedTransformCount);
        map.put(LATEST_RECORD_TIME, m_LatestRecordTimeStamp);
        map.put(EXCLUDED_RECORD_COUNT, m_ExcludedRecordCount);
        return map;
    }

    /**
     * Equality test
     */
    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }

        if (other instanceof DataCounts == false)
        {
            return false;
        }

        DataCounts that = (DataCounts)other;

        return this.m_BucketCount.equals(that.m_BucketCount) &&
                this.m_ProcessedRecordCount == that.m_ProcessedRecordCount &&
                this.m_ProcessedFieldCount == that.m_ProcessedFieldCount &&
                this.m_InputBytes == that.m_InputBytes &&
                this.m_InputFieldCount == that.m_InputFieldCount &&
                this.m_InvalidDateCount == that.m_InvalidDateCount &&
                this.m_MissingFieldCount == that.m_MissingFieldCount &&
                this.m_OutOfOrderTimeStampCount == that.m_OutOfOrderTimeStampCount &&
                this.m_FailedTransformCount == that.m_FailedTransformCount &&
                this.m_ExcludedRecordCount == that.m_ExcludedRecordCount &&
                Objects.equals(this.m_LatestRecordTimeStamp, that.m_LatestRecordTimeStamp);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(m_BucketCount, m_ProcessedRecordCount, m_ProcessedFieldCount,
                m_InputBytes, m_InputFieldCount, m_InvalidDateCount, m_MissingFieldCount,
                m_OutOfOrderTimeStampCount, m_FailedTransformCount, m_ExcludedRecordCount,
                m_LatestRecordTimeStamp);
    }
}
