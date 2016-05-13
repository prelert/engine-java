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

import com.fasterxml.jackson.core.type.TypeReference;
import com.prelert.job.results.CategoryDefinition;
import com.prelert.rs.data.SingleDocument;

public class CategoryDefinitionRequestBuilder extends BaseJobRequestBuilder<CategoryDefinition>
{
    private final String m_CategoryId;

    /**
     * @param client The engine API client
     * @param jobId The Job's unique Id
     * @param categoryId The category's unique ID
     */
    public CategoryDefinitionRequestBuilder(EngineApiClient client, String jobId, String categoryId)
    {
        super(client, jobId);
        m_CategoryId = categoryId;
    }

    /**
     * Returns a single document with the category definition that was requested
     *
     * @return A {@link SingleDocument} object containing the requested {@link CategoryDefinition}
     * object
     * @throws IOException If HTTP GET fails
     */
    public SingleDocument<CategoryDefinition> get() throws IOException
    {
        StringBuilder url = new StringBuilder();
        url.append(baseUrl()).append("/results/").append(jobId()).append("/categorydefinitions/")
                .append(m_CategoryId);
        return createHttpGetRequester().getSingleDocument(url.toString(),
                new TypeReference<SingleDocument<CategoryDefinition>>() {});
    }
}
