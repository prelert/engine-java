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
import com.prelert.job.errorcodes.HasErrorCode;

/**
 * General job exception class with a specific error code and message.
 */
public class JobException extends Exception implements HasErrorCode
{
    private static final long serialVersionUID = -5289885963015348819L;

    private final ErrorCodes m_ErrorCode;

    public JobException(String message, ErrorCodes errorCode)
    {
        super(message);
        m_ErrorCode = errorCode;
    }

    public JobException(String message, ErrorCodes errorCode, Throwable cause)
    {
        super(message, cause);
        m_ErrorCode = errorCode;
    }

    @Override
    public ErrorCodes getErrorCode()
    {
        return m_ErrorCode;
    }
}
