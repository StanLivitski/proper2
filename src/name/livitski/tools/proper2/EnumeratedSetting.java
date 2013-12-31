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
 * Parses and stores a setting with an enumerated value. This
 * parser assumes that enumerated constants have all-uppercase
 * names and converts the string to the upper case before looking
 * up the target constant.
 * @param E the type that enumerates possible values of this setting
 */
public abstract class EnumeratedSetting<D, E extends Enum<E>>
	extends AbstractSetting<D, E>
{
 public EnumeratedSetting(String name, Class<E> type)
 {
  super(name);
  this.type = type;
 }

 @Override
 public Class<E> getType()
 {
  return type;
 }

 @Override
 public E getValue() throws ConfigurationException
 {
  if (null == valueString)
   return null;
  try
  {
   return Enum.valueOf(type, valueString.toUpperCase());
  }
  catch (IllegalArgumentException badValue)
  {
   throw new ConfigurationException(
     "Unrecognized value of setting \"" + getName() + "\": " + valueString, badValue);
  }
 }

 private Class<E> type;
}
