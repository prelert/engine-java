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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Bucket Result POJO
 */
@JsonIgnoreProperties({"epoch", "normalisable", "id"})
@JsonInclude(Include.NON_NULL)
public class Bucket
{
    /*
     * Field Names
     */
    public static final String TIMESTAMP = "timestamp";
    public static final String ANOMALY_SCORE = "anomalyScore";
    public static final String INITIAL_ANOMALY_SCORE = "initialAnomalyScore";
    public static final String MAX_NORMALIZED_PROBABILITY = "maxNormalizedProbability";
    public static final String IS_INTERIM = "isInterim";
    public static final String RECORD_COUNT = "recordCount";
    public static final String EVENT_COUNT = "eventCount";
    public static final String RECORDS = "records";
    public static final String BUCKET_INFLUENCERS = "bucketInfluencers";
    public static final String INFLUENCERS = "influencers";
    public static final String BUCKET_SPAN = "bucketSpan";

    /**
     * Elasticsearch type
     */
    public static final String TYPE = "bucket";

    private String m_Id;
    private Date m_Timestamp;
    private double m_AnomalyScore;
    private long m_BucketSpan;

    private double m_InitialAnomalyScore;

    private double m_MaxNormalizedProbability;
    private int m_RecordCount;
    private List<AnomalyRecord> m_Records;
    private long m_EventCount;
    private boolean m_IsInterim;
    private boolean m_HadBigNormalisedUpdate;
    private List<BucketInfluencer> m_BucketInfluencers;
    private List<Influencer> m_Influencers;

    public Bucket()
    {
        m_Records = Collections.emptyList();
        m_Influencers = Collections.emptyList();
        m_BucketInfluencers = new ArrayList<>();
    }

    public String getId()
    {
        return m_Id;
    }

    public void setId(String id)
    {
        m_Id = id;
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

    /**
     * Bucketspan expressed in seconds
     */
    public long getBucketSpan()
    {
        return m_BucketSpan;
    }

    /**
     * Bucketspan expressed in seconds
     */
    public void setBucketSpan(long bucketSpan)
    {
        m_BucketSpan = bucketSpan;
    }

    public double getAnomalyScore()
    {
        return m_AnomalyScore;
    }

    public void setAnomalyScore(double anomalyScore)
    {
        m_AnomalyScore = anomalyScore;
    }

    public double getInitialAnomalyScore()
    {
        return m_InitialAnomalyScore;
    }

    public void setInitialAnomalyScore(double influenceScore)
    {
        this.m_InitialAnomalyScore = influenceScore;
    }

    public double getMaxNormalizedProbability()
    {
        return m_MaxNormalizedProbability;
    }

    public void setMaxNormalizedProbability(double maxNormalizedProbability)
    {
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
    public boolean isInterim()
    {
        return m_IsInterim;
    }

    @JsonProperty("isInterim")
    public void setInterim(boolean isInterim)
    {
        m_IsInterim = isInterim;
    }

    public List<Influencer> getInfluencers()
    {
        return m_Influencers;
    }

    public void setInfluencers(List<Influencer> influences)
    {
        this.m_Influencers = influences;
    }

    public List<BucketInfluencer> getBucketInfluencers()
    {
        return m_BucketInfluencers;
    }

    public void setBucketInfluencers(List<BucketInfluencer> bucketInfluencers)
    {
        m_BucketInfluencers = bucketInfluencers;
    }

    public void addBucketInfluencer(BucketInfluencer bucketInfluencer)
    {
        if (m_BucketInfluencers == null)
        {
            m_BucketInfluencers = new ArrayList<>();
        }
        m_BucketInfluencers.add(bucketInfluencer);
    }

    @Override
    public int hashCode()
    {
        // m_HadBigNormalisedUpdate is deliberately excluded from the hash
        // as is m_Id, which is generated by the datastore
        return Objects.hash(m_Timestamp, m_EventCount, m_InitialAnomalyScore, m_AnomalyScore,
                m_MaxNormalizedProbability, m_RecordCount, m_Records, m_IsInterim, m_BucketSpan,
                m_BucketInfluencers, m_Influencers);
    }

    /**
     * Compare all the fields and embedded anomaly records (if any)
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
        // as is m_Id, which is generated by the datastore
        return Objects.equals(this.m_Timestamp, that.m_Timestamp)
                && (this.m_EventCount == that.m_EventCount)
                && (this.m_BucketSpan == that.m_BucketSpan)
                && (this.m_AnomalyScore == that.m_AnomalyScore)
                && (this.m_InitialAnomalyScore == that.m_InitialAnomalyScore)
                && (this.m_MaxNormalizedProbability == that.m_MaxNormalizedProbability)
                && (this.m_RecordCount == that.m_RecordCount)
                && Objects.equals(this.m_Records, that.m_Records)
                && Objects.equals(this.m_IsInterim, that.m_IsInterim)
                && Objects.equals(this.m_BucketInfluencers, that.m_BucketInfluencers)
                && Objects.equals(this.m_Influencers, that.m_Influencers);
    }

    public boolean hadBigNormalisedUpdate()
    {
        return m_HadBigNormalisedUpdate;
    }

    public void resetBigNormalisedUpdateFlag()
    {
        m_HadBigNormalisedUpdate = false;
    }

    public void raiseBigNormalisedUpdateFlag()
    {
        m_HadBigNormalisedUpdate = true;
    }

    /**
     * This method encapsulated the logic for whether a bucket should
     * be normalised. The decision depends on two factors.
     *
     * The first is whether the bucket has bucket influencers.
     * Since bucket influencers were introduced, every bucket must have
     * at least one bucket influencer. If it does not, it means it is
     * a bucket persisted with an older version and should not be
     * normalised.
     *
     * The second factor has to do with minimising the number of buckets
     * that are sent for normalisation. Buckets that have no records
     * and a score of zero should not be normalised as their score
     * will not change and they will just add overhead.
     *
     * @return true if the bucket should be normalised or false otherwise
     */
    public boolean isNormalisable()
    {
        if (m_BucketInfluencers == null || m_BucketInfluencers.isEmpty())
        {
            return false;
        }
        return m_AnomalyScore > 0.0 || m_RecordCount > 0;
    }
}
