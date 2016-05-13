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
package com.prelert.job.results;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Influence field name and list of influence field values/score pairs
 */
public class Influence
{
    /**
     * Note all publicly exposed field names are "influencer" not "influence"
     */
    public static final String INFLUENCER_FIELD_NAME = "influencerFieldName";
    public static final String INFLUENCER_FIELD_VALUES = "influencerFieldValues";


    private String m_Field;
    private List<String> m_FieldValues;

    public Influence()
    {
        m_FieldValues = new ArrayList<String>();
    }

    public Influence(String field)
    {
        this();
        m_Field = field;
    }

    public String getInfluencerFieldName()
    {
        return m_Field;
    }

    public void setInfluencerFieldName(String field)
    {
        this.m_Field = field;
    }

    public List<String> getInfluencerFieldValues()
    {
        return m_FieldValues;
    }

    public void setInfluencerFieldValues(List<String> values)
    {
        this.m_FieldValues = values;
    }

    public void addInfluenceFieldValue(String value)
    {
        m_FieldValues.add(value);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(m_Field, m_FieldValues);
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

        Influence other = (Influence) obj;

        return Objects.equals(m_Field, other.m_Field) &&
                Objects.equals(m_FieldValues, other.m_FieldValues);
    }
}
