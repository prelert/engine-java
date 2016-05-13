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
import com.prelert.job.results.AnomalyRecord;
import com.prelert.rs.data.Pagination;

public class RecordsRequestBuilder extends BaseJobRequestBuilder<AnomalyRecord>
{
    private final Map<String, String> m_Params;

    /**
     * @param client The Engine API client
     * @param jobId The Job's unique Id
     */
    public RecordsRequestBuilder(EngineApiClient client, String jobId)
    {
        super(client, jobId);
        m_Params = new LinkedHashMap<>();
    }

    /**
     * Sets whether interim results are included in result. Default is false.
     *
     * @param includeInterim Should interim results be included or not
     * @return this {@code Builder} object
     */
    public RecordsRequestBuilder includeInterim(boolean includeInterim)
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
    public RecordsRequestBuilder anomalyScoreThreshold(double value)
    {
        m_Params.put(AnomalyRecord.ANOMALY_SCORE, Double.toString(value));
        return this;
    }

    /**
     * Return only buckets with a maxNormalizedProbability &gt;= this value.
     *
     * @param value The normalized probability threshold
     * @return this {@code Builder} object
     */
    public RecordsRequestBuilder normalizedProbabilityThreshold(double value)
    {
        m_Params.put(AnomalyRecord.NORMALIZED_PROBABILITY, Double.toString(value));
        return this;
    }

    /**
     * Sets the number of records to skip. Default is 0.
     *
     * @param value The number of records to skip
     * @return this {@code Builder} object
     */
    public RecordsRequestBuilder skip(long value)
    {
        m_Params.put(SKIP_QUERY_PARAM, Long.toString(value));
        return this;
    }

    /**
     * Sets the max number of buckets to request. Default is 100.
     *
     * @param value The number of records to request
     * @return this {@code Builder} object
     */
    public RecordsRequestBuilder take(long value)
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
    public RecordsRequestBuilder start(long value)
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
    public RecordsRequestBuilder start(String value) throws UnsupportedEncodingException
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
    public RecordsRequestBuilder end(long value)
    {
        m_Params.put(END_QUERY_PARAM, Long.toString(value));
        return this;
    }

    /**
     * Filters out buckets that start at or after the given value.
     * Value is expected as an ISO 8601 date String.
     *
     * @param value The end date as an ISO 8601 String
     * @return this {@code Builder} object
     * @throws UnsupportedEncodingException If UTF-8 not supported
     */
    public RecordsRequestBuilder end(String value) throws UnsupportedEncodingException
    {
        m_Params.put(END_QUERY_PARAM, URLEncoder.encode(value, UTF8));
        return this;
    }

    /**
     * Sets the field to sort by
     *
     * @param field The field to sort by
     * @return this {@code Builder} object
     */
    public RecordsRequestBuilder sortField(String field)
    {
        m_Params.put(SORT_QUERY_PARAM, field);
        return this;
    }

    /**
     * Sets whether the sorting order is descending
     *
     * @param descending Should the sorting order be descending or not
     * @return this {@code Builder} object
     */
    public RecordsRequestBuilder descending(boolean descending)
    {
        m_Params.put(DESCENDING_ORDER, Boolean.toString(descending));
        return this;
    }

    /**
     * Returns the page with the records that were requested
     *
     * @return A {@link Pagination} object containing the resulted {@link AnomalyRecord} objects
     * @throws IOException If HTTP GET fails
     */
    public Pagination<AnomalyRecord> get() throws IOException
    {
        StringBuilder url = new StringBuilder();
        url.append(baseUrl()).append("/results/").append(jobId()).append("/records");
        appendParams(m_Params, url);
        return createHttpGetRequester().getPage(url.toString(),
                new TypeReference<Pagination<AnomalyRecord>>() {});
    }
}
