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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class for methods involving arrays of transforms
 */
public class TransformConfigs
{
    private List<TransformConfig> m_Transforms;

    public TransformConfigs(List<TransformConfig> transforms)
    {
        m_Transforms = transforms;
        if (m_Transforms == null)
        {
            m_Transforms = Collections.emptyList();
        }
    }


    public List<TransformConfig> getTransforms()
    {
        return m_Transforms;
    }


    /**
     * Set of all the field names configured as inputs to the transforms
     * @return
     */
    public Set<String> inputFieldNames()
    {
        Set<String> fields = new HashSet<>();
        for (TransformConfig t : m_Transforms)
        {
            fields.addAll(t.getInputs());
        }

        return fields;
    }

    public Set<String> outputFieldNames()
    {
        Set<String> fields = new HashSet<>();
        for (TransformConfig t : m_Transforms)
        {
            fields.addAll(t.getOutputs());
        }

        return fields;
    }
}
