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

import java.io.File;

/**
 * Reads a {@link File file or directory} location from the
 * configuration file.
 * @param <D> the class that implements a specific setting
 */
public abstract class FileSetting<D> extends AbstractSetting<D, File>
{
 @Override
 public Class<File> getType()
 {
  return File.class;
 }

 @Override
 public File getValue() throws ConfigurationException
 {
  return null == valueString ? null : new File(valueString);
 }

 public FileSetting(String name)
 {
  super(name);
 }
}
