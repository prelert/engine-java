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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;


/**
 * Autodetect analysis configuration options describes which fields are
 * analysed and the functions to use.
 * <p/>
 * The configuration can contain multiple detectors, a new anomaly detector will
 * be created for each detector configuration. The other fields
 * <code>bucketSpan, summaryCountFieldName</code> etc apply to all detectors.<p/>
 * If a value has not been set it will be <code>null</code>
 * Object wrappers are used around integral types & booleans so they can take
 * <code>null</code> values.
 */
public class AnalysisConfig
{
	/**
	 * Serialisation names
	 */
	public static final String BUCKET_SPAN = "bucketSpan";
	public static final String BATCH_SPAN = "batchSpan";
	public static final String PERIOD = "period";
	public static final String SUMMARY_COUNT_FIELD_NAME = "summaryCountFieldName";
	public static final String DETECTORS = "detectors";

	/**
	 * These values apply to all detectors
	 */
	private Long m_BucketSpan;
	private Long m_BatchSpan;
	private Long m_Period;
	private String m_SummaryCountFieldName;
	private List<Detector> m_Detectors;

	/**
	 * Default constructor
	 */
	public AnalysisConfig()
	{
		m_Detectors = new ArrayList<>();
	}

	/**
	 * Construct an AnalysisConfig from a map. Detector objects are
	 * nested elements in the map.
	 * @param values
	 */
	@SuppressWarnings("unchecked")
	public AnalysisConfig(Map<String, Object> values)
	{
		this();

		if (values.containsKey(BUCKET_SPAN))
		{
			Object obj = values.get(BUCKET_SPAN);
			if (obj != null)
			{
				m_BucketSpan = ((Integer)obj).longValue();
			}
		}
		if (values.containsKey(BATCH_SPAN))
		{
			Object obj = values.get(BATCH_SPAN);
			if (obj != null)
			{
				m_BatchSpan = ((Integer)obj).longValue();
			}
		}
		if (values.containsKey(PERIOD))
		{
			Object obj = values.get(PERIOD);
			if (obj != null)
			{
				m_Period = ((Integer)obj).longValue();
			}
		}
		if (values.containsKey(SUMMARY_COUNT_FIELD_NAME))
		{
			Object obj = values.get(SUMMARY_COUNT_FIELD_NAME);
			if (obj != null)
			{
				m_SummaryCountFieldName = (String)obj;
			}
		}
		if (values.containsKey(DETECTORS))
		{
			Object obj = values.get(DETECTORS);
			if (obj instanceof ArrayList)
			{
				for (Map<String, Object> detectorMap : (ArrayList<Map<String, Object>>)obj)
				{
					Detector detector = new Detector(detectorMap);
					m_Detectors.add(detector);
				}
			}

		}
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
	 * Return the list of fields required by the analysis.
	 * These are the metric field, partition field, by field and over
	 * field of each detector, plus the summary count field of the job.
	 * <code>null</code> and empty strings are filtered from the
	 * config
	 *
	 * @return List of required fields.
	 */
	public List<String> analysisFields()
	{
		Set<String> fields = new TreeSet<>();

		if (m_SummaryCountFieldName != null)
		{
			fields.add(m_SummaryCountFieldName);
		}

		for (Detector d : getDetectors())
		{
			if (d.getFieldName() != null)
			{
				fields.add(d.getFieldName());
			}
			if (d.getByFieldName() != null)
			{
				fields.add(d.getByFieldName() );
			}
			if (d.getOverFieldName() != null)
			{
				fields.add(d.getOverFieldName());
			}
			if (d.getPartitionFieldName() != null)
			{
				fields.add(d.getPartitionFieldName());
			}
		}

		// remove empty strings
		fields.remove("");

		return new ArrayList<String>(fields);
	}



	public List<String> fields()
	{
		Set<String> fields = new HashSet<>();

		for (Detector d : getDetectors())
		{
			fields.add(d.getFieldName());
		}

		// remove the null and empty strings
		fields.remove("");
		fields.remove(null);

		return new ArrayList<String>(fields);
	}



	public List<String> byFields()
	{
		Set<String> fields = new HashSet<>();

		for (Detector d : getDetectors())
		{
			fields.add(d.getByFieldName());
		}

		// remove the null and empty strings
		fields.remove("");
		fields.remove(null);

		return new ArrayList<String>(fields);
	}

	public List<String> overFields()
	{
		Set<String> fields = new HashSet<>();

		for (Detector d : getDetectors())
		{
			fields.add(d.getOverFieldName());
		}

		// remove the null and empty strings
		fields.remove("");
		fields.remove(null);

		return new ArrayList<String>(fields);
	}


	public List<String> partitionFields()
	{
		Set<String> fields = new HashSet<>();

		for (Detector d : getDetectors())
		{
			fields.add(d.getPartitionFieldName());
		}

		// remove the null and empty strings
		fields.remove("");
		fields.remove(null);

		return new ArrayList<String>(fields);
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

		return JobDetails.bothNullOrEqual(this.m_BucketSpan, that.m_BucketSpan) &&
				JobDetails.bothNullOrEqual(this.m_BatchSpan, that.m_BatchSpan) &&
				JobDetails.bothNullOrEqual(this.m_Period, that.m_Period) &&
				JobDetails.bothNullOrEqual(this.m_SummaryCountFieldName, that.m_SummaryCountFieldName);
	}

    @Override
    public int hashCode()
    {
        return Objects.hash(m_Detectors, m_BucketSpan, m_BatchSpan, m_Period,
                m_SummaryCountFieldName);
    }
}
