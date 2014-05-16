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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Generic wrapper class for returning a single document requested through
 * the REST API. If the requested document does not exist {@link #isExists()}
 * will be false and {@link #getDocument()} will return <code>null</code>.
 *
 * @param <T>
 */
@JsonPropertyOrder({"documentId", "exists", "type", "document"})
public class SingleDocument<T> 
{
	private boolean m_Exists;
	private String m_Type;
	private String m_Id;
	
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
		this.m_Exists = exists;
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
		this.m_Type = type;
	}

	/**
	 * The id of the requested document
	 * @return The document Id
	 */
	public String getDocumentId() 
	{
		return m_Id;
	}

	public void setDocumentId(String id) 
	{
		this.m_Id = id;
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
	 * @param doc
	 */
	public void setDocument(T doc)
	{		
		m_Document = doc;
		m_Exists = (doc != null) ? true : false;
	}
	
}
