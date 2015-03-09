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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;


/**
 * Enum type representing the different transform functions
 * with functions for converting between the enum and its
 * pretty name i.e. human readable string.
 */
public enum TransformType
{
    DOMAIN_LOOKUP(Names.DOMAIN_LOOKUP_NAME, 1, Arrays.asList("subDomain", "hrd")),
    CONCAT(Names.CONCAT, -1, Arrays.asList("concat"));

    /**
     * Enums cannot use static fields in their constructors as the
     * enum values are initialised before the statics.
     * Having the static fields in nested class means they are created
     * when required.
     */
    public class Names
    {
        public static final String DOMAIN_LOOKUP_NAME = "domain_lookup";
        public static final String CONCAT = "concat";
    }


    private int m_Arity;
    private String m_PrettyName;
    private List<String> m_DefaultOutputNames;

    private TransformType(String prettyName, int arity, List<String> defaultOutputNames)
    {
        m_Arity = arity;
        m_PrettyName = prettyName;
        m_DefaultOutputNames = defaultOutputNames;
    }

    /**
     * Arity of -1 means the function is variadic e.g. concat
     * @return
     */
    public int arity()
    {
        return m_Arity;
    }

    public String prettyName()
    {
        return m_PrettyName;
    }

    public List<String> defaultOutputNames()
    {
        return m_DefaultOutputNames;
    }

    @Override
    public String toString()
    {
        return m_PrettyName;
    }

    /**
     * Get the enum for the given pretty name.
     * The static function valueOf() cannot be overridden so use
     * this method instead when converting from the pretty name
     * to enum.
     *
     * @param prettyName
     * @return
     */
    public static TransformType fromString(String prettyName)
    {
        Set<TransformType> all = EnumSet.allOf(TransformType.class);

        for (TransformType type : all)
        {
            if (type.prettyName().equals(prettyName))
            {
                return type;
            }
        }

        throw new IllegalArgumentException (
                                "Unknown TransformType '" + prettyName + "'");
    }

}
