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

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import com.prelert.app.Shutdownable;
import com.prelert.job.JobDetails;
import com.prelert.job.JobIdAlreadyExistsException;
import com.prelert.job.JobStatus;
import com.prelert.job.SchedulerConfig;
import com.prelert.job.SchedulerState;
import com.prelert.job.UnknownJobException;

/**
 * General interface for classes that persist Jobs and job data
 */
public interface JobDetailsProvider extends Shutdownable
{
    /**
     * Store the Prelert info doc (version number etc)
     *
     * @param infoDoc
     * @return
     */
    boolean savePrelertInfo(String infoDoc);

    /**
     * Ensures a given {@code jobId} corresponds to an existing job
     * @throws UnknownJobException if there is no job with {@code jobId}
     */
    void checkJobExists(String jobId) throws UnknownJobException;

    /**
     * Return true if the job id is unique else if it is already used
     * by another job false is returned
     *
     * @param jobId
     * @return true or false
     */
    boolean jobIdIsUnique(String jobId);

    /**
     * Get the details of the specific job or an empty
     * Optional if there is no job with the given id.
     *
     * @param jobId
     * @return The JobDetails
     */
    Optional<JobDetails> getJobDetails(String jobId);

    /**
     * Get details of all Jobs.
     *
     * @param skip Skip the first N Jobs. This parameter is for paging
     * results if not required set to 0.
     * @param take Take only this number of Jobs
     * @return A QueryPage object with hitCount set to the total number
     * of jobs not the only the number returned here as determined by the
     * <code>take</code> parameter.
     */
    QueryPage<JobDetails> getJobs(int skip, int take);

    /**
     * Returns a {@link BatchedDocumentsIterator} that allows querying
     * and iterating over all jobs
     *
     * @return a job {@link BatchedDocumentsIterator}
     */
    BatchedDocumentsIterator<JobDetails> newBatchedJobsIterator();

    /**
     * Save the details of the new job to the datastore.
     * Throws <code>JobIdAlreadyExistsException</code> if a job with the
     * same Id already exists.
     *
     * @param job
     * @return True
     * @throws JobIdAlreadyExistsException
     */
    boolean createJob(JobDetails job) throws JobIdAlreadyExistsException;

    /**
     * Update the job document with the values in the <code>updates</code> map.
     * e.g. Map<String, Object> update = new HashMap<>();<br>
     *      update.put(JobDetails.STATUS, JobStatus.CLOSED);
     *
     * @param jobId
     * @return Whether the operation was a success
     * @throws UnknownJobException if there is no job with the id.
     */
    boolean updateJob(String jobId, Map<String, Object> updates) throws UnknownJobException;

    /**
     * Delete all the job related documents from the database.
     *
     * @param jobId
     * @return
     * @throws UnknownJobException If the jobId is not recognised
     * @throws DataStoreException If there is a datastore error
     */
    boolean deleteJob(String jobId) throws UnknownJobException, DataStoreException;

    /**
     * Set the job status
     *
     * @param jobId
     * @param status
     * @return
     * @throws UnknownJobException If there is no job with id <code>jobId</code>
     */
    boolean setJobStatus(String jobId, JobStatus status) throws UnknownJobException;

    /**
     * Set the job's finish time and status
     * @param jobId
     * @param time
     * @param status
     * @return
     * @throws UnknownJobException
     */
    boolean setJobFinishedTimeAndStatus(String jobId, Date time, JobStatus status)
            throws UnknownJobException;

    /**
     * Sets the description of detector at {@code detectorIndex} of job
     * with id {@code jobId} to {@code newDescription}
     *
     * @param detectorIndex the zero-based index of the detector in detectors list
     * @param newDescription the new description
     * @return {@code true} if update was successful
     * @throws UnknownJobException If there is no job with id <code>jobId</code>
     */
    boolean updateDetectorDescription(String jobId, int detectorIndex, String newDescription)
            throws UnknownJobException;

    /**
     * Updates the scheduler config of job with id {@code jobId} to the given {@code newSchedulerConfig}
     *
     * @param jobId The job id to be updated
     * @param newSchedulerConfig the new scheduler config
     * @return {@code true} if update was successful
     * @throws UnknownJobException If there is no job with id <code>jobId</code>
     */
    boolean updateSchedulerConfig(String jobId, SchedulerConfig newSchedulerConfig)
            throws UnknownJobException;

    /**
     * Updates the scheduler state for the given job
     * @param jobId the job id
     * @param schedulerState the new scheduler state
     * @return {@code true} if update was successful
     * @throws UnknownJobException If there is no job with id <code>jobId</code>
     */
    boolean updateSchedulerState(String jobId, SchedulerState schedulerState)
            throws UnknownJobException;

    /**
     * Retrieves the state of the scheduler for the given job
     * @param jobId the job id
     * @return the scheduler state or empty if none exists
     */
    Optional<SchedulerState> getSchedulerState(String jobId);
}
