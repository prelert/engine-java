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

import java.util.Deque;
import java.util.NoSuchElementException;

/**
 * An iterator useful to fetch a big number of documents of type T
 * and iterate through them in batches.
 */
public interface BatchedDocumentsIterator<T>
{
    /**
     * Query documents whose timestamp is within the given time range
     *
     * @param startEpochMs the start time as epoch milliseconds (inclusive)
     * @param endEpochMs the end time as epoch milliseconds (exclusive)
     * @return the iterator itself
     */
    BatchedDocumentsIterator<T> timeRange(long startEpochMs, long endEpochMs);

    /**
     * Include interim documents
     *
     * @param interimFieldName Name of the include interim field
     * @return
     */
    BatchedDocumentsIterator<T> includeInterim(String interimFieldName);

    /**
     * The first time next() is called, the search will be performed and the first
     * batch will be returned. Any subsequent call will return the following batches.
     * <p>
     * Note that in some implementations it is possible that when there are no
     * results at all, the first time this method is called an empty {@code Deque} is returned.
     *
     * @return a {@code Deque} with the next batch of documents
     * @throws NoSuchElementException if the iteration has no more elements
     */
    Deque<T> next();

    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    boolean hasNext();
}
