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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Bucket Result POJO
 */
@JsonIgnoreProperties({"epoch", "detectors"})
@JsonInclude(Include.NON_NULL)
public class Bucket
{
    /*
     * Field Names
     */
    public static final String ID = "id";
    public static final String TIMESTAMP = "timestamp";
    public static final String RAW_ANOMALY_SCORE = "rawAnomalyScore";
    public static final String ANOMALY_SCORE = "anomalyScore";
    public static final String MAX_NORMALIZED_PROBABILITY = "maxNormalizedProbability";
    public static final String IS_INTERIM = "isInterim";
    public static final String RECORD_COUNT = "recordCount";
    public static final String EVENT_COUNT = "eventCount";
    public static final String DETECTORS = "detectors";
    public static final String RECORDS = "records";


    /**
     * Elasticsearch type
     */
    public static final String TYPE = "bucket";

    private static final Logger LOGGER = Logger.getLogger(Bucket.class);

    private Date m_Timestamp;
    private double m_RawAnomalyScore;
    private double m_AnomalyScore;
    private double m_MaxNormalizedProbability;
    private int m_RecordCount;
    private List<Detector> m_Detectors;
    private List<AnomalyRecord> m_Records;
    private long m_EventCount;
    private Boolean m_IsInterim;
    private boolean m_HadBigNormalisedUpdate;

    public Bucket()
    {
        m_Detectors = new ArrayList<>();
        m_Records = Collections.emptyList();
    }

    /**
     * The bucket Id is the bucket's timestamp in seconds
     * from the epoch. As the id is derived from the timestamp
     * field it doesn't need to be serialised, however, in the
     * past it was serialised accidentally, so it still is.
     *
     * @return The bucket id
     */
    public String getId()
    {
        return Long.toString(getEpoch()).intern();
    }


    /**
     * Set the ID and derive the timestamp from it.  It MUST be
     * a number that corresponds to the bucket's timestamp in seconds
     * from the epoch.
     */
    public void setId(String id)
    {
        try
        {
            long epoch = Long.parseLong(id);
            m_Timestamp = new Date(epoch * 1000);
        }
        catch (NumberFormatException nfe)
        {
            LOGGER.error("Could not parse ID " + id + " as a long");
        }
    }


    /**
     * Timestamp expressed in seconds since the epoch (rather than Java's
     * convention of milliseconds).
     */
    public long getEpoch()
    {
        return m_Timestamp.getTime() / 1000;
    }

    public Date getTimestamp()
    {
        return m_Timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        m_Timestamp = timestamp;
    }


    public double getRawAnomalyScore()
    {
        return m_RawAnomalyScore;
    }

    public void setRawAnomalyScore(double rawAnomalyScore)
    {
        m_RawAnomalyScore = rawAnomalyScore;
    }


    public double getAnomalyScore()
    {
        return m_AnomalyScore;
    }

    public void setAnomalyScore(double anomalyScore)
    {
        m_HadBigNormalisedUpdate |= AnomalyRecord.isBigUpdate(m_AnomalyScore, anomalyScore);
        m_AnomalyScore = anomalyScore;
    }

    public double getMaxNormalizedProbability()
    {
        return m_MaxNormalizedProbability;
    }

    public void setMaxNormalizedProbability(double maxNormalizedProbability)
    {
        m_HadBigNormalisedUpdate |= AnomalyRecord.isBigUpdate(m_MaxNormalizedProbability, maxNormalizedProbability);
        m_MaxNormalizedProbability = maxNormalizedProbability;
    }


    public int getRecordCount()
    {
        return m_RecordCount;
    }

    public void setRecordCount(int recordCount)
    {
        m_RecordCount = recordCount;
    }


    /**
     * Get the list of detectors that produced output in this bucket
     *
     * @return A list of detector
     */
    public List<Detector> getDetectors()
    {
        return m_Detectors;
    }

    public void setDetectors(List<Detector> detectors)
    {
        m_Detectors = detectors;
    }

    /**
     * Get all the anomaly records associated with this bucket
     * @return All the anomaly records
     */
    public List<AnomalyRecord> getRecords()
    {
        return m_Records;
    }

    public void setRecords(List<AnomalyRecord> records)
    {
        m_Records = records;
    }

    /**
     * The number of records (events) actually processed
     * in this bucket.
     * @return
     */
    public long getEventCount()
    {
        return m_EventCount;
    }

    public void setEventCount(long value)
    {
        m_EventCount = value;
    }

    @JsonProperty("isInterim")
    public Boolean isInterim()
    {
        return m_IsInterim;
    }

    @JsonProperty("isInterim")
    public void setInterim(Boolean isInterim)
    {
        m_IsInterim = isInterim;
    }

    @Override
    public int hashCode()
    {
        // m_HadBigNormalisedUpdate is deliberately excluded from the hash
        return Objects.hash(m_Timestamp, m_EventCount, m_RawAnomalyScore, m_AnomalyScore,
                m_MaxNormalizedProbability, m_RecordCount, m_Records, m_IsInterim);
    }


    /**
     * Compare all the fields and embedded anomaly records
     * (if any), does not compare detectors as they are not
     * serialized anyway.
     */
    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }

        if (other instanceof Bucket == false)
        {
            return false;
        }

        Bucket that = (Bucket)other;

        // m_HadBigNormalisedUpdate is deliberately excluded from the test
        boolean equals =
                (this.m_Timestamp.equals(that.m_Timestamp)) &&
                (this.m_EventCount == that.m_EventCount) &&
                (this.m_RawAnomalyScore == that.m_RawAnomalyScore) &&
                (this.m_AnomalyScore == that.m_AnomalyScore) &&
                (this.m_MaxNormalizedProbability == that.m_MaxNormalizedProbability) &&
                (this.m_RecordCount == that.m_RecordCount);

        // don't bother testing detectors
        if (this.m_Records == null && that.m_Records == null)
        {
            equals &= true;
        }
        else if (this.m_Records != null && that.m_Records != null)
        {
            equals &= this.m_Records.size() == that.m_Records.size();
            if (equals)
            {
                for (int i=0; i<this.m_Records.size(); i++)
                {
                    equals &= this.m_Records.get(i).equals(that.m_Records.get(i));
                }
            }
        }
        else
        {
            // one null the other not
            equals = false;
        }

        if (this.m_IsInterim == null && that.m_IsInterim == null)
        {
            equals &= true;
        }
        else if (this.m_IsInterim != null && that.m_IsInterim != null)
        {
            equals &= (this.m_IsInterim == that.m_IsInterim);
        }
        else
        {
            // one null the other not
            equals = false;
        }

        return equals;
    }


    public boolean hadBigNormalisedUpdate()
    {
        return m_HadBigNormalisedUpdate;
    }


    public void resetBigNormalisedUpdateFlag()
    {
        m_HadBigNormalisedUpdate = false;
    }
}
