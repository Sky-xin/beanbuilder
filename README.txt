Bean Builder
============

Introduction
------------

BeanBuilder is a simple builder that demonstrates the new persistance
mechanism. The new persistance scheme is a text based mechanism that 
allows for the long term storage and reconstruction of Java Bean object 
graphs.

BeanBuilder is also a demonstration of the following technologies:

* Design time meta information specified in javadoc format and generated as 
  NV pairs in BeanInfo classes. 

* Dynamic adapter generation to hook up events using the new JDK 1.3 Proxy API's. 
  See Dynamic Proxy Classes for details. 

* New JFC/Swing based Property Editors for editing: Fonts, Colors, Objects, 
  Dimension, Insets, Rectangles, etc,... 

* Actions architecture based on javax.swing.Action interface. See Adding Actions 
  to ActionEvent Soruces for details. 

* A simple Jar classloader based on java.net.URLClassLoader that 
  was introduced in JDK 1.2 

* Correct use of JFC/Swing TableModels and TreeModels. 

* Sorting and filtering using the Java collections classes introduced in JDK 1.2 

Watch the Swing Connection for upcoming articles about how BeanBuilder implements these
technologies.


JDK Version
-----------

The BeanBuilder requires JDK 1.3 beta or later to be installed in order
to run.

You will need an installation of JDK 1.3 beta or higher. You can get an 
early access of the JDK 1.3 from the Java Developer Connection.

http://developer.java.sun.com/developer/earlyAccess/j2sdk13/

You will need to register if you are not already a member of the JDC. 
Registration is free.

Running BeanBuilder
-------------------

You will need to use the design time jar file from the JDK in order to see
the full capabilities of the builder. This jar file is called "dt.jar" and is 
located in the lib directory beneath the root of the JDK installation. 
The BeanInfo meta information and icons are contained in dt.jar.

You may want to set the JAVA_HOME environment variable to point to the 
top of your JDK distribution. 

To run the example builder on a Windows platform double click on the runnit.bat 
file or start runnit.bat from a console window. 

To start the example builder on Solaris from the Bourne or Korn shells type 
the following:

   JAVA_HOME=/usr/local/jdk1.3 runnit.sh

replacing /usr/local/jdk1.3 with the path to your Java installation.
If you're using the csh then type this:

   (setenv JAVA_HOME /usr/local/java/jdk1.3/solaris; runnit.sh)

	

