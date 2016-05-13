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

/**
 * The acknowledgement message for the REST API.
 * Operations such as delete that don't return data
 * should return this.
 */
public class Acknowledgement
{
    private boolean m_Ack;

    /**
     * Default is true
     */
    public Acknowledgement()
    {
        m_Ack = true;
    }

    public Acknowledgement(boolean ack)
    {
        m_Ack = ack;
    }

    /**
     * Get the acknowledgement value.
     * @return true
     */
    public boolean getAcknowledgement()
    {
        return m_Ack;
    }

    /**
     * Set the acknowledgement value.
     * @param value
     */
    public void setAcknowledgement(boolean value)
    {
        m_Ack = value;
    }
}
