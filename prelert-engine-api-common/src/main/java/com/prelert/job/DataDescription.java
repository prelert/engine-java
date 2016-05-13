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

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Describes the format of the data used in the job and how it should
 * be interpreted by autodetect.
 * <p>
 * Data must either be in a textual delineated format (e.g. csv, tsv) or JSON
 * the {@linkplain DataFormat} enum indicates which. {@link #getTimeField()}
 * is the name of the field containing the timestamp and {@link #getTimeFormat()}
 * is the format code for the date string in as described by
 * {@link java.time.format.DateTimeFormatter}. The default quote character for
 * delineated formats is {@value #DEFAULT_QUOTE_CHAR} but any other character can be
 * used.
 */
@JsonIgnoreProperties({"transformTime", "epochMs"})
@JsonInclude(Include.NON_NULL)
public class DataDescription
{
    /**
     * Enum of the acceptable data formats.
     */
    public enum DataFormat
    {
        JSON, DELIMITED, SINGLE_LINE, ELASTICSEARCH;

        /**
         * Delimited used to be called delineated. We keep supporting that for backwards
         * compatibility.
         */
        private static final String DEPRECATED_DELINEATED = "DELINEATED";

        /**
         * Case-insensitive from string method.
         * Works with either JSON, json, etc.
         *
         * @param value String representation
         * @return The data format
         */
        @JsonCreator
        public static DataFormat forString(String value)
        {
            String valueUpperCase = value.toUpperCase();
            return DEPRECATED_DELINEATED.equals(valueUpperCase) ? DELIMITED : DataFormat
                    .valueOf(valueUpperCase);
        }
    }

    /**
     * Special time format string for epoch times (seconds)
     */
    public static final String EPOCH = "epoch";

    /**
     * Special time format string for epoch times (milli-seconds)
     */
    public static final String EPOCH_MS = "epoch_ms";

    /**
     * The format field name
     */
    public static final String FORMAT = "format";
    /**
     * The time field name
     */
    public static final String TIME_FIELD_NAME = "timeField";

    /**
     * By default autodetect expects the timestamp in a field with this name
     */
    public static final String DEFAULT_TIME_FIELD = "time";

    /**
     * The timeFormat field name
     */
    public static final String TIME_FORMAT = "timeFormat";
    /**
     * The field delimiter field name
     */
    public static final String FIELD_DELIMITER = "fieldDelimiter";
    /**
     * The quote char field name
     */
    public static final String QUOTE_CHARACTER = "quoteCharacter";

    /**
     * The default field delimiter expected by the native autodetect_api
     * program.
     */
    public static final char DEFAULT_DELIMITER = '\t';

    /**
     * Csv data must have this line ending
     */
    public static final char LINE_ENDING = '\n';

    /**
     * The default quote character used to escape text in
     * delineated data formats
     */
    public static final char DEFAULT_QUOTE_CHAR = '"';

    private DataFormat m_DataFormat;
    private String m_TimeFieldName;
    private String m_TimeFormat;
    private char m_FieldDelimiter;
    private char m_QuoteCharacter;

    public DataDescription()
    {
        m_DataFormat = DataFormat.DELIMITED;
        m_TimeFieldName = DEFAULT_TIME_FIELD;
        m_TimeFormat = EPOCH;
        m_FieldDelimiter = DEFAULT_DELIMITER;
        m_QuoteCharacter = DEFAULT_QUOTE_CHAR;
    }

    /**
     * The format of the data to be processed.
     * Defaults to {@link DataDescription.DataFormat#DELIMITED}
     * @return The data format
     */
    public DataFormat getFormat()
    {
        return m_DataFormat;
    }

    public void setFormat(DataFormat format)
    {
        m_DataFormat = format;
    }

    /**
     * The name of the field containing the timestamp
     * @return A String if set or <code>null</code>
     */
    public String getTimeField()
    {
        return m_TimeFieldName;
    }

    public void setTimeField(String fieldName)
    {
        m_TimeFieldName = fieldName;
    }

    /**
     * Either {@value #EPOCH}, {@value #EPOCH_MS} or a SimpleDateTime format string.
     * If not set (is <code>null</code> or an empty string) or set to
     * {@value #EPOCH} (the default) then the date is assumed to be in
     * seconds from the epoch.
     * @return A String if set or <code>null</code>
     */
    public String getTimeFormat()
    {
        return m_TimeFormat;
    }

    public void setTimeFormat(String format)
    {
        m_TimeFormat = format;
    }

    /**
     * If the data is in a delineated format with a header e.g. csv or tsv
     * this is the delimiter character used. This is only applicable if
     * {@linkplain #getFormat()} is {@link DataDescription.DataFormat#DELIMITED}.
     * The default value is {@value #DEFAULT_DELIMITER}
     *
     * @return A char
     */
    public char getFieldDelimiter()
    {
        return m_FieldDelimiter;
    }

    public void setFieldDelimiter(char delimiter)
    {
        m_FieldDelimiter = delimiter;
    }

    /**
     * The quote character used in delineated formats.
     * Defaults to {@value #DEFAULT_QUOTE_CHAR}
     * @return The delineated format quote character
     */
    public char getQuoteCharacter()
    {
        return m_QuoteCharacter;
    }

    public void setQuoteCharacter(char value)
    {
        m_QuoteCharacter = value;
    }


    /**
     * Returns true if the data described by this object needs
     * transforming before processing by autodetect.
     * A transformation must be applied if either a timeformat is
     * not in seconds since the epoch or the data is in Json format.
     * @return True if the data should be transformed.
     */
    public boolean transform()
    {
        return m_DataFormat == DataFormat.JSON ||
                isTransformTime();
    }


    /**
     * Return true if the time is in a format that needs transforming.
     * Anytime format this isn't {@value #EPOCH} or <code>null</code>
     * needs transforming.
     * @return True if the time field needs to be transformed.
     */
    public boolean isTransformTime()
    {
        return m_TimeFormat != null && !EPOCH.equals(m_TimeFormat);
    }

    /**
     * Return true if the time format is {@value #EPOCH_MS}
     * @return True if the date is in milli-seconds since the epoch.
     */
    public boolean isEpochMs()
    {
        return EPOCH_MS.equals(m_TimeFormat);
    }

    /**
     * Overridden equality test
     */
    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }

        if (other instanceof DataDescription == false)
        {
            return false;
        }

        DataDescription that = (DataDescription)other;

        return this.m_DataFormat == that.m_DataFormat &&
                this.m_QuoteCharacter == that.m_QuoteCharacter &&
                Objects.equals(this.m_TimeFieldName, that.m_TimeFieldName) &&
                Objects.equals(this.m_TimeFormat, that.m_TimeFormat) &&
                Objects.equals(this.m_FieldDelimiter, that.m_FieldDelimiter);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(m_DataFormat, m_QuoteCharacter, m_TimeFieldName,
                m_TimeFormat, m_FieldDelimiter);
    }


}
