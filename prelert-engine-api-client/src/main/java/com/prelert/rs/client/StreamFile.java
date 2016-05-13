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

package com.prelert.rs.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.prelert.rs.data.MultiDataPostResult;

/**
 * Simple program to upload a file to the Prelert Engine API.
 * <br>
 * The job should have been created already and the job's data endpoint known
 * as it is the first argument to this program, the second is the file to upload.
 */
public final class StreamFile
{
    private static final String USAGE = ""
            + "A filename and job id must be specified:\n"
            + "Usage:\n"
            + "\tjava -cp '.:./*' com.prelert.rs.client.StreamFile data_endpoint data_file [--help --compressed --close]\n"
            + "Where data_endpoint is the full Url to a Engine API jobs\n"
            + "data endpoint e.g. http://localhost:8080:/engine/<version>/data/<job_id>\n"
            + "Options:\n"
            + "\t--compressed If the source file is gzip compressed\n"
            + "\t--close If the job should be closed after the file is uploaded\n"
            + "\t--help Show this help";

    private StreamFile()
    {
    }

    /**
     * The program expects 2 arguments a the Url of the job's data endpoint
     * and the file to upload. The use the additional flag <code>--compressed</code>
     * if the data file is gzip compresseed and <code>--close</code> if you wish
     * to close the job once the upload is complete.
     *
     * @param args The input arguments
     * @throws IOException If GZIP or HTTP fails
     */
    public static void main(String[] args) throws IOException
    {
        List<String> argsList = Arrays.asList(args);

        if (argsList.size() < 2 || argsList.contains("--help"))
        {
            System.out.println(USAGE);
            return;
        }

        String url = args[0];
        String filename = args[1];
        boolean compressed = argsList.contains("--compressed");
        boolean close = argsList.contains("--close");

        // extract the job id from the url
        if (url.endsWith("/"))
        {
            url = url.substring(0, url.length() -1);
        }
        int lastIndex = url.lastIndexOf("/");
        String jobId = url.substring(lastIndex + 1);

        lastIndex = url.lastIndexOf("/data");
        String baseUrl = url.substring(0, lastIndex);

        try (EngineApiClient engineApiClient = new EngineApiClient(baseUrl);
             FileInputStream fs = new FileInputStream(new File(filename)))
        {
            long start = System.currentTimeMillis();

            MultiDataPostResult uploaded = engineApiClient.streamingUpload(jobId, fs, compressed);

            if (close)
            {
                engineApiClient.closeJob(jobId);
            }

            long end = System.currentTimeMillis();

            if (uploaded.anErrorOccurred() == false)
            {
                System.out.println(String.format("%s uploaded in %dms", filename, end - start));
            }
        }
    }
}
