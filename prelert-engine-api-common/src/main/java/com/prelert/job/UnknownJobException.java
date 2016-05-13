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
 * This type of exception represents an error where
 * an operation uses a <i>JobId</i> that does not exist.
 */
public class UnknownJobException extends JobException
{
    private static final long serialVersionUID = 8603362038035845948L;

    private final String m_JobId;

    /**
     * Create with the default message and error code
     * set to ErrorCode.MISSING_JOB_ERROR
     * @param jobId
     */
    public UnknownJobException(String jobId)
    {
        super(Messages.getMessage(Messages.JOB_UNKNOWN_ID, jobId), ErrorCodes.MISSING_JOB_ERROR);
        m_JobId = jobId;
    }

    /**
     * Create a new UnknownJobException with an error code
     *
     * @param jobId The Job Id that could not be found
     * @param message Details of error explaining the context
     * @param errorCode
     */
    public UnknownJobException(String jobId, String message, ErrorCodes errorCode)
    {
        super(message, errorCode);
        m_JobId = jobId;
    }

    public UnknownJobException(String jobId, String message, ErrorCodes errorCode,
            Throwable cause)
    {
        super(message, errorCode, cause);
        m_JobId = jobId;
    }

    /**
     * Get the unknown <i>JobId</i> that was the source of the error.
     * @return
     */
    public String getJobId()
    {
        return m_JobId;
    }
}
