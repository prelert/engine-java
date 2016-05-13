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
package com.prelert.job.transform;

import java.util.Objects;

/**
 * The Transform condition class.
 * Some transforms should only be applied if a condition
 * is met. One example is exclude a record if a value is
 * greater than some numeric constant.
 * The {@linkplain Operator} enum defines the available
 * comparisons a condition can use.
 */
public class Condition
{
    private Operator m_Op;
    private String m_FilterValue;

    /**
     * Operation defaults to {@linkplain Operator#NONE}
     * and the filter is an empty string
     * @param
     */
    public Condition()
    {
        m_Op = Operator.NONE;
    }

    public Condition(Operator op, String filterString)
    {
        m_Op = op;
        m_FilterValue = filterString;
    }

    public Operator getOperator()
    {
        return m_Op;
    }

    public void setOperator(Operator op)
    {
        m_Op = op;
    }

    public String getValue()
    {
        return m_FilterValue;
    }

    public void setValue(String value)
    {
        m_FilterValue = value;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((m_FilterValue == null) ? 0 : m_FilterValue.hashCode());
        result = prime * result + ((m_Op == null) ? 0 : m_Op.hashCode());
        return result;
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

        Condition other = (Condition) obj;
        return Objects.equals(this.m_Op, other.m_Op) &&
                    Objects.equals(this.m_FilterValue, other.m_FilterValue);
    }
}
