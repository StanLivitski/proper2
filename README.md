<!--
 |    Copyright © 2013 Konstantin "Stan" Livitski
 | 
 |    This file is part of proper2. Proper2 is
 |    licensed under the Apache License, Version 2.0 (the "License");
 |    you may not use this file except in compliance with the License.
 |    You may obtain a copy of the License at
 | 
 |      http://www.apache.org/licenses/LICENSE-2.0
 | 
 |    Unless required by applicable law or agreed to in writing, software
 |    distributed under the License is distributed on an "AS IS" BASIS,
 |    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 |    See the License for the specific language governing permissions and
 |    limitations under the License.
 -->

<a name="sec-about"> </a>
What is proper2?
================
Proper2 is a Java library that simplifies and organizes access to configuration
files by an application. Proper2 assumes that configuration files it has to read
are formatted as [Java properties][properties]. The library addresses common
concerns related to the configuration settings, including:

 - Conversion of configured values from text to the relevant data type 
 - Validation of configured values against application-specific constraints
 - Substitution of the default values for omitted settings
 - Caching of validated and converted data

Proper2 uses [generics][] to simplify access to configuration settings and requires
the host application to define a separate handler class for each setting. It
provides a number of template handlers to address typical setting types and
usage patterns.

<a name="sec-depends"> </a>
Project's dependencies
======================

Proper2 uses the [Apache Commons Logging][commons-logging] library. You must
have that library on the classpath both when compiling proper2 and using it.
The code has been tested with version `1.1.1` of the library.

<a name="sec-download"> </a>
Downloading the binary and Javadoc
==================================

The binary and compressed Javadoc pages of the proper2 library will be available at:

 - <https://github.com/StanLivitski/proper2/wiki/Download>

<a name="sec-use"> </a>
Using proper2
=============

<a name="sec-handlers"> </a>
Defining handlers for settings
------------------------------

Proper2 expects applications to define concrete classes for all configuration
settings they need to access. These classes are called _handler classes_. All
handler classes inherit from `name.livitski.tools.proper2.AbstractSetting`.
A handler class supplies proper2 with the following information:
 
 - name of the configuration setting (property) that it processes
 - data type of the values that the setting can have
 - any additional constraints the setting's values must comply with
 - how the setting's values must be transformed before the application
 receives them
 - whether or not the setting must have a value for the application to work
 - whether or not the library is allowed to cache the handler (along with
 the value of its respective setting it may contain) 
 
Each concrete handler class must have a default (no-args) constructor. 

In most cases, applications do not have to provide all of the above information
for each setting they have. Proper2 has a number of template handler superclasses
to address typical configuration needs. For example, a handler for boolean setting
may simply inherit from the template class `name.livitski.tools.proper2.BooleanSetting`
and initialize it with the name of respective configuration file property: 

>     import name.livitski.tools.proper2.BooleanSetting;
>
>     /**
>      * The handler for <code>to.be.or.not.to.be</code> setting.
>      */
>     public class HamletSetting extends
>       BooleanSetting<HamletSetting>
>     {
>      public static final String NAME = "to.be.or.not.to.be";
>
>      public HamletSetting()
>      {
>       super(NAME);
>       setRequired(true);
>      }
>     }

Having this handler, you might expect a line like

>     to.be.or.not.to.be = true

in the configuration file.

Note that handlers' superclasses have a type parameter that points to the
concrete class that handles a setting. Also observe the use of `setRequired()`
method defined in `name.livitski.tools.proper2.AbstractSetting` class to
indicate that proper2 cannot return `null` to the caller when the setting
is omitted from the configuration. If your application is prepared to
handle `null` values in such cases, you should omit that call.

Please refer to the project's [javadoc][] for additional details about the
general contract of a setting's handler and the template handlers included
with proper2.

Note that `name.livitski.tools.proper2.AbstractSetting` does not contain a
mechanism for providing default values of settings that aren't configured
explicitly. Proper2 does not require handler objects to deal with
default settings. Instead, the library allows you to bundle a resource that
contains default values of the settings with the application, as explained
[below](#sec-config-access).

<a name="sec-config-access"> </a>
Accessing the configuration with proper2
----------------------------------------

Applications read the configuration settings with proper2 by querying an
object of class `name.livitski.tools.proper2.Configuration`. One way to
obtain such object is to create it, supplying the object being configured as
the argument to the constructor:

>     import name.livitski.tools.proper2.Configuration;
>     ...
>
>        Configuration config = new Configuration(myApp);

Then, you have to tell proper2 the location of your configuration file
and, in many cases, the location of resource that contains the default
settings.

>     import java.io.File;
>     ...
>
>        config.setConfigFile(new File("/etc/myapp.properties"));
>        config.setDefaultsResource(DEFAULTS);

The argument to `setDefaultsResource()` is a relative URL string
resolved against the class of an object provided to the
`Configuration` constructor. This allows you to bundle a file with
constant values of default settings with your application's distribution.

To read a configuration setting, call the `readSetting()` method of
the `Configuration` object. The argument is the class of your setting's
handler:

>      Boolean answer = config.readSetting(HamletSetting.class);

Note that `readSetting()` takes its return type from a type parameter
of the handler class. For instance, since our `HamletSetting` is derived
from `AbstractSetting<HamletSetting,Boolean>`, the return type of
`readSetting(HamletSetting.class)` is `Boolean`, which eliminates the need
to cast it to the target type.

When you call `readSetting()` the first time, proper2 reads the configuration
file and the defaults resource and stores that data in memory (unless you
configure it otherwise). It also stores all setting handlers that it creates,
except for handlers marked _transient_.

Problems that occur while reading configuration files, validating or
transforming settings' values, are reported by throwing a `ConfigurationException`.
Though that is an unchecked exception, it is advisable to catch and handle it
explicitly in production code.

For more detailed proper2 API information, please consult the project's [javadoc][]. 

<a name="sec-repo"> </a>
About this repository
=====================

This repository contains the source code of proper2. Its top-level components are:

        src/           		proper2's source files
        lib/				an empty directory for placing links or copies of
        					 dependency libraries
        LICENSE		        Document that describes the project's licensing terms
        NOTICE   	        A summary of license terms that apply to proper2
        build.xml      		Configuration file for the tool (Ant) that builds
                       		 the binary and Javadoc
        README.md			This document

<a name="sec-building"> </a>
Building proper2
================

To build the binary from this repository, you need:

   - A **Java SDK**, also known as JDK, Standard Edition (SE), version 6 or
   later, available from OpenJDK <http://openjdk.java.net/> or Oracle
   <http://www.oracle.com/technetwork/java/javase/downloads/index.html>.

   Even though a Java runtime may already be installed on your machine
   (check that by running `java --version`), the build will fail if you
   don't have a complete JDK (check that by running `javac`).

   - **Apache Ant** version 1.7.1 or newer, available from the Apache Software
   Foundation <http://ant.apache.org/>.

   - [Dependency libraries](#sec-depends), or links thereto in the `lib/`
   subdirectory of your working copy.

To build proper2, go to the directory containing a working copy of project's
sources and run:

     ant

The result is a file named `proper2.jar` in the same directory. 


<a name="sec-javadoc"> </a>
Building Javadoc
================

To build the Javadoc for the project, make sure you have met the prerequisites
[listed above](#sec-building), go to the directory containing a working copy of
project's sources and run:

     ant javadoc

The result will be placed in the `javadoc` subdirectory. 

<a name="sec-contact"> </a>
Contacting the project's team
=============================

You can send a message to the project's team via the
[Contact page](http://www.livitski.com/contact) at <http://www.livitski.com/>
or via *GitHub*. We will be glad to hear from you!

   [properties]: http://docs.oracle.com/javase/6/docs/api/java/util/Properties.html#load(java.io.Reader)
   [generics]: http://docs.oracle.com/javase/6/docs/technotes/guides/language/generics.html
   [javadoc]: #sec-download
   [commons-logging]: http://commons.apache.org/proper/commons-logging/download_logging.cgi