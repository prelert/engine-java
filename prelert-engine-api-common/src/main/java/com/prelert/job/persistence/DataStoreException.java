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

import com.prelert.job.JobException;
import com.prelert.job.errorcodes.ErrorCodes;

public class DataStoreException extends JobException
{
    private static final long serialVersionUID = 3297520527560841022L;

    public DataStoreException(String message, ErrorCodes errorCode)
    {
        super(message, errorCode);
    }

    public DataStoreException(String message, ErrorCodes errorCode, Throwable cause)
    {
        super(message, errorCode, cause);
    }
}
