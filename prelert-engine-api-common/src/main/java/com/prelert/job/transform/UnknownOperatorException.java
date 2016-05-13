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
package com.prelert.job.transform;

public class UnknownOperatorException extends Exception
{
    private static final long serialVersionUID = 4691115581549035110L;

    private final String m_Name;

    /**
     *
     * @param operatorName The unrecognised operator name
     */
    public UnknownOperatorException(String operatorName)
    {
        m_Name = operatorName;
    }

    /**
     * Get the unknown operator name
     * @return
     */
    public String getName()
    {
        return m_Name;
    }
}
