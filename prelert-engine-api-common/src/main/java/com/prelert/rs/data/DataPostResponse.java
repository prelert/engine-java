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

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.prelert.job.DataCounts;

/**
 * The result of a data upload is either an error
 * or an {@linkplain DataCounts} object. This class
 * encapsulates this with the Job Id.
 */
@JsonInclude(Include.NON_NULL)
public class DataPostResponse
{
    private String m_JobId;
    private DataCounts m_DataCounts;
    private ApiError m_Error;

    /**
     * For serialisation
     */
    public DataPostResponse()
    {
    }

    public DataPostResponse(String jobId, DataCounts counts)
    {
        m_JobId = jobId;
        m_DataCounts = counts;
    }

    public DataPostResponse(String jobId, ApiError error)
    {
        m_JobId = jobId;
        m_Error = error;
    }

    public String getJobId()
    {
        return m_JobId;
    }

    public void setJobId(String jobId)
    {
        this.m_JobId = jobId;
    }

    public DataCounts getUploadSummary()
    {
        return m_DataCounts;
    }

    public void setUploadSummary(DataCounts dataCounts)
    {
        this.m_DataCounts = dataCounts;
    }

    public ApiError getError()
    {
        return m_Error;
    }

    public void setError(ApiError error)
    {
        this.m_Error = error;
    }


    @Override
    public int hashCode()
    {
        return Objects.hash(m_DataCounts, m_Error, m_JobId);
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

        DataPostResponse other = (DataPostResponse) obj;

        return Objects.equals(this.m_DataCounts, other.m_DataCounts) &&
                   Objects.equals(this.m_JobId, other.m_JobId) &&
                   Objects.equals(this.m_Error, other.m_Error);
    }

}
