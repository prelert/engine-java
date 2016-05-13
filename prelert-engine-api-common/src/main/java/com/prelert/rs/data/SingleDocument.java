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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Generic wrapper class for returning a single document requested through
 * the REST API. If the requested document does not exist {@link #isExists()}
 * will be false and {@link #getDocument()} will return <code>null</code>.
 *
 * @param <T> the requested document type
 */
@JsonPropertyOrder({"exists", "type", "document"})
@JsonInclude(Include.NON_NULL)
public class SingleDocument<T>
{
    private boolean m_Exists;
    private String m_Type;

    private T m_Document;

    /**
     * Return true if the requested document exists
     * @return true is document exists
     */
    public boolean isExists()
    {
        return m_Exists;
    }

    public void setExists(boolean exists)
    {
        m_Exists = exists;
    }

    /**
     * The type of the requested document
     * @return The document type
     */
    public String getType()
    {
        return m_Type;
    }

    public void setType(String type)
    {
        m_Type = type;
    }

    /**
     * Get the requested document or null
     * @return The document or <code>null</code>
     */
    public T getDocument()
    {
        return m_Document;
    }

    /**
     * Set the requested document.
     * If the doc is non-null {@link #isExists() Exists} is set to true
     * else it is false
     * @param doc the requested document
     */
    public void setDocument(T doc)
    {
        m_Document = doc;
        m_Exists = (doc != null) ? true : false;
    }
}
