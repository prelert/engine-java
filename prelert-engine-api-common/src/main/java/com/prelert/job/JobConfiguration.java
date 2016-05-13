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

import java.util.List;
import java.util.Map;

import com.prelert.job.transform.TransformConfig;


/**
 * This class encapsulates all the data required to create a new job. It
 * does not represent the state of a created job (see {@linkplain JobDetails}
 * for that).
 * <p>
 * If a value has not been set it will be {@code null}. Object wrappers
 * are used around integral types &amp; booleans so they can take {@code null}
 * values.
 */
public class JobConfiguration
{
    private String m_ID;
    private String m_Description;

    private AnalysisConfig m_AnalysisConfig;
    private AnalysisLimits m_AnalysisLimits;
    private SchedulerConfig m_SchedulerConfig;
    private List<TransformConfig> m_Transforms;
    private DataDescription m_DataDescription;
    private Long m_Timeout;
    private ModelDebugConfig m_ModelDebugConfig;
    private Long m_RenormalizationWindowDays;
    private Long m_BackgroundPersistInterval;
    private Long m_ModelSnapshotRetentionDays;
    private Long m_ResultsRetentionDays;
    private IgnoreDowntime m_IgnoreDowntime;
    private Map<String, Object> m_CustomSettings;

    public JobConfiguration()
    {
    }

    public JobConfiguration(AnalysisConfig analysisConfig)
    {
        this();
        m_AnalysisConfig = analysisConfig;
    }

    /**
     * The human readable job Id
     * @return The provided name or null if not set
     */
    public String getId()
    {
        return m_ID;
    }

    /**
     * Set the job's ID
     * @param id the id of the job
     */
    public void setId(String id)
    {
        m_ID = id;
    }

    public Map<String, Object> getCustomSettings()
    {
        return m_CustomSettings;
    }

    public void setCustomSettings(Map<String, Object> customSettings)
    {
        m_CustomSettings = customSettings;
    }

    /**
     * The job's human readable description
     * @return the job description
     */
    public String getDescription()
    {
        return m_Description;
    }

    /**
     * Set the human readable description
     */
    public void setDescription(String description)
    {
        m_Description = description;
    }


    /**
     * The analysis configuration. A properly configured job must have
     * a valid AnalysisConfig
     * @return AnalysisConfig or null if not set.
     */
    public AnalysisConfig getAnalysisConfig()
    {
        return m_AnalysisConfig;
    }

    public void setAnalysisConfig(AnalysisConfig config)
    {
        m_AnalysisConfig = config;
    }

    /**
     * The analysis limits
     *
     * @return Analysis limits or null if not set.
     */
    public AnalysisLimits getAnalysisLimits()
    {
        return m_AnalysisLimits;
    }

    public void setAnalysisLimits(AnalysisLimits options)
    {
        m_AnalysisLimits = options;
    }

    /**
     * The scheduler configuration.
     *
     * @return Scheduler configuration or null if not set.
     */
    public SchedulerConfig getSchedulerConfig()
    {
        return m_SchedulerConfig;
    }

    public void setSchedulerConfig(SchedulerConfig config)
    {
        m_SchedulerConfig = config;
    }

    /**
     * The timeout period for the job in seconds
     * @return The timeout in seconds
     */
    public Long getTimeout()
    {
        return m_Timeout;
    }

    public void setTimeout(Long timeout)
    {
        m_Timeout = timeout;
    }

    public List<TransformConfig> getTransforms()
    {
        return m_Transforms;
    }

    public void setTransforms(List<TransformConfig> transforms)
    {
        m_Transforms = transforms;
    }

    /**
     * If not set the input data is assumed to be csv with a '_time' field
     * in epoch format.
     * @return A DataDescription or {@code null}
     * @see DataDescription
     */
    public DataDescription getDataDescription()
    {
        return m_DataDescription;
    }

    public void setDataDescription(DataDescription description)
    {
        m_DataDescription = description;
    }

    public void setModelDebugConfig(ModelDebugConfig modelDebugConfig)
    {
        m_ModelDebugConfig = modelDebugConfig;
    }

    public ModelDebugConfig getModelDebugConfig()
    {
        return m_ModelDebugConfig;
    }

    /**
     * The background persistence interval in seconds
     * @return background persistence interval in seconds
     */
    public Long getBackgroundPersistInterval()
    {
        return m_BackgroundPersistInterval;
    }

    /**
     * Set the background persistence interval
     * @param backgroundPersistInterval the persistence interval in seconds
     */
    public void setBackgroundPersistInterval(Long backgroundPersistInterval)
    {
        m_BackgroundPersistInterval = backgroundPersistInterval;
    }

    /**
     * The duration of the renormalization window in days
     * @return renormalization window in days
     */
    public Long getRenormalizationWindowDays()
    {
        return m_RenormalizationWindowDays;
    }

    /**
     * Set the renormalization window duration
     * @param renormalizationWindowDays the renormalization window in days
     */
    public void setRenormalizationWindowDays(Long renormalizationWindowDays)
    {
        m_RenormalizationWindowDays = renormalizationWindowDays;
    }

    public Long getModelSnapshotRetentionDays()
    {
        return m_ModelSnapshotRetentionDays;
    }

    public void setModelSnapshotRetentionDays(Long modelSnapshotRetentionDays)
    {
        m_ModelSnapshotRetentionDays = modelSnapshotRetentionDays;
    }

    public Long getResultsRetentionDays()
    {
        return m_ResultsRetentionDays;
    }

    public void setResultsRetentionDays(Long resultsRetentionDays)
    {
        m_ResultsRetentionDays = resultsRetentionDays;
    }

    public IgnoreDowntime getIgnoreDowntime()
    {
        return m_IgnoreDowntime;
    }

    public void setIgnoreDowntime(IgnoreDowntime ignoreDowntime)
    {
        m_IgnoreDowntime = ignoreDowntime;
    }
}
