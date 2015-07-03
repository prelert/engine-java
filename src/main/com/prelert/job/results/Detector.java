/****************************************************************************
 *                                                                          *
 * Copyright 2015 Prelert Ltd                                               *
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents the anomaly detector.
 * Only the detector name is serialised anomaly records aren't.
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties({"records"})
public class Detector
{
    public static final String TYPE = "detector";
    public static final String NAME = "name";
    public static final String RECORDS = "records";

    private String m_Name;
    private List<AnomalyRecord> m_Records;


    public Detector()
    {
        m_Records = new ArrayList<>();
    }

    public Detector(String name)
    {
        this();
        setName(name);
    }

    public String getName()
    {
        return m_Name;
    }

    public void setName(String name)
    {
        m_Name = name.intern();
    }

    public void addRecord(AnomalyRecord record)
    {
        m_Records.add(record);
    }

    public List<AnomalyRecord> getRecords()
    {
        return m_Records;
    }
}
