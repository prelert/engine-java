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

package com.prelert.job;

import com.prelert.job.errorcodes.ErrorCodes;
import com.prelert.job.messages.Messages;


/**
 * Job ids (names) must be unique no 2 jobs can have the same id.
 */
public class JobIdAlreadyExistsException extends JobException
{
    private static final long serialVersionUID = 8656604180755905746L;

    private final String m_JobId;

    /**
     * Create a new JobIdAlreadyExistsException with the error code
     * and Id (job name)
     *
     * @param jobId The Job Id that could not be found
     */
    public JobIdAlreadyExistsException(String jobId)
    {
        super(Messages.getMessage(Messages.JOB_CONFIG_ID_ALREADY_TAKEN, jobId),
                ErrorCodes.JOB_ID_TAKEN);
        m_JobId = jobId;
    }

    public String getAlias()
    {
        return m_JobId;
    }
}
