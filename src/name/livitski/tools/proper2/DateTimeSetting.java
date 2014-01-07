/**
 *    Copyright © 2014 Konstantin "Stan" Livitski
 * 
 *    This file is part of proper2. Proper2 is
 *    licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package name.livitski.tools.proper2;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;


/**
 * Parses and stores a setting with a date and/or time value.
 * Call {@link #setFormat(DateFormat)} to control the contents
 * of values this setting may take and how you expect them to be
 * encoded in the property text.
 */
public abstract class DateTimeSetting<D> extends AbstractSetting<D, Date>
{
 /**
  * @param name the key of this setting in the configuration file
  */
 public DateTimeSetting(String name)
 {
  this(name, null);
 }

 /**
  * @param name the key of this setting in the configuration file
  * @param format the argument to pass to {@link #setFormat(DateFormat)}
  */
 public DateTimeSetting(String name, DateFormat format)
 {
  super(name);
  if (null != format)
   setFormat(format);
 }

 /**
  * Returns the format object that parses the text of this
  * setting. If no format has been assigned to this handler,
  * creates and assigns a locale-specific
  * {@link DateFormat#getDateTimeInstance() default parser}
  * that expects both date and time values. 
  * @return a non-null format object
  */
 public DateFormat getFormat()
 {
  if (null == format)
   format = DateFormat.getDateTimeInstance();
  return format;
 }

 /**
  * Designates the format that will
  * {@link DateFormat#parse(String)} the text of this
  * setting.
  * @param format parser for the target format or
  * <code>null</code> to use the default format
  * for the current locale
  * @see #getFormat() 
  */
 public void setFormat(DateFormat format)
 {
  this.format = format;
 }

 @Override
 public Class<Date> getType()
 {
  return Date.class;
 }

 @Override
 public Date getValue() throws ConfigurationException
 {
  try
  {
   return getFormat().parse((String)valueString);
  }
  catch (ParseException e)
  {
   throw new ConfigurationException(
     this + " encountered an invalid date/time value \"" + valueString + '"', e);
  }
 }

 private DateFormat format;
}
