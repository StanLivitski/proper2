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

/**
 * Parses and stores a setting with a float value.
 */
public abstract class FloatSetting<D> extends AbstractSetting<D, Float>
{
 /**
  * @param name the key of this setting in the configuration file
  */
 public FloatSetting(String name)
 {
  super(name);
 }

 @Override
 public Class<Float> getType()
 {
  return Float.class;
 }

 @Override
 public Float getValue() throws ConfigurationException
 {
  try
  {
   return Float.valueOf((String)valueString);
  }
  catch (NumberFormatException e)
  {
   throw new ConfigurationException(
     this + " encountered an invalid floating-point number \"" + valueString + '"', e);
  }
 }
}
