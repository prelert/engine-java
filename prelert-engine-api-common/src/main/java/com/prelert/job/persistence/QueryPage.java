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

package com.prelert.job.persistence;

import java.util.List;

/**
 * Generic wrapper class for a page of query results and the
 * total number of query hits.<br>
 * {@linkplain #hitCount()} is the total number of results
 * but that value may not be equal to the actual length of
 * the {@linkplain #queryResults()} list if skip & take or
 * some cursor was used in the database query.
 *
 * @param <T>
 */
public final class QueryPage<T>
{
    private final List<T> m_QueryResults;
    private final long m_HitCount;

    public QueryPage(List<T> queryResults, long hitCount)
    {
        m_QueryResults = queryResults;
        m_HitCount = hitCount;
    }

    public List<T> queryResults()
    {
        return m_QueryResults;
    }

    public long hitCount()
    {
        return m_HitCount;
    }
}
