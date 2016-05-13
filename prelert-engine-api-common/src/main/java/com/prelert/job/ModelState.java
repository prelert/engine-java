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


/**
 * The serialised models can get very large and only the C++ code
 * understands how to decode them, hence there is no reason to load
 * them into the Java process.
 *
 * However, the Java process DOES set up a mapping on the Elasticsearch
 * index to tell Elasticsearch not to analyse the model state documents
 * in any way.  (Otherwise Elasticsearch would go into a spin trying to
 * make sense of such large JSON documents.)
 */
public class ModelState
{
    /**
     * The type of this class used when persisting the data
     */
    public static final String TYPE = "modelState";

    private ModelState()
    {
    }
}

