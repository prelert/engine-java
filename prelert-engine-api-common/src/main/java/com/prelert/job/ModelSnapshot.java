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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.prelert.job.quantiles.Quantiles;


/**
 * ModelSnapshot Result POJO
 */
@JsonInclude(Include.NON_NULL)
public class ModelSnapshot
{
    /**
     * Field Names
     */
    public static final String TIMESTAMP = "timestamp";
    public static final String DESCRIPTION = "description";
    public static final String RESTORE_PRIORITY = "restorePriority";
    public static final String SNAPSHOT_ID = "snapshotId";
    public static final String SNAPSHOT_DOC_COUNT = "snapshotDocCount";
    public static final String LATEST_RECORD_TIME = "latestRecordTimeStamp";
    public static final String LATEST_RESULT_TIME = "latestResultTimeStamp";

    /**
     * Elasticsearch type
     */
    public static final String TYPE = "modelSnapshot";

    private Date m_Timestamp;
    private String m_Description;
    private long m_RestorePriority;
    private String m_SnapshotId;
    private int m_SnapshotDocCount;
    private ModelSizeStats m_ModelSizeStats;
    private Date m_LatestRecordTimeStamp;
    private Date m_LatestResultTimeStamp;
    private Quantiles m_Quantiles;

    public Date getTimestamp()
    {
        return m_Timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        m_Timestamp = timestamp;
    }

    public String getDescription()
    {
        return m_Description;
    }

    public void setDescription(String description)
    {
        m_Description = description;
    }

    public long getRestorePriority()
    {
        return m_RestorePriority;
    }

    public void setRestorePriority(long restorePriority)
    {
        m_RestorePriority = restorePriority;
    }

    public String getSnapshotId()
    {
        return m_SnapshotId;
    }

    public void setSnapshotId(String snapshotId)
    {
        m_SnapshotId = snapshotId;
    }

    public int getSnapshotDocCount()
    {
        return m_SnapshotDocCount;
    }

    public void setSnapshotDocCount(int snapshotDocCount)
    {
        m_SnapshotDocCount = snapshotDocCount;
    }

    public ModelSizeStats getModelSizeStats()
    {
        return m_ModelSizeStats;
    }

    public void setModelSizeStats(ModelSizeStats modelSizeStats)
    {
        m_ModelSizeStats = modelSizeStats;
    }

    public Quantiles getQuantiles()
    {
        return m_Quantiles;
    }

    public void setQuantiles(Quantiles q)
    {
        m_Quantiles = q;
    }

    public Date getLatestRecordTimeStamp()
    {
        return m_LatestRecordTimeStamp;
    }

    public void setLatestRecordTimeStamp(Date latestRecordTimeStamp)
    {
        m_LatestRecordTimeStamp = latestRecordTimeStamp;
    }

    public Date getLatestResultTimeStamp()
    {
        return m_LatestResultTimeStamp;
    }

    public void setLatestResultTimeStamp(Date latestResultTimeStamp)
    {
        m_LatestResultTimeStamp = latestResultTimeStamp;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(m_Timestamp, m_Description, m_RestorePriority, m_SnapshotId, m_Quantiles,
                m_SnapshotDocCount, m_ModelSizeStats, m_LatestRecordTimeStamp, m_LatestResultTimeStamp);
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

        if (other instanceof ModelSnapshot == false)
        {
            return false;
        }

        ModelSnapshot that = (ModelSnapshot) other;

        return Objects.equals(this.m_Timestamp, that.m_Timestamp)
                && Objects.equals(this.m_Description, that.m_Description)
                && this.m_RestorePriority == that.m_RestorePriority
                && Objects.equals(this.m_SnapshotId, that.m_SnapshotId)
                && this.m_SnapshotDocCount == that.m_SnapshotDocCount
                && Objects.equals(this.m_ModelSizeStats, that.m_ModelSizeStats)
                && Objects.equals(this.m_Quantiles,  that.m_Quantiles)
                && Objects.equals(this.m_LatestRecordTimeStamp, that.m_LatestRecordTimeStamp)
                && Objects.equals(this.m_LatestResultTimeStamp, that.m_LatestResultTimeStamp);
    }
}
