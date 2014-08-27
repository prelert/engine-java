/****************************************************************************
 *                                                                          *
 * Copyright 2014 Prelert Ltd                                               *
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

import com.fasterxml.jackson.core.io.JsonStringEncoder;



/**
 * Encapsulates the an API error condition.
 * The errorCode identifies the error type and the message 
 * provides further details. If the error was caused by 
 * a Java Exception {@linkplain #getCause()} will return that
 * Exception else it returns <code>null</code>. 
 * 
 * @see ErrorCode
 */
public class ApiError 
{
	private ErrorCode m_ErrorCode;
	private String m_Message;
	private Throwable m_Cause;
	
	/**
	 * Default cons for serialisation (Jackson)
	 */
	public ApiError()
	{

	}

	/**
	 * Create a new ApiError from one of the list of error codes.
	 * 
	 * @param errorCode
	 * @see ErrorCode 
	 */
	public ApiError(ErrorCode errorCode)
	{
		m_ErrorCode = errorCode;		
	}
	
	/**
	 * The error code
	 * @see ErrorCode
	 * @return one of {@linkplain ErrorCode}
	 */
	public ErrorCode getErrorCode()
	{
		return m_ErrorCode;
	}
	
	/**
	 * Set the error code.
	 * @see ErrorCode
	 * @param value
	 */
	public void setErrorCode(ErrorCode value)
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
	 * The exception that caused the error
	 * @return The exception that caused the error or <code>null</code> 
	 */
	public Throwable getCause()
	{
		return m_Cause;
	}
	
	/**
	 * Set the cause to the error
	 * @param e
	 */
	public void setCause(Throwable e)
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
}
