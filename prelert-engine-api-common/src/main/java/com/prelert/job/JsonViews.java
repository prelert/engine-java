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
 * Classes used to configure Jackson's JsonView functionality.  The
 * nested static classes are effectively labels.  They are used in
 * Jackson annotations to specify that certain fields are only
 * serialised/deserialised when a particular "view" of the object is
 * desired.
 */
public class JsonViews
{
    /**
     * Neither this class nor its nested classes are ever meant to be
     * instantiated.
     */
    private JsonViews()
    {
    }

    /**
     * View used when serialising objects for the REST API.
     */
    public static class RestApiView
    {
    }

    /**
     * View used when serialising objects for the data store.
     */
    public static class DatastoreView
    {
    }
}
