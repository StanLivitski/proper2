/**
 *    Copyright © 2013 Konstantin "Stan" Livitski
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

import java.util.Properties;
import java.util.regex.Pattern;


/**
 * Parses a configuration setting and stores its value.
 * @param <D> the concrete class that handles this setting
 * @param <T> the type of values accepted by this setting
 */
public abstract class AbstractSetting<D, T>
{
 /**
  * Returns the (most general) type of accepted values.
  */
 public abstract Class<T> getType();

 /**
  * Returns the value of this setting.
  * @throws ConfigurationException if the setting's value is
  * not valid or this is a required setting
  * not defined in the configuration
  */
 public abstract T getValue() throws ConfigurationException;

 /**
  * Loads this setting from a settings map if the map contains it.
  */
 public void load(Properties map) throws ConfigurationException
 {
  valueString = map.getProperty(name);
  if (required && null == valueString)
   throw new ConfigurationException(
     this + " is required and missing from the configuration file.");
  if (null != constraintPattern && null != valueString && !constraintPattern.matcher(valueString).matches())
   throw new ConfigurationException(
     this + " \"" + valueString
     + "\" does not match the constraint pattern: " + getConstraintPattern());
 }

 /**
  * Returns the key of this setting in the properties' file.
  */
 public String getName()
 {
  return name;
 }

 /**
  * Tells whether the configuration contains a value for
  * this setting. This is always true for
  * {@link #isRequired() required settings}. 
  */
 public boolean isSet()
 {
  return null != valueString;
 }

 /**
  * Tells whether this setting must not be stored in memory.
  */
 public boolean isTransient()
 {
  return false;
 }

 /**
  * Tells whether the callers that {@link #getValue() query this setting}
  * expect it to always have a value. A value obtained from the 
  * {@link Configuration#setDefaultsResource(String) defaults resource},
  * if any, satisfies this requirement as well as an explicit
  * value from the configuration file.
  * @see #setRequired(boolean)
  * @see #getValue()
  */
 public boolean isRequired()
 {
  return required;
 }

 /**
  * Configures this object to throw an exception when its setting is
  * not present in the configuration file AND there is no default
  * value configured for that setting. 
  * @param required whether or not this setting must be configured
  * @see #isRequired()
  * @see #getValue()
  */
 public void setRequired(boolean required)
 {
  this.required = required;
 }

 /**
  * Returns the {@link Pattern regular expression} that this setting's
  * values must match.
  */
 public String getConstraintPattern()
 {
  return null == constraintPattern ? null : constraintPattern.pattern();
 }

 /**
  * Imposes a {@link Pattern regular expression} check on this setting's
  * values.
  * @param constraintPattern the regular expression to test setting's
  * values against 
  */
 public void setConstraintPattern(String constraintPattern)
 {
  this.constraintPattern = null == constraintPattern ? null : Pattern.compile(constraintPattern);
 }

 /**
  * Pass the name of a specific setting when creating a handler for it.
  * @param name the key of this setting in in the properties' file
  */
 public AbstractSetting(String name)
 {
  this.name = name;
 }

 @Override
 public String toString()
 {
  return "Setting \"" + name + "\" of type " + getType().getName();
 }

 protected static final String SQL_LITERAL_PATTERN = "[\\w\\^\\&\\|\\]\\[\\\\~!@#$%*()<>?,./;:{}]+";
 
 protected String valueString;
 protected Pattern constraintPattern;
 private String name;
 private boolean required;
}
