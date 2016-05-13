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

package com.prelert.rs.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.prelert.job.alert.Alert;
import com.prelert.rs.data.SingleDocument;

/**
 * Build a blocking Long poll alert for the job.
 * Blocks until the alert occurs or the timeout period expires.
 */
public class AlertRequestBuilder extends BaseJobRequestBuilder<Alert>
{
    public static final String TIMEOUT = "timeout";
    public static final String SCORE = "score";
    public static final String PROBABILITY = "probability";
    public static final String ENDPOINT = "/alerts_longpoll/";

    public static final String BUCKET = "bucket";
    public static final String BUCKET_INFLUENCER = "bucketinfluencer";
    public static final String INFLUENCER = "influencer";

    public static final String INCLUDE_INTERIM = "includeInterim";

    public static final String ALERT_ON = "alertOn";

    private Map<String, String> m_Params;

    public AlertRequestBuilder(EngineApiClient client, String jobId)
    {
        super(client, jobId);
        m_Params = new HashMap<>();
    }


    /**
     * Set the timeout period for the request
     *
     * @param seconds Timeout the request after this many seconds.
     * @return this {@code Builder} object
     */
    public AlertRequestBuilder timeout(long seconds)
    {
        m_Params.put(TIMEOUT, Long.toString(seconds));
        return this;
    }

    /**
     * Set the anomaly score threshold and alert if a record
     * has an anomalyScore threshold &gt;= <code>threshold</code>
     *
     * @param threshold This must be in the range 0-100
     * @return this {@code Builder} object
     */
    public AlertRequestBuilder score(double threshold)
    {
        m_Params.put(SCORE, Double.toString(threshold));
        return this;
    }

    /**
     * Set the max normalised probability threshold and alert if
     * a bucket's maxNormalizedProbability is &gt;= <code>threshold</code>
     *
     * @param threshold This must be in the range 0-100
     * @return this {@code Builder} object
     */
    public AlertRequestBuilder probability(double threshold)
    {
        m_Params.put(PROBABILITY, Double.toString(threshold));
        return this;
    }

    /**
     * Alert on interim and full results
     * @return this {@code Builder} object
     */
    public AlertRequestBuilder includeInterim()
    {
        m_Params.put(INCLUDE_INTERIM, Boolean.toString(true));
        return this;
    }

    /**
     * Alert on bucket results
     * @return this {@code Builder} object
     */
    public AlertRequestBuilder alertOnBuckets()
    {
        addAlertType(BUCKET);
        return this;
    }

    /**
     * Alert on influencer
     * @return this {@code Builder} object
     */
    public AlertRequestBuilder alertOnInfluencers()
    {
        addAlertType(INFLUENCER);
        return this;
    }

    /**
     * Alert on bucket influencer
     * @return this {@code Builder} object
     */
    public AlertRequestBuilder alertOnBucketInfluencers()
    {
        addAlertType(BUCKET_INFLUENCER);
        return this;
    }

    /**
     * Returns a single document with the alert that was requested
     *
     * @return A {@link SingleDocument} object containing the requested {@link Alert} object
     * @throws IOException If HTTP GET fails
     */
    public Alert get()
    throws IOException
    {
        StringBuilder url = new StringBuilder();
        url.append(baseUrl()).append(ENDPOINT).append(jobId());
        appendParams(m_Params, url);

        return createHttpGetRequester().get(url.toString(), new TypeReference<Alert>() {});
    }

    private void addAlertType(String type)
    {
        if (m_Params.containsKey(ALERT_ON))
        {
            type = m_Params.get(ALERT_ON) + "," + type;
        }

        m_Params.put(ALERT_ON, type);
    }

}
