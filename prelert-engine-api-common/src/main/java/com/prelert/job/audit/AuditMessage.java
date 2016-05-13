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

public class AuditMessage
{
    public static final String TYPE = "auditMessage";

    public static final String JOB_ID = "jobId";
    public static final String MESSAGE = "message";
    public static final String LEVEL = "level";

    private String m_JobId;
    private String m_Message;
    private Level m_Level;
    private Date m_Timestamp;

    public AuditMessage()
    {
        // Default constructor
    }

    private AuditMessage(String jobId, String message, Level severity)
    {
        m_JobId = jobId;
        m_Message = message;
        m_Level = severity;
        m_Timestamp = new Date();
    }

    public String getJobId()
    {
        return m_JobId;
    }

    public void setJobId(String jobId)
    {
        m_JobId = jobId;
    }

    public String getMessage()
    {
        return m_Message;
    }

    public void setMessage(String message)
    {
        m_Message = message;
    }

    public Level getLevel()
    {
        return m_Level;
    }

    public void setLevel(Level level)
    {
        m_Level = level;
    }

    public Date getTimestamp()
    {
        return m_Timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        m_Timestamp = timestamp;
    }

    public static AuditMessage newInfo(String jobId, String message)
    {
        return new AuditMessage(jobId, message, Level.INFO);
    }

    public static AuditMessage newWarning(String jobId, String message)
    {
        return new AuditMessage(jobId, message, Level.WARNING);
    }

    public static AuditMessage newActivity(String jobId, String message)
    {
        return new AuditMessage(jobId, message, Level.ACTIVITY);
    }

    public static AuditMessage newError(String jobId, String message)
    {
        return new AuditMessage(jobId, message, Level.ERROR);
    }
}
