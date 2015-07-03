/************************************************************
 *                                                          *
 * Contents of file Copyright (c) Prelert Ltd 2006-2015     *
 *                                                          *
 *----------------------------------------------------------*
 *----------------------------------------------------------*
 * WARNING:                                                 *
 * THIS FILE CONTAINS UNPUBLISHED PROPRIETARY               *
 * SOURCE CODE WHICH IS THE PROPERTY OF PRELERT LTD AND     *
 * PARENT OR SUBSIDIARY COMPANIES.                          *
 * PLEASE READ THE FOLLOWING AND TAKE CAREFUL NOTE:         *
 *                                                          *
 * This source code is confidential and any person who      *
 * receives a copy of it, or believes that they are viewing *
 * it without permission is asked to notify Prelert Ltd     *
 * on +44 (0)20 3567 1249 or email to legal@prelert.com.    *
 * All intellectual property rights in this source code     *
 * are owned by Prelert Ltd.  No part of this source code   *
 * may be reproduced, adapted or transmitted in any form or *
 * by any means, electronic, mechanical, photocopying,      *
 * recording or otherwise.                                  *
 *                                                          *
 *----------------------------------------------------------*
 *                                                          *
 *                                                          *
 ************************************************************/

package com.prelert.job;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties({"enabled"})
@JsonInclude(Include.NON_NULL)
public class ModelDebugConfig
{
    public static final String TYPE = "modelDebugConfig";
    public static final String BOUNDS_PERCENTILE = "boundsPercentile";
    public static final String TERMS = "terms";

    private Double m_BoundsPercentile;
    private String m_Terms;

    public ModelDebugConfig()
    {
    }

    public ModelDebugConfig(Double boundsPercentile, String terms)
    {
        m_BoundsPercentile = boundsPercentile;
        m_Terms = terms;
    }

    public boolean isEnabled()
    {
        return m_BoundsPercentile != null;
    }

    public Double getBoundsPercentile()
    {
        return m_BoundsPercentile;
    }

    public void setBoundsPercentile(Double boundsPercentile)
    {
        m_BoundsPercentile = boundsPercentile;
    }

    public String getTerms()
    {
        return m_Terms;
    }

    public void setTerms(String terms)
    {
        m_Terms = terms;
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }

        if (other instanceof ModelDebugConfig == false)
        {
            return false;
        }

        ModelDebugConfig that = (ModelDebugConfig) other;
        return Objects.equals(this.m_BoundsPercentile, that.m_BoundsPercentile)
                && Objects.equals(this.m_Terms, that.m_Terms);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(m_BoundsPercentile, m_Terms);
    }
}
