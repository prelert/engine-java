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
package com.prelert.job.transform;

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

    /**
     * Find circular dependencies in the list of transforms.
     * This might be because a transform's input is its output
     * or because of a transitive dependency.
     *
     * If there is a circular dependency the index of the transform
     * in the <code>transforms</code> list at the start of the chain
     * is returned else -1
     *
     * @param transforms
     * @return -1 if no circular dependencies else the index of the
     * transform at the start of the circular chain
     */
    public static int checkForCircularDependencies(List<TransformConfig> transforms)
    {
        for (int i=0; i<transforms.size(); i++)
        {
            Set<Integer> chain = new HashSet<Integer>();
            chain.add(new Integer(i));

            TransformConfig tc = transforms.get(i);
            if (checkCircularDependenciesRecursive(tc, transforms, chain) == false)
            {
                return i;
            }
        }

        return -1;
    }


    private static boolean checkCircularDependenciesRecursive(TransformConfig transform,
                                                    List<TransformConfig> transforms,
                                                    Set<Integer> chain)
    {
        boolean result = true;

        for (int i=0; i<transforms.size(); i++)
        {
            TransformConfig tc = transforms.get(i);

            for (String input : transform.getInputs())
            {
                if (tc.getOutputs().contains(input))
                {
                    Integer index = new Integer(i);
                    if (chain.contains(index))
                    {
                        return false;
                    }

                    chain.add(index);
                    result = result && checkCircularDependenciesRecursive(tc, transforms, chain);
                }
            }
        }

        return result;
    }


}
