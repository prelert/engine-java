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
package com.prelert.job;

import com.prelert.job.errorcodes.ErrorCodes;
import com.prelert.job.messages.Messages;

/**
 * This type of exception represents an error where
 * an operation uses a <i>SnapshotId</i> that does not exist.
 */
public class NoSuchModelSnapshotException extends JobException
{
    private static final long serialVersionUID = -2359537142813149135L;

    public NoSuchModelSnapshotException(String jobId)
    {
        super(Messages.getMessage(Messages.REST_NO_SUCH_MODEL_SNAPSHOT, jobId),
                ErrorCodes.NO_SUCH_MODEL_SNAPSHOT);
    }
}
