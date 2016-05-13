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
import com.prelert.job.results.Bucket;
import com.prelert.rs.data.SingleDocument;

public class BucketRequestBuilder extends BaseJobRequestBuilder<Bucket>
{
    private final String m_BucketTimestamp;

    private final Map<String, String> m_Params;

    /**
     * @param client The Engine API client
     * @param jobId The Job's unique Id
     * @param bucketTimestamp The timestamp of the requested bucket
     */
    public BucketRequestBuilder(EngineApiClient client, String jobId, String bucketTimestamp)
    {
        super(client, jobId);
        m_Params = new HashMap<>();
        m_BucketTimestamp = bucketTimestamp;
    }

    /**
     * Sets whether anomaly records should be in-lined with the results. Default is false.
     *
     * @param shouldExpand Should the buckets be expanded to contain the records or not
     * @return this {@code Builder} object
     */
    public BucketRequestBuilder expand(boolean shouldExpand)
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
    public BucketRequestBuilder includeInterim(boolean includeInterim)
    {
        m_Params.put(INCLUDE_INTERIM_QUERY_PARAM, Boolean.toString(includeInterim));
        return this;
    }

    /**
     * Returns a single document with the bucket that was requested
     *
     * @return A {@link SingleDocument} object containing the requested {@link Bucket} object
     * @throws IOException If HTTP GET fails
     */
    public SingleDocument<Bucket> get() throws IOException
    {
        StringBuilder url = new StringBuilder();
        url.append(baseUrl()).append("/results/").append(jobId()).append("/buckets/").append(m_BucketTimestamp);
        appendParams(m_Params, url);
        return createHttpGetRequester().getSingleDocument(url.toString(),
                new TypeReference<SingleDocument<Bucket>>() {});
    }
}
