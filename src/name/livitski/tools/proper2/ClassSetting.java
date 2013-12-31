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
 * Reads a full class name from the configuration file
 * and loads that class in the current context.
 * @param <D> the class that implements a specific setting
 */
@SuppressWarnings("unchecked")
public abstract class ClassSetting<D> extends AbstractSetting<D, Class>
{
 @Override
 public Class<Class> getType()
 {
  return Class.class;
 }

 @Override
 public Class getValue() throws ConfigurationException
 {
  try
  {
   return Class.forName((String)valueString, true, Thread.currentThread().getContextClassLoader());
  }
  catch (ClassNotFoundException e)
  {
   throw new ConfigurationException(
     this + " could not find class: " + valueString, e);
  }
 }

 public ClassSetting(String name)
 {
  super(name);
 }
}
