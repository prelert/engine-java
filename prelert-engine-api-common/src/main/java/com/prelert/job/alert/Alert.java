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
package com.prelert.job.alert;

import java.net.URI;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.prelert.job.results.AnomalyRecord;
import com.prelert.job.results.Bucket;

/**
 * Encapsulate an Engine API alert. Alerts have:
 * <ol>
 *  <li>Job Id - The source of the alert</li>
 *  <li>Timestamp - The time of the alert </li>
 *  <li>Bucket - The bucket that caused the alert if the alert was based on
 *  anomaly score</li>
 *  <li>Records - The records that caused the alert if the alert was based on a
 *  normalized probability threshold</li>
 *  <li>Alert Type see {@linkplain AlertType} the default is {@linkplain AlertType#BUCKET}
 * </ol>
 */
@JsonInclude(Include.NON_NULL)
public class Alert
{
    public static final String TYPE = "alert";

    public static final String JOB_ID = "JobId";
    public static final String TIMESTAMP = "timestamp";
    public static final String URI = "uri";


    private String m_JobId;
    private Date m_Timestamp;
    private URI m_Uri;
    private double m_AnomalyScore;
    private double m_MaxNormalizedProb;
    private boolean m_IsTimeout;
    private AlertType m_AlertType;
    private Bucket m_Bucket;
    private List<AnomalyRecord> m_Records;
    private boolean m_IsInterim;

    public Alert()
    {
        m_AlertType = AlertType.BUCKET;
    }

    public String getJobId()
    {
        return m_JobId;
    }

    public void setJobId(String jobId)
    {
        this.m_JobId = jobId;
    }

    public Date getTimestamp()
    {
        return m_Timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        m_Timestamp = timestamp;
    }

    public double getAnomalyScore()
    {
        return m_AnomalyScore;
    }

    public void setAnomalyScore(double anomalyScore)
    {
        m_AnomalyScore = anomalyScore;
    }

    public double getMaxNormalizedProbability()
    {
        return m_MaxNormalizedProb;
    }

    public void setMaxNormalizedProbability(double prob)
    {
        m_MaxNormalizedProb = prob;
    }

    public URI getUri()
    {
        return m_Uri;
    }

    public void setUri(URI uri)
    {
        m_Uri = uri;
    }

    public boolean isTimeout()
    {
        return m_IsTimeout;
    }

    public void setTimeout(boolean timeout)
    {
        m_IsTimeout = timeout;
    }

    public Bucket getBucket()
    {
        return m_Bucket;
    }

    public void setBucket(Bucket bucket)
    {
        m_Bucket = bucket;
    }

    public List<AnomalyRecord> getRecords()
    {
        return m_Records;
    }

    public void setRecords(List<AnomalyRecord> records)
    {
        m_Records = records;
    }

    public AlertType getAlertType()
    {
        return m_AlertType;
    }

    public void setAlertType(AlertType value)
    {
        m_AlertType = value;
    }

    @JsonProperty("isInterim")
    public boolean isInterim()
    {
        return m_IsInterim;
    }

    @JsonProperty("isInterim")
    public void setInterim(boolean value)
    {
        m_IsInterim = value;
    }

}
