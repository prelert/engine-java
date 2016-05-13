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

import com.fasterxml.jackson.annotation.JsonCreator;

public enum IgnoreDowntime
{
    NEVER, ONCE, ALWAYS;

    /**
     * <p>
     * Parses a string and returns the corresponding enum value.
     * </p>
     * <p>
     * The method differs from {@link #valueOf(String)} by being
     * able to handle leading/trailing whitespace and being case
     * insensitive.
     * </p>
     * <p>
     * If there is no match {@link IllegalArgumentException} is thrown.
     * </p>
     * @param value A String that should match one of the enum values
     * @return the matching enum value
     */
    @JsonCreator
    public static IgnoreDowntime fromString(String value)
    {
        return valueOf(value.trim().toUpperCase());
    }
}
