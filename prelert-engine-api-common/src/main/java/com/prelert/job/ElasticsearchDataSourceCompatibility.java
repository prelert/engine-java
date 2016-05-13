/****************************************************************************
 *                                                                          *
 * Copyright 2016-2016 Prelert Ltd                                          *
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

import java.util.Objects;

public enum ElasticsearchDataSourceCompatibility
{
    V_1_7_X("1.7.x"),
    V_2_X_X("2.x.x");

    private final String m_Description;

    private ElasticsearchDataSourceCompatibility(String description)
    {
        m_Description = Objects.requireNonNull(description);
    }

    public String getDescription()
    {
        return m_Description;
    }

    /**
     * Returns the enum value that matches the given {@code description}
     * @param description a description that matches one of the enum values
     * @return the matching enum value
     * @throws IllegalArgumentException if the given description matches none of the enum values
     */
    public static ElasticsearchDataSourceCompatibility from(String description)
    {
        for (ElasticsearchDataSourceCompatibility option : ElasticsearchDataSourceCompatibility.values())
        {
            if (option.m_Description.equalsIgnoreCase(description))
            {
                return option;
            }
        }
        throw new IllegalArgumentException("No compatibility matches description: " + description);
    }
}
