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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * Autodetect analysis configuration options describes which fields are
 * analysed and the functions to use.
 * <p>
 * The configuration can contain multiple detectors, a new anomaly detector will
 * be created for each detector configuration. The fields
 * <code>bucketSpan, batchSpan, summaryCountFieldName and categorizationFieldName</code>
 * apply to all detectors.
 * <p>
 * If a value has not been set it will be <code>null</code>
 * Object wrappers are used around integral types &amp; booleans so they can take
 * <code>null</code> values.
 */
@JsonInclude(Include.NON_NULL)
public class AnalysisConfig
{
    /**
     * Serialisation names
     */
    public static final String BUCKET_SPAN = "bucketSpan";
    public static final String BATCH_SPAN = "batchSpan";
    public static final String CATEGORIZATION_FIELD_NAME = "categorizationFieldName";
    public static final String LATENCY = "latency";
    public static final String PERIOD = "period";
    public static final String SUMMARY_COUNT_FIELD_NAME = "summaryCountFieldName";
    public static final String DETECTORS = "detectors";
    public static final String INFLUENCERS = "influencers";
    public static final String OVERLAPPING_BUCKETS = "overlappingBuckets";
    public static final String RESULT_FINALIZATION_WINDOW = "resultFinalizationWindow";
    public static final String MULTIVARIATE_BY_FIELDS = "multivariateByFields";

    private static final String PRELERT_CATEGORY_FIELD = "prelertcategory";
    public static final Set<String> AUTO_CREATED_FIELDS = new HashSet<>(
            Arrays.asList(PRELERT_CATEGORY_FIELD));

    public static final long DEFAULT_RESULT_FINALIZATION_WINDOW = 2L;

    /**
     * These values apply to all detectors
     */
    private Long m_BucketSpan;
    private Long m_BatchSpan;
    private String m_CategorizationFieldName;
    private Long m_Latency = 0L;
    private Long m_Period;
    private String m_SummaryCountFieldName;
    private List<Detector> m_Detectors;
    private List<String> m_Influencers;
    private Boolean m_OverlappingBuckets;
    private Long m_ResultFinalizationWindow;
    private Boolean m_MultivariateByFields;

    /**
     * Default constructor
     */
    public AnalysisConfig()
    {
        m_Detectors = new ArrayList<>();
        m_Influencers = new ArrayList<>();
    }

    /**
     * The size of the interval the analysis is aggregated into measured in
     * seconds
     * @return The bucketspan or <code>null</code> if not set
     */
    public Long getBucketSpan()
    {
        return m_BucketSpan;
    }

    public void setBucketSpan(Long span)
    {
        m_BucketSpan = span;
    }

    /**
     * Interval into which to batch seasonal data measured in seconds
     * @return The batchspan or <code>null</code> if not set
     */
    public Long getBatchSpan()
    {
        return m_BatchSpan;
    }

    public void setBatchSpan(Long batchSpan)
    {
        m_BatchSpan = batchSpan;
    }

    public String getCategorizationFieldName()
    {
        return m_CategorizationFieldName;
    }

    public void setCategorizationFieldName(String categorizationFieldName)
    {
        m_CategorizationFieldName = categorizationFieldName;
    }

    /**
     * The latency interval (seconds) during which out-of-order records should be handled.
     * @return The latency interval (seconds) or <code>null</code> if not set
     */
    public Long getLatency()
    {
        return m_Latency;
    }

    /**
     * Set the latency interval during which out-of-order records should be handled.
     * @param latency the latency interval in seconds
     */
    public void setLatency(Long latency)
    {
        m_Latency = latency;
    }

    /**
     * The repeat interval for periodic data in multiples of
     * {@linkplain #getBatchSpan()}
     * @return The period or <code>null</code> if not set
     */
    public Long getPeriod()
    {
        return m_Period;
    }

    public void setPeriod(Long period)
    {
        m_Period = period;
    }

    /**
     * The name of the field that contains counts for pre-summarised input
     * @return The field name or <code>null</code> if not set
     */
    public String getSummaryCountFieldName()
    {
        return m_SummaryCountFieldName;
    }

    public void setSummaryCountFieldName(String summaryCountFieldName)
    {
        m_SummaryCountFieldName = summaryCountFieldName;
    }

    /**
     * The list of analysis detectors. In a valid configuration the list should
     * contain at least 1 {@link Detector}
     * @return The Detectors used in this job
     */
    public List<Detector> getDetectors()
    {
        return m_Detectors;
    }

    public void setDetectors(List<Detector> detectors)
    {
        m_Detectors = detectors;
    }

    /**
     * The list of influence field names
     */
    public List<String> getInfluencers()
    {
        return m_Influencers;
    }

    public void setInfluencers(List<String> influencers)
    {
        m_Influencers = influencers;
    }

    /**
     * Return the list of term fields.
     * These are the influencer fields, partition field,
     * by field and over field of each detector.
     * <code>null</code> and empty strings are filtered from the
     * config.
     *
     * @return Set of term fields - never <code>null</code>
     */
    public Set<String> termFields()
    {
        Set<String> termFields = new TreeSet<>();

        for (Detector d : getDetectors())
        {
            addIfNotNull(termFields, d.getByFieldName());
            addIfNotNull(termFields, d.getOverFieldName());
            addIfNotNull(termFields, d.getPartitionFieldName());
        }

        for (String i : getInfluencers())
        {
            addIfNotNull(termFields, i);
        }

        // remove empty strings
        termFields.remove("");

        return termFields;
    }

    public Boolean getOverlappingBuckets()
    {
        return m_OverlappingBuckets;
    }

    public void setOverlappingBuckets(Boolean b)
    {
        m_OverlappingBuckets = b;
    }

    public Long getResultFinalizationWindow()
    {
        return m_ResultFinalizationWindow;
    }

    public void setResultFinalizationWindow(Long l)
    {
        m_ResultFinalizationWindow = l;
    }

    public Boolean getMultivariateByFields()
    {
        return m_MultivariateByFields;
    }

    public void setMultivariateByFields(Boolean multivariateByFields)
    {
        m_MultivariateByFields = multivariateByFields;
    }

    /**
     * Return the list of fields required by the analysis.
     * These are the influencer fields, metric field, partition field,
     * by field and over field of each detector, plus the summary count
     * field and the categorization field name of the job.
     * <code>null</code> and empty strings are filtered from the
     * config.
     *
     * @return List of required analysis fields - never <code>null</code>
     */
    public List<String> analysisFields()
    {
        Set<String> analysisFields = termFields();

        addIfNotNull(analysisFields, m_CategorizationFieldName);
        addIfNotNull(analysisFields, m_SummaryCountFieldName);

        for (Detector d : getDetectors())
        {
            addIfNotNull(analysisFields, d.getFieldName());
        }

        // remove empty strings
        analysisFields.remove("");

        return new ArrayList<String>(analysisFields);
    }

    private static void addIfNotNull(Set<String> fields, String field)
    {
        if (field != null)
        {
            fields.add(field);
        }
    }

    public List<String> fields()
    {
        return collectNonNullAndNonEmptyDetectorFields(d -> d.getFieldName());
    }

    private List<String> collectNonNullAndNonEmptyDetectorFields(
            Function<Detector, String> fieldGetter)
    {
        Set<String> fields = new HashSet<>();

        for (Detector d : getDetectors())
        {
            addIfNotNull(fields, fieldGetter.apply(d));
        }

        // remove empty strings
        fields.remove("");

        return new ArrayList<String>(fields);
    }

    public List<String> byFields()
    {
        return collectNonNullAndNonEmptyDetectorFields(d -> d.getByFieldName());
    }

    public List<String> overFields()
    {
        return collectNonNullAndNonEmptyDetectorFields(d -> d.getOverFieldName());
    }


    public List<String> partitionFields()
    {
        return collectNonNullAndNonEmptyDetectorFields(d -> d.getPartitionFieldName());
    }

    /**
     * The array of detectors are compared for equality but they are not sorted
     * first so this test could fail simply because the detector arrays are in
     * different orders.
     */
    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }

        if (other instanceof AnalysisConfig == false)
        {
            return false;
        }

        AnalysisConfig that = (AnalysisConfig)other;

        if (this.m_Detectors.size() != that.m_Detectors.size())
        {
            return false;
        }

        for (int i=0; i<m_Detectors.size(); i++)
        {
            if (!this.m_Detectors.get(i).equals(that.m_Detectors.get(i)))
            {
                return false;
            }
        }

        return Objects.equals(this.m_BucketSpan, that.m_BucketSpan) &&
                Objects.equals(this.m_BatchSpan, that.m_BatchSpan) &&
                Objects.equals(this.m_CategorizationFieldName, that.m_CategorizationFieldName) &&
                Objects.equals(this.m_Latency, that.m_Latency) &&
                Objects.equals(this.m_Period, that.m_Period) &&
                Objects.equals(this.m_SummaryCountFieldName, that.m_SummaryCountFieldName) &&
                Objects.equals(this.m_Influencers, that.m_Influencers) &&
                Objects.equals(this.m_OverlappingBuckets, that.m_OverlappingBuckets) &&
                Objects.equals(this.m_ResultFinalizationWindow,  that.m_ResultFinalizationWindow) &&
                Objects.equals(this.m_MultivariateByFields, that.m_MultivariateByFields);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(m_Detectors, m_BucketSpan, m_BatchSpan, m_Latency, m_Period,
                m_SummaryCountFieldName, m_Influencers, m_OverlappingBuckets, m_ResultFinalizationWindow,
                m_MultivariateByFields);
    }

}
