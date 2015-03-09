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

package com.prelert.job;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents an API data transform
 */
public class TransformConfig
{
    // Serialisation strings
    public static final String TYPE = "transform";
    public static final String TRANSFORM = "transform";
    public static final String INPUTS = "inputs";
    public static final String OUTPUTS = "outputs";


    private List<String> m_Inputs;
    private String m_Name;
    private List<String> m_Outputs;
    private TransformType m_Type;

    public TransformConfig()
    {
    }

    public List<String> getInputs()
    {
        return m_Inputs;
    }

    public void setInputs(List<String> fields)
    {
        m_Inputs = fields;
    }

    public String getTransform()
    {
        return m_Name;
    }

    public void setTransform(String type)
    {
        m_Name = type;
    }

    public List<String> getOutputs()
    {
        if (m_Outputs == null || m_Outputs.isEmpty())
        {
            try
            {
                m_Outputs = type().defaultOutputNames();
            }
            catch (IllegalArgumentException e)
            {
                m_Outputs = Collections.emptyList();
            }
        }

        return m_Outputs;
    }

    public void setOutputs(List<String> outputs)
    {
        m_Outputs = outputs;
    }

    /**
     * This field shouldn't be serialised as its created dynamically
     * Type may be null when the class is constructed.
     * @return
     * @throws IllegalArgumentException if the type name is not recognized.
     */
    public TransformType type()
    {
        if (m_Type == null)
        {
            m_Type = TransformType.fromString(m_Name);
        }

        return m_Type;
    }

    @Override
    public String toString()
    {
        return m_Name;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(m_Inputs, m_Name, m_Outputs, m_Type);
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

        TransformConfig other = (TransformConfig) obj;

        return Objects.equals(this.m_Inputs, other.m_Inputs)
                && Objects.equals(this.m_Name, other.m_Name)
                && Objects.equals(this.m_Outputs, other.m_Outputs)
                && Objects.equals(this.m_Type, other.m_Type);
    }
}
