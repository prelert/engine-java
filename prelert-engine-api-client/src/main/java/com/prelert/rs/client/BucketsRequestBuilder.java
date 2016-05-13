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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.prelert.job.results.Bucket;
import com.prelert.rs.data.Pagination;

public class BucketsRequestBuilder extends BaseJobRequestBuilder<Bucket>
{
    private final Map<String, String> m_Params;

    /**
     * @param client The engine API client
     * @param jobId The Job's unique Id
     */
    public BucketsRequestBuilder(EngineApiClient client, String jobId)
    {
        super(client, jobId);
        m_Params = new LinkedHashMap<>();
    }

    /**
     * Sets whether anomaly records should be in-lined with the results. Default is false.
     *
     * @param shouldExpand Should the buckets be expanded to contain the records or not
     * @return this {@code Builder} object
     */
    public BucketsRequestBuilder expand(boolean shouldExpand)
    {
        m_Params.put(EXPAND_QUERY_PARAM, Boolean.toString(shouldExpand));
        return this;
    }

    /**
     * Sets whether interim results are included in result. Default is false.
     *
     * @param includeInterim Should interim results be included or not
     * @return this {@code Builder} object
     */
    public BucketsRequestBuilder includeInterim(boolean includeInterim)
    {
        m_Params.put(INCLUDE_INTERIM_QUERY_PARAM, Boolean.toString(includeInterim));
        return this;
    }

    /**
     * Return only buckets with an anomalyScore &gt;= this value.
     *
     * @param value The anomaly score threshold
     * @return this {@code Builder} object
     */
    public BucketsRequestBuilder anomalyScoreThreshold(double value)
    {
        m_Params.put(Bucket.ANOMALY_SCORE, Double.toString(value));
        return this;
    }

    /**
     * Return only buckets with a maxNormalizedProbability &gt;= this value.
     *
     * @param value The normalized probability threshold
     * @return this {@code Builder} object
     */
    public BucketsRequestBuilder normalizedProbabilityThreshold(double value)
    {
        m_Params.put(Bucket.MAX_NORMALIZED_PROBABILITY, Double.toString(value));
        return this;
    }

    /**
     * Sets the number of buckets to skip. Default is 0.
     *
     * @param value The number of buckets to skip
     * @return this {@code Builder} object
     */
    public BucketsRequestBuilder skip(long value)
    {
        m_Params.put(SKIP_QUERY_PARAM, Long.toString(value));
        return this;
    }

    /**
     * Sets the max number of buckets to request. Default is 100.
     *
     * @param value The number of buckets to request
     * @return this {@code Builder} object
     */
    public BucketsRequestBuilder take(long value)
    {
        m_Params.put(TAKE_QUERY_PARAM, Long.toString(value));
        return this;
    }

    /**
     * Filters out buckets that start before the given value.
     * Value is expected in seconds from the Epoch.
     *
     * @param value The start date as seconds from the Epoch
     * @return this {@code Builder} object
     */
    public BucketsRequestBuilder start(long value)
    {
        m_Params.put(START_QUERY_PARAM, Long.toString(value));
        return this;
    }

    /**
     * Filters out buckets that start before the given value.
     * Value is expected as an ISO 8601 date String.
     *
     * @param value The start date as an ISO 8601 String
     * @return this {@code Builder} object
     * @throws UnsupportedEncodingException If UTF-8 not supported
     */
    public BucketsRequestBuilder start(String value) throws UnsupportedEncodingException
    {
        m_Params.put(START_QUERY_PARAM, URLEncoder.encode(value, UTF8));
        return this;
    }

    /**
     * Filters out buckets that start at or after the given value.
     * Value is expected in seconds from the Epoch.
     *
     * @param value The end date as seconds from the Epoch
     * @return this {@code Builder} object
     */
    public BucketsRequestBuilder end(long value)
    {
        m_Params.put(END_QUERY_PARAM, Long.toString(value));
        return this;
    }

    /**
     * Filters out buckets that start at or after the given value.
     * Value is expected as an ISO 8601 date String.
     *
     * @param value The end date as an ISO 8601 String
     *
     * @return this {@code Builder} object
     * @throws UnsupportedEncodingException If UTF-8 not supported
     */
    public BucketsRequestBuilder end(String value) throws UnsupportedEncodingException
    {
        m_Params.put(END_QUERY_PARAM, URLEncoder.encode(value, UTF8));
        return this;
    }

    /**
     * Returns the page with the buckets that were requested
     *
     * @return A {@link Pagination} object containing the resulted {@link Bucket} objects
     * @throws IOException If HTTP GET fails
     */
    public Pagination<Bucket> get() throws IOException
    {
        StringBuilder url = new StringBuilder();
        url.append(baseUrl()).append("/results/").append(jobId()).append("/buckets");
        appendParams(m_Params, url);
        return createHttpGetRequester().getPage(url.toString(),
                new TypeReference<Pagination<Bucket>>() {});
    }
}
