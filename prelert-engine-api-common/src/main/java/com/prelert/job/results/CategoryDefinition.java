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

package com.prelert.job.results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CategoryDefinition
{
    public static final String TYPE = "categoryDefinition";
    public static final String CATEGORY_ID = "categoryId";
    public static final String TERMS = "terms";
    public static final String REGEX = "regex";
    public static final String EXAMPLES = "examples";

    private long m_Id = 0L;
    private String m_Terms = "";
    private String m_Regex = "";
    private final Set<String> m_Examples = new TreeSet<>();

    public long getCategoryId()
    {
        return m_Id;
    }

    public void setCategoryId(long categoryId)
    {
        m_Id = categoryId;
    }

    public String getTerms()
    {
        return m_Terms;
    }

    public void setTerms(String terms)
    {
        m_Terms = terms;
    }

    public String getRegex()
    {
        return m_Regex;
    }

    public void setRegex(String regex)
    {
        m_Regex = regex;
    }

    public List<String> getExamples()
    {
        return new ArrayList<>(m_Examples);
    }

    public void setExamples(Collection<String> examples)
    {
        m_Examples.clear();
        m_Examples.addAll(examples);
    }

    public void addExample(String example)
    {
        m_Examples.add(example);
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (other instanceof CategoryDefinition == false)
        {
            return false;
        }
        CategoryDefinition that = (CategoryDefinition) other;
        return Objects.equals(this.m_Id, that.m_Id)
                && Objects.equals(this.m_Terms, that.m_Terms)
                && Objects.equals(this.m_Regex, that.m_Regex)
                && Objects.equals(this.m_Examples, that.m_Examples);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(m_Id, m_Terms, m_Regex, m_Examples);
    }
}
