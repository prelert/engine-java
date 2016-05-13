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

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Enum representing logical comparisons on doubles
 */
public enum Operator
{
    EQ
    {
        @Override
        public boolean test(double lhs, double rhs)
        {
            return Double.compare(lhs, rhs) == 0;
        }
    },
    GT
    {
        @Override
        public boolean test(double lhs, double rhs)
        {
            return Double.compare(lhs, rhs) > 0;
        }
    },
    GTE
    {
        @Override
        public boolean test(double lhs, double rhs)
        {
            return Double.compare(lhs, rhs) >= 0;
        }
    },
    LT
    {
        @Override
        public boolean test(double lhs, double rhs)
        {
            return Double.compare(lhs, rhs) < 0;
        }
    },
    LTE
    {
        @Override
        public boolean test(double lhs, double rhs)
        {
            return Double.compare(lhs, rhs) <= 0;
        }
    },
    MATCH
    {
        @Override
        public boolean match(Pattern pattern, String field)
        {
            Matcher match = pattern.matcher(field);
            return match.matches();
        }

        @Override
        public boolean expectsANumericArgument()
        {
            return false;
        }
    },
    NONE;

    public boolean test(double lhs, double rhs)
    {
        return false;
    }

    public boolean match(Pattern pattern, String field)
    {
        return false;
    }


    public boolean expectsANumericArgument()
    {
        return true;
    }

    @JsonCreator
    public static Operator fromString(String name) throws UnknownOperatorException
    {
        Set<Operator> all = EnumSet.allOf(Operator.class);

        String ucName = name.toUpperCase();
        for (Operator type : all)
        {
            if (type.toString().equals(ucName))
            {
                return type;
            }
        }

        throw new UnknownOperatorException(name);
    }

 };
