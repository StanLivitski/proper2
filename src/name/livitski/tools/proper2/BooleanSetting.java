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


/**
 * Parses and stores a setting with a boolean value.
 * Case insensitive strings "true", "yes", "on", and "1" are
 * converted to true, strings "false", "no", "off", and "0" are
 * converted to false, other values are considered invalid.
 */
public abstract class BooleanSetting<D> extends AbstractSetting<D, Boolean>
{
 public BooleanSetting(String name)
 {
  super(name);
 }

 @Override
 public Class<Boolean> getType()
 {
  return Boolean.class;
 }

 @Override
 public Boolean getValue() throws ConfigurationException
 {
  if (null == valueString)
   return null;
  valueString = valueString.toLowerCase();
  if ("true".equals(valueString) || "yes".equals(valueString)
    || "on".equals(valueString) || "1".equals(valueString))
   return true;
  if ("false".equals(valueString) || "no".equals(valueString)
    || "off".equals(valueString) || "0".equals(valueString))
   return false;
  throw new ConfigurationException("Configuration setting \"" + getName()
    + "\" has an invalid boolean value: " + valueString);
 }
}
