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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * List of {@linkplain DataPostResponse}
 */
@JsonIgnoreProperties("anErrorOccurred")
@JsonInclude(Include.NON_NULL)
public class MultiDataPostResult
{
    private List<DataPostResponse> m_Responses;

    public MultiDataPostResult()
    {
        m_Responses = new ArrayList<DataPostResponse>();
    }

    public List<DataPostResponse> getResponses()
    {
        return m_Responses;
    }

    public void setResponses(List<DataPostResponse> results)
    {
        m_Responses = results;
    }

    public void addResult(DataPostResponse result)
    {
        m_Responses.add(result);
    }

    /**
     * @return true if any of the uploads failed.
     */
    public boolean anErrorOccurred()
    {
        for (DataPostResponse response : m_Responses)
        {
            if (response.getError() != null)
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(m_Responses);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        MultiDataPostResult other = (MultiDataPostResult) obj;

        return Objects.equals(this.m_Responses, other.m_Responses);
    }
}
