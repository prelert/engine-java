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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

class BaseJobRequestBuilder<T>
{
    public static final String START_QUERY_PARAM = "start";
    public static final String END_QUERY_PARAM = "end";
    public static final String SORT_QUERY_PARAM = "sort";
    public static final String DESCENDING_ORDER = "desc";
    public static final String SKIP_QUERY_PARAM = "skip";
    public static final String TAKE_QUERY_PARAM = "take";
    public static final String INCLUDE_INTERIM_QUERY_PARAM = "includeInterim";
    public static final String EXPAND_QUERY_PARAM = "expand";
    protected static final String UTF8 = "UTF-8";


    private final EngineApiClient m_Client;
    private final String m_JobId;

    /**
     * @param client The Engine API client
     * @param jobId The Job's unique Id
     */
    public BaseJobRequestBuilder(EngineApiClient client, String jobId)
    {
        m_Client = client;
        m_JobId = jobId;
    }

    protected String baseUrl()
    {
        return m_Client.getBaseUrl();
    }

    protected String jobId()
    {
        return m_JobId;
    }

    protected HttpGetRequester<T> createHttpGetRequester()
    {
        return new HttpGetRequester<>(m_Client);
    }

    protected static void appendParams(Map<String, String> params, StringBuilder url)
    {
        if (!params.isEmpty())
        {
            List<String> paramPairs = new ArrayList<>();
            params.forEach((key, value) -> paramPairs.add(key + "=" + value));
            url.append('?');

            StringJoiner joiner = new StringJoiner("&");
            paramPairs.forEach(pair -> joiner.add(pair));
            url.append(joiner.toString());
        }
    }
}
