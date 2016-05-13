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
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Provide access to the C++ model memory usage numbers
 * for the Java process.
 */
@JsonIgnoreProperties({"modelSizeStatsId"})
public class ModelSizeStats
{
    /**
     * Field Names
     */
    public static final String MODEL_SIZE_STATS_ID = "modelSizeStatsId";
    public static final String MODEL_BYTES = "modelBytes";
    public static final String TOTAL_BY_FIELD_COUNT = "totalByFieldCount";
    public static final String TOTAL_OVER_FIELD_COUNT = "totalOverFieldCount";
    public static final String TOTAL_PARTITION_FIELD_COUNT = "totalPartitionFieldCount";
    public static final String BUCKET_ALLOCATION_FAILURES_COUNT = "bucketAllocationFailuresCount";
    public static final String MEMORY_STATUS = "memoryStatus";
    public static final String LOG_TIME = "logTime";
    public static final String BUCKET_TIME = "bucketTime";
    public static final String TIMESTAMP = "timestamp";

    /**
     * Elasticsearch type
     */
    public static final String TYPE = "modelSizeStats";

    /**
     * The status of the memory monitored by the ResourceMonitor.
     * OK is default, SOFT_LIMIT means that the models have done
     * some aggressive pruning to keep the memory below the limit,
     * and HARD_LIMIT means that samples have been dropped
     */
    public enum MemoryStatus { OK, SOFT_LIMIT, HARD_LIMIT }

    private long m_ModelBytes;
    private long m_TotalByFieldCount;
    private long m_TotalOverFieldCount;
    private long m_TotalPartitionFieldCount;
    private long m_BucketAllocationFailuresCount;
    private MemoryStatus m_MemoryStatus;
    private Date m_Timestamp;
    private Date m_LogTime;
    private String m_Id = TYPE;

    public ModelSizeStats()
    {
        m_ModelBytes = 0;
        m_TotalByFieldCount = 0;
        m_TotalOverFieldCount = 0;
        m_TotalPartitionFieldCount = 0;
        m_BucketAllocationFailuresCount = 0;
        m_MemoryStatus = MemoryStatus.OK;
        m_LogTime = new Date();
    }

    public String getModelSizeStatsId()
    {
        return m_Id;
    }

    public void setModelSizeStatsId(String id)
    {
        m_Id = id;
    }

    public void setModelBytes(long m)
    {
        m_ModelBytes = m;
    }

    public long getModelBytes()
    {
        return m_ModelBytes;
    }

    public void setTotalByFieldCount(long m)
    {
        m_TotalByFieldCount = m;
    }

    public long getTotalByFieldCount()
    {
        return m_TotalByFieldCount;
    }

    public void setTotalPartitionFieldCount(long m)
    {
        m_TotalPartitionFieldCount = m;
    }

    public long getTotalPartitionFieldCount()
    {
        return m_TotalPartitionFieldCount;
    }

    public void setTotalOverFieldCount(long m)
    {
        m_TotalOverFieldCount = m;
    }

    public long getTotalOverFieldCount()
    {
        return m_TotalOverFieldCount;
    }

    public void setBucketAllocationFailuresCount(long m)
    {
        m_BucketAllocationFailuresCount = m;
    }

    public long getBucketAllocationFailuresCount()
    {
        return m_BucketAllocationFailuresCount;
    }

    public void setMemoryStatus(String m)
    {
        if (m == null || m.isEmpty())
        {
            m_MemoryStatus = MemoryStatus.OK;
        }
        else
        {
            m_MemoryStatus = MemoryStatus.valueOf(m);
        }
    }

    public String getMemoryStatus()
    {
        return m_MemoryStatus.name();
    }

    public Date getTimestamp()
    {
        return m_Timestamp;
    }

    public void setTimestamp(Date d)
    {
        m_Timestamp = d;
    }

    public Date getLogTime()
    {
        return m_LogTime;
    }

    public void setLogTime(Date d)
    {
        m_LogTime = d;
    }

    @Override
    public int hashCode()
    {
        // m_Id excluded here as it is generated by the datastore
        return Objects.hash(m_ModelBytes, m_TotalByFieldCount, m_TotalOverFieldCount, m_TotalPartitionFieldCount,
                m_BucketAllocationFailuresCount, m_MemoryStatus, m_Timestamp, m_LogTime);
    }

    /**
     * Compare all the fields.
     */
    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }

        if (other instanceof ModelSizeStats == false)
        {
            return false;
        }

        ModelSizeStats that = (ModelSizeStats) other;

        // m_Id excluded here as it is generated by the datastore
        return this.m_ModelBytes == that.m_ModelBytes
                && this.m_TotalByFieldCount == that.m_TotalByFieldCount
                && this.m_TotalOverFieldCount == that.m_TotalOverFieldCount
                && this.m_TotalPartitionFieldCount == that.m_TotalPartitionFieldCount
                && this.m_BucketAllocationFailuresCount == that.m_BucketAllocationFailuresCount
                && Objects.equals(this.m_MemoryStatus, that.m_MemoryStatus)
                && Objects.equals(this.m_Timestamp, that.m_Timestamp)
                && Objects.equals(this.m_LogTime, that.m_LogTime);
    }
}
