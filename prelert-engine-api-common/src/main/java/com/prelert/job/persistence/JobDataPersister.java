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

package com.prelert.job.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.primitives.Ints;

/**
 * Persist the records sent the the API.
 * Only the analysis fields are written. Records are mapped by the
 * by, over, partition and metric fields.
 *
 * Concrete classes need to implement the {@link #persistRecord(long, String[])},
 * {@linkplain #deleteData()} and {@linkplain #flushRecords()} methods.
 */
public abstract class JobDataPersister
{
    public static final String FIELDS = "fields";
    public static final String BY_FIELDS = "byFields";
    public static final String OVER_FIELDS = "overFields";
    public static final String PARTITION_FIELDS = "partitionFields";


    protected String [] m_FieldNames;
    protected int [] m_FieldMappings;
    protected int [] m_ByFieldMappings;
    protected int [] m_OverFieldMappings;
    protected int [] m_PartitionFieldMappings;

    public void setFieldMappings(List<String> fields,
            List<String> byFields, List<String> overFields,
            List<String> partitionFields, Map<String, Integer> fieldMap)
    {
        m_FieldNames = fields.toArray(new String[fields.size()]);
        m_FieldMappings = extractMappings(fields, fieldMap);
        m_ByFieldMappings = extractMappings(byFields, fieldMap);
        m_OverFieldMappings = extractMappings(overFields, fieldMap);
        m_PartitionFieldMappings = extractMappings(partitionFields, fieldMap);
    }

    private static int[] extractMappings(List<String> fields, Map<String, Integer> fieldMap)
    {
        List<Integer> mappings = new ArrayList<>();
        for (int i = 0; i < fields.size(); i++)
        {
            Integer index = fieldMap.get(fields.get(i));
            if (index != null)
            {
                mappings.add(index);
            }
        }
        return Ints.toArray(mappings);
    }

    /**
     * Save the record as per the field mappings
     * set up in {@linkplain #setFieldMappings(List, List, List, List, String[])}
     * setFieldMappings must have been called so this class knows where to
     *
     *
     * @param epoch
     * @param record
     */
    public abstract void persistRecord(long epoch, String[] record);

    /**
     * Delete all the persisted records
     *
     * @return
     */
    public abstract boolean deleteData();

    /**
     * Flush any records that may not have been persisted yet
     */
    public abstract void flushRecords();
}
