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

package com.prelert.job.alert;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum AlertType
{
    BUCKET
    {
        @Override
        @JsonValue
        public String toString()
        {
            return new String("bucket");
        }
    },

    INFLUENCER
    {
        @Override
        @JsonValue
        public String toString()
        {
            return new String("influencer");
        }
    },

    BUCKETINFLUENCER
    {
        @Override
        @JsonValue
        public String toString()
        {
            return new String("bucketinfluencer");
        }
    };

    @JsonCreator
    public static AlertType fromString(String str)
    {
        for (AlertType at : AlertType.values())
        {
            if (at.toString().equals(str))
            {
                return at;
            }
        }

        throw new IllegalArgumentException("The string '" + str +
                "' cannot be converted to an AlertType");
    }
}