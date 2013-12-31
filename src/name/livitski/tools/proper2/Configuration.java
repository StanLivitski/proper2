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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Provides access to {@link AbstractSetting configuration settings}
 * read from a {@link #getConfigFile() properties file} with optional
 * {@link #isCachingEnabled() caching} of the settings read.
 * @see #readSetting(Class)
 */
public class Configuration
{
 /**
  * Creates the settings container for a host class.
  * @param forClass the class of object that is configured using this
  * settings container
  */
 public Configuration(Class<?> forClass)
 {
  this.forClass = forClass;
  this.log = LogFactory.getLog(forClass);
 }

 /**
  * Creates the settings container for an object.
  * @param host the object that is configured using this
  * settings container
  */
 public Configuration(Object host)
 {
  this(host.getClass());
 }

 /**
  * Reads a specific setting from this container and converts it to
  * the appropriate data type. This method should not be called on
  * a {@link #isCachingEnabled() non-caching} settings container, since
  * it will read the entire properties file for each setting fetched.
  * When {@link #isCachingEnabled() caching is disabled}, use the
  * {@link #readSetting(Class, Properties) two-argument version} of this
  * method instead.
  * @param <T> the data type of the values contained in this setting  
  * @param <D> the class that defines the setting to be read
  * @param clazz the class that defines the setting to be read, as
  * a concrete {@link Class} object
  * @return the setting's value read
  * @throws ConfigurationException if there was an error reading the
  * settings or the setting read was invalid or, for a required setting,
  * missing
  */
 public <T, D extends AbstractSetting<D,T>> T readSetting(Class<D> clazz)
   throws ConfigurationException
 {
   return readSetting(clazz, readConfiguration());
 }

 /**
  * A version of {@link #readSetting(Class)} for use with
  * {@link #isCachingEnabled() non-caching} settings containers.
  * Call {@link #readConfiguration()} first, then use the return
  * value to read all the settings needed.
  * @param clazz the class that defines the setting to be read
  * @param config the object returned from {@link #readConfiguration()}
  */
 public <T, D extends AbstractSetting<D,T>> T readSetting(Class<D> clazz, Properties config)
 	throws ConfigurationException
 {
  D setting = findSetting(clazz, config);
  return setting.getValue();
 }

 /**
  * Returns a {@link AbstractSetting setting container} populated
  * with a value read from the configuration, or an
  * {@link AbstractSetting#isSet() empty container} if the setting
  * is not specified by the configuration. This method should not be
  * called on a {@link #isCachingEnabled() non-caching} settings
  * container, since it will read the entire properties file for
  * each setting fetched.
  * When {@link #isCachingEnabled() caching is disabled}, use the
  * {@link #findSetting(Class, Properties) two-argument version} of
  * this method instead.
  * @param <D> the class that defines the setting to be read
  * @param clazz the class that defines the setting to be read, as
  * a concrete {@link Class} object
  * @return the setting container of the requested class 
  * @throws ConfigurationException if there was an error reading the
  * settings or the setting read was invalid or, for a required setting,
  * missing
  */
 public <D extends AbstractSetting<D,?>> D findSetting(Class<D> clazz)
  throws ConfigurationException
 {
   return findSetting(clazz, readConfiguration());
 }

 /**
  * A version of {@link #findSetting(Class)} for use with
  * {@link #isCachingEnabled() non-caching} settings containers.
  * Call {@link #readConfiguration()} first, then use the return
  * value to read all the settings needed.
  * @param clazz the class that defines the setting to be read
  * @param config the object returned from {@link #readConfiguration()}
  */
 public <D extends AbstractSetting<D,?>> D findSetting(Class<D> clazz, Properties config)
   throws ConfigurationException
 {
  @SuppressWarnings("unchecked")
  D setting = (D)settings.get(clazz);
  if (null == setting)
  {
   try
   {
    setting = (D) clazz.newInstance();
   }
   catch (Exception e)
   {
    throw new IllegalArgumentException(
      "Could not create a setting handler for " + clazz, e);
   }
   setting.load(config);
   if (!setting.isTransient())
    settings.put(clazz, setting);
  }
  return setting;
 }

 /**
  * Reads this object's configuration settings and caches them for future use
  * if caching is enabled.
  * @return configuration settings map
  * @throws ConfigurationException if there was an error reading the
  * settings
  * @see #isCachingEnabled()
  */
 public Properties readConfiguration()
	throws ConfigurationException
 {
  Properties config = null;
  if (cachingEnabled && null != configCache)
   config = configCache.get();
  if (null == config)
  {
   config = readConfigurationFromFile();
   if (cachingEnabled)
    configCache = new SoftReference<Properties>(config);
  }
  return config;
 }

 /**
  * Reads configuration settings from a properties file with optional
  * defaults.
  * @return configuration settings map
  * @throws ConfigurationException if there was an error reading the
  * settings
  */
 protected Properties readConfigurationFromFile()
 	throws ConfigurationException
 {
  Reader input = null;
  try {
   final Properties defaults = new Properties();
   InputStream res = null == defaultsResource ? null : forClass.getResourceAsStream(defaultsResource);
   if (null != res)
   {
    input = new InputStreamReader(res);
    try
    {
     defaults.load(input);
     input.close();
     input = null;
    }
    catch (IOException ioerr)
    {
     throw new ConfigurationException(
       "Error reading configuration defaults from " + forClass.getResource(defaultsResource),
       ioerr);
    }
   }
   Properties config = new Properties(defaults);
   if (null != configFile)
    try
    {
     input = new FileReader(configFile);
     config.load(input);
     input.close();
     input = null;
    }
    catch (IOException ioerr)
    {
     throw new ConfigurationException(
       "Error reading configuration file " + configFile,
       ioerr);
    }
   return config;
  }
  finally
  {
   if (null != input)
    try { input.close(); }
    catch(Exception fail)
    {
     log.warn("Could not close a configuration file: " + fail.getMessage(), fail);
    }
  }
 }
 
 public File getConfigFile()
 {
  return configFile;
 }

 /**
  * This operation resets the config cache, if any.
  * @see #isCachingEnabled()
  */
 public void setConfigFile(File configFile)
 {
  this.configCache = null;
  this.configFile = configFile;
 }

 /**
  * Returns the resource location used to set defaults for this object's
  * configuration settings, if any. The location is resolved relatively
  * to the {@link #Configuration(Class) host class}. Returns
  * <code>null</code> if this object has no default settings.
  * Returns {@link #DEFAULT_DEFAULTS_RESOURCE} when uninitialized. 
  */
 public String getDefaultsResource()
 {
  return defaultsResource;
 }

 public static final String DEFAULT_DEFAULTS_RESOURCE = "config/defaults.properties";

 /**
  * Changes the resource location used to set defaults for this object's
  * configuration settings. The location is resolved relatively
  * to the {@link #Configuration(Class) host class}. Set to
  * <code>null</code> to clear any default settings.
  * This operation resets the configuration cache, if any.
  * @see #isCachingEnabled()
  */
 public void setDefaultsResource(String defaultsResource)
 {
  this.configCache = null;
  this.defaultsResource = defaultsResource;
 }

 /**
  * Tells whether this object caches properties that it reads.
  * The default is <code>true</code>.
  */
 public boolean isCachingEnabled()
 {
  return cachingEnabled;
 }

 /**
  * This operation resets the config cache, if any.
  * @see #isCachingEnabled()
  */
 public void setCachingEnabled(boolean cachingEnabled)
 {
  this.configCache = null;
  this.cachingEnabled = cachingEnabled;
 }
 
 private Reference<Properties> configCache;
 private File configFile;
 private Class<?> forClass;
 private String defaultsResource = DEFAULT_DEFAULTS_RESOURCE;
 private boolean cachingEnabled = true;
 private Log log;
 @SuppressWarnings("unchecked")
 private Map<Class<? extends AbstractSetting>, AbstractSetting> settings
	= new HashMap<Class<? extends AbstractSetting>, AbstractSetting>(); 
}
