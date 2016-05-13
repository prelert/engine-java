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

import java.util.Objects;

public class SchedulerState
{
    public static final String TYPE = "schedulerState";

    public static final String START_TIME_MILLIS = "startTimeMillis";
    public static final String END_TIME_MILLIS = "endTimeMillis";

    private Long m_StartTimeMillis;
    private Long m_EndTimeMillis;

    public SchedulerState()
    {
        // Default constructor needed for serialization
    }

    public SchedulerState(Long startTimeMillis, Long endTimeMillis)
    {
        m_StartTimeMillis = startTimeMillis;
        m_EndTimeMillis = endTimeMillis;
    }

    public Long getStartTimeMillis()
    {
        return m_StartTimeMillis;
    }

    public void setStartTimeMillis(Long startTimeMillis)
    {
        m_StartTimeMillis = startTimeMillis;
    }

    public Long getEndTimeMillis()
    {
        return m_EndTimeMillis;
    }

    public void setEndTimeMillis(Long endTimeMillis)
    {
        m_EndTimeMillis = endTimeMillis;
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (other instanceof SchedulerState == false)
        {
            return false;
        }

        SchedulerState that = (SchedulerState) other;

        return Objects.equals(this.m_StartTimeMillis, that.m_StartTimeMillis) &&
                Objects.equals(this.m_EndTimeMillis, that.m_EndTimeMillis);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(m_StartTimeMillis, m_EndTimeMillis);
    }
}
