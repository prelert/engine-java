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

package com.prelert.job.audit;

import java.util.Date;

public class AuditActivity
{
    public static final String TYPE = "auditActivity";

    public static final String TOTAL_JOBS = "totalJobs";
    public static final String TOTAL_DETECTORS = "totalDetectors";
    public static final String RUNNING_JOBS = "runningJobs";
    public static final String RUNNING_DETECTORS = "runningDetectors";

    private int m_TotalJobs;
    private int m_TotalDetectors;
    private int m_RunningJobs;
    private int m_RunningDetectors;
    private Date m_Timestamp;

    public AuditActivity()
    {
        // Default constructor
    }

    private AuditActivity(int totalJobs, int totalDetectors, int runningJobs, int runningDetectors)
    {
        m_TotalJobs = totalJobs;
        m_TotalDetectors = totalDetectors;
        m_RunningJobs = runningJobs;
        m_RunningDetectors = runningDetectors;
        m_Timestamp = new Date();
    }

    public int getTotalJobs()
    {
        return m_TotalJobs;
    }

    public void setTotalJobs(int totalJobs)
    {
        m_TotalJobs = totalJobs;
    }

    public int getTotalDetectors()
    {
        return m_TotalDetectors;
    }

    public void setTotalDetectors(int totalDetectors)
    {
        m_TotalDetectors = totalDetectors;
    }

    public int getRunningJobs()
    {
        return m_RunningJobs;
    }

    public void setRunningJobs(int runningJobs)
    {
        m_RunningJobs = runningJobs;
    }

    public int getRunningDetectors()
    {
        return m_RunningDetectors;
    }

    public void setRunningDetectors(int runningDetectors)
    {
        m_RunningDetectors = runningDetectors;
    }

    public Date getTimestamp()
    {
        return m_Timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        m_Timestamp = timestamp;
    }

    public static AuditActivity newActivity(int totalJobs, int totalDetectors, int runningJobs, int runningDetectors)
    {
        return new AuditActivity(totalJobs, totalDetectors, runningJobs, runningDetectors);
    }
}
