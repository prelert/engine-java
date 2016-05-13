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
import java.util.Collections;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.prelert.rs.data.Pagination;
import com.prelert.rs.data.SingleDocument;

class HttpGetRequester<T>
{
    private static final Logger LOGGER = Logger.getLogger(HttpGetRequester.class);

    private final EngineApiClient m_Client;

    public HttpGetRequester(EngineApiClient client)
    {
        m_Client = client;
    }

    protected Pagination<T> getPage(String fullUrl, TypeReference<Pagination<T>> typeRef)
            throws IOException
    {
        LOGGER.debug("GET " + fullUrl);

        Pagination<T> page = m_Client.get(fullUrl, typeRef);

        // else return empty page
        if (page == null)
        {
            page = new Pagination<>();
            page.setDocuments(Collections.emptyList());
        }

        return page;
    }

    protected SingleDocument<T> getSingleDocument(String fullUrl,
            TypeReference<SingleDocument<T>> typeRef) throws IOException
    {
        LOGGER.debug("GET " + fullUrl);

        SingleDocument<T> doc = m_Client.get(fullUrl, typeRef);

        // If no doc was returned return an empty one
        return doc == null ? new SingleDocument<>() : doc;
    }

    protected T get(String fullUrl, TypeReference<T> typeRef)
    throws IOException
    {
        LOGGER.debug("GET " + fullUrl);

        T doc = m_Client.get(fullUrl, typeRef, true);

        return doc;
    }
}
