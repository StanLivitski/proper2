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

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Reads an {@link URI} name from the configuration file.
 * @param <D> the class that implements a specific setting
 */
public abstract class URISetting<D> extends AbstractSetting<D, URI>
{
 @Override
 public Class<URI> getType()
 {
  return URI.class;
 }

 @Override
 public URI getValue() throws ConfigurationException
 {
  try
  {
   return new URI(valueString);
  }
  catch (URISyntaxException e)
  {
   throw new ConfigurationException(
     this + " contains an invalid URI: " + valueString, e);
  }
 }

 public URISetting(String name)
 {
  super(name);
 }
}
