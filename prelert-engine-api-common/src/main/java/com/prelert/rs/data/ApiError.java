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
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.prelert.job.errorcodes.ErrorCodes;
import com.prelert.job.errorcodes.HasErrorCode;


/**
 * Encapsulates the an API error condition.
 * The errorCode identifies the error type and the message
 * provides further details. If the error was caused by
 * a Java Exception {@linkplain #getCause()} will return that
 * exception's error message else <code>null</code>.
 *
 * Note that the cause exception's message is used not the
 * actual exception this is due to problems serialising the
 * exceptions.
 *
 * @see ErrorCodes
 */
@JsonInclude(Include.NON_NULL)
public class ApiError implements HasErrorCode
{
    private volatile ErrorCodes m_ErrorCode;
    private volatile String m_Message;
    private volatile String m_Cause;

    /**
     * Default cons for serialization (Jackson)
     */
    public ApiError()
    {
        // Default constructor
    }

    /**
     * Create a new ApiError from one of the list of error codes.
     *
     * @param errorCode The error code
     * @see ErrorCodes
     */
    public ApiError(ErrorCodes errorCode)
    {
        m_ErrorCode = errorCode;
    }

    /**
     * The error code
     * @see ErrorCodes
     * @return one of {@linkplain ErrorCodes}
     */
    @Override
    public ErrorCodes getErrorCode()
    {
        return m_ErrorCode;
    }

    /**
     * Set the error code.
     * @see ErrorCodes
     * @param value
     */
    public void setErrorCode(ErrorCodes value)
    {
        m_ErrorCode = value;
    }

    /**
     * The error message
     * @return The error string
     */
    public String getMessage()
    {
        return m_Message;
    }

    /**
     * Set the error message
     * @param message
     */
    public void setMessage(String message)
    {
        m_Message = message;
    }

    /**
     * The message from the exception that caused the error
     * @return The cause exception message or <code>null</code>
     */
    public String getCause()
    {
        return m_Cause;
    }

    /**
     * Set the error message of the cause exception
     * @param e
     */
    public void setCause(String e)
    {
        m_Cause = e;
    }

    /**
     * JSON representation of this object.
     * If cause is null then it is not written and
     * if errorCode <= 0 then it is not written.
     * @return JSON string
     */
    public String toJson()
    {
        JsonStringEncoder encoder = JsonStringEncoder.getInstance();

        StringBuilder builder = new StringBuilder();
        builder.append('{');

        boolean needComma = true;
        if (m_Message != null)
        {
            char [] message = encoder.quoteAsString(m_Message.toString());
            builder.append("\n  \"message\" : \"").append(message).append('"').append(',');
            needComma = false;
        }

        if (m_ErrorCode != null)
        {
            builder.append("\n  \"errorCode\" : ").append(m_ErrorCode.getValueString());
            needComma = true;
        }

        if (m_Cause != null)
        {
            if (needComma)
            {
                builder.append(',');
            }
            char [] cause = encoder.quoteAsString(m_Cause.toString());
            builder.append("\n  \"cause\" : \"").append(cause).append('"');
        }

        builder.append("\n}\n");

        return builder.toString();
    }

    @Override
    public int hashCode()
    {
        return this.toJson().hashCode();
    }

    /**
     * Throwable does not implement toString() so as the method is mainly
     * for testing compare the toJson() result
     */
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

        ApiError other = (ApiError) obj;

        return Objects.equals(this.toJson(), other.toJson());
    }
}
