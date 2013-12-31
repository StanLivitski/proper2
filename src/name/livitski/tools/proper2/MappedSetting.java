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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Parses and stores a setting that can take values from an array, a
 * set, or a map that links stored string values to actual values
 * retrieved from the property. This class copies the structure of all
 * possible values passed to its constructor. Therefore, subsequent
 * changes to that structure
 * do not affect the values this object will recognize. 
 * @param E the type of possible values of this setting
 */
public abstract class MappedSetting<D, E>
	extends AbstractSetting<D, E>
{
 /**
  * Constrains the possible property values to string representations of elements
  * of a set, as returned by calling {@link #toString()} on those elements.
  * @param name the property name
  * @param type common supertype of all possible values this setting may have
  * @param possibleValues the set of all possible values this setting may have
  */
 public MappedSetting(String name, Class<E> type, Set<E> possibleValues)
 {
  super(name);
  this.type = type;
  this.possibleValuesMap = new HashMap<String, E>(possibleValues.size(), 1f);
  for (E value : possibleValues)
  {
   if (null != this.possibleValuesMap.put(value.toString(), value))
    throw new IllegalArgumentException("Value "
      + value + " of setting '" + name
      + "' has a non-unique string form among possible values of that setting.");
  }
 }

 /**
  * Constrains the possible property values to string representations of elements
  * of an array, as returned by calling {@link #toString()} on those elements.
  * @param name the property name
  * @param possibleValues the set of all possible values this setting may have
  */
 @SuppressWarnings("unchecked")
 public MappedSetting(String name, E[] possibleValues)
 {
  super(name);
  this.type = (Class<E>)possibleValues.getClass().getComponentType();
  this.possibleValuesMap = new HashMap<String, E>(possibleValues.length, 1f);
  for (E value : possibleValues)
  {
   if (null != this.possibleValuesMap.put(value.toString(), value))
    throw new IllegalArgumentException("Value "
      + value + " of setting '" + name
      + "' has a non-unique string form among possible values of that setting.");
  }
 }

 /**
  * Constrains the possible property values to elements of a map with string 
  * keys representing property values that are converted to those elements.
  * @param name the property name
  * @param type common supertype of all possible retrieved values of this setting
  * @param possibleValuesMap the map of all possible property values to
  * retrieved values of this setting
  */
 public MappedSetting(String name, Class<E> type, Map<String,E> possibleValuesMap)
 {
  super(name);
  this.type = type;
  this.possibleValuesMap = new HashMap<String, E>(possibleValuesMap.size(), 1f);
  this.possibleValuesMap.putAll(possibleValuesMap);
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
  E value = possibleValuesMap.get(valueString);
  if (null == value)
   throw new ConfigurationException(
     "Unrecognized value of setting \"" + getName() + "\": " + valueString);
  return value;
 }

 private Class<E> type;
 private Map<String,E> possibleValuesMap;
}
