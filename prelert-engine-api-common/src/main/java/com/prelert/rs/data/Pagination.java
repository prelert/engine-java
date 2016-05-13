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

package com.prelert.rs.data;

import java.net.URI;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Generic wrapper class for results returned by the RESTful API.
 *
 * The API automatically pages results if more than the <code>skip</code>
 * query parameter are returned. If this is not the last page of results
 * {@link #getNextPage()} will return a non <code>null</code> value
 * that is the link to the next page of results. Similarly if this is not the
 * first page of results {@link #getPreviousPage()} will return a non
 * <code>null</code> value. {@link #getDocuments()} Returns the actual list
 * of requested documents the size of that list will always be &lt;= {@link #getTake()}
 * <br>
 * Skip and Take are set to the argument values used in the query.
 *
 * @param <T> The type of the result
 */
@JsonPropertyOrder({"hitCount", "skip", "take", "nextPage", "previousPage", "documents"})
@JsonIgnoreProperties({"documentCount"})
public class Pagination<T>
{
    private long m_HitCount;

    private int m_Skip;
    private int m_Take;

    private URI m_NextPage;
    private URI m_PreviousPage;

    private List<T> m_Documents;

    public Pagination()
    {
    }

    /**
     * The number of hits in the request. This does not
     * necessarily match the length of the number of documents returned
     * if paging is in action.
     * @return The number of search hits
     */
    public long getHitCount()
    {
        return m_HitCount;
    }

    public void setHitCount(long hitCount)
    {
        m_HitCount = hitCount;
    }

    /**
     * If the results are paged this is the starting point of that page.
     *
     * @return The skip query parameter used in the query
     */
    public int getSkip()
    {
        return m_Skip;
    }

    public void setSkip(int skip)
    {
        m_Skip = skip;
    }

    /**
     * The number of documents requested this value can be greater than
     * the number actually returned.
     *
     * @return The take query parameter used in the query
     */
    public int getTake()
    {
        return m_Take;
    }

    public void setTake(int take)
    {
        m_Take = take;
    }

    /**
     * If there is another page of results then this URI points to that page.
     * @return The next page or <code>null</code>
     */
    public URI getNextPage()
    {
        return m_NextPage;
    }

    public void setNextPage(URI nextPage)
    {
        m_NextPage = nextPage;
    }

    /**
     * If there is a previous page of results then this URI points to that page.
     * @return The previous page or <code>null</code>
     */
    public URI getPreviousPage()
    {
        return m_PreviousPage;
    }

    public void setPreviousPage(URI previousPage)
    {
        m_PreviousPage = previousPage;
    }

    /**
     * The documents.
     * @return The list of documents or <code>null</code> if
     * {@link #getHitCount()} == 0
     */
    public List<T> getDocuments()
    {
        return m_Documents;
    }

    public void setDocuments(List<T> documents)
    {
        m_Documents = documents;
    }

    /**
     * The number of documents returned in this document page.
     * This property calculated on the fly so shouldn't be serialised
     * @return The number of documents in this page
     */
    public int getDocumentCount()
    {
        return (m_Documents == null) ? 0 : m_Documents.size();
    }

    /**
     * True if all results are return i.e. <code>documents.size() == hitcount</code>
     * and there is no next page or previous page
     * If the object contains 0 documents it returns false.
     *
     * @return True if all the requested documents are returned in this page
     */
    @JsonIgnore()
    public boolean isAllResults()
    {
        return (m_Documents == null) ? false : m_Documents.size() == m_HitCount;
    }
}
