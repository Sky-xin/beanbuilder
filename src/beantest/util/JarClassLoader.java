/*
 * @(#)JarClassLoader.java	1.2 99/10/21
 *
 * Copyright 1999 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package beantest.util;

import java.io.IOException;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.jar.Attributes;

/**
 * A class loader for loading jar files, both local and remote.
 * Adapted from the Java Tutorial.
 *
 * http://java.sun.com/docs/books/tutorial/jar/api/index.html
 * 
 * @version 1.2 10/21/99
 * @author Mark Davidson
 */
public class JarClassLoader extends URLClassLoader {

    // These manifest attributes were left out of Attributes.Name
    // They have to go somewhere so the chaces are if you need them,
    // then you are playing with this class loader.
    public static final Attributes.Name Attributes_Name_JAVA_BEAN = new Attributes.Name("Java-Bean");
    public static final Attributes.Name Attributes_Name_NAME = new Attributes.Name("Name");

    private static JarClassLoader loader = null;
    
    /** 
     * Null ctor DO NOT USE! This will result in an NPE if the class loader is
     * used. So this class loader isn't really Bean like.
     */
    public JarClassLoader()  {
        this(null);
    }

    /**
     * Creates a new JarClassLoader for the specified url.
     *
     * @param url The url of the jar file i.e. http://www.xxx.yyy/jarfile.jar
     *            or file:c:\foo\lib\testbeans.jar
     */
    public JarClassLoader(URL url) {
        super(new URL[] { url });
    }
    
    /** 
     * Adds the jar file with the following url into the class loader. This can be 
     * a local or network resource.
     * 
     * @param url The url of the jar file i.e. http://www.xxx.yyy/jarfile.jar
     *            or file:c:\foo\lib\testbeans.jar
     */
    public void addJarFile(URL url)  {
        addURL(url);
    }

    /** 
     * Adds a jar file from the filesystems into the jar loader list.
     * 
     * @param jarfile The full path to the jar file.
     */
    public void addJarFile(String jarfile)  {
        try {
            URL url = new URL("file:" + jarfile);
            addURL(url);
        } catch (IOException ex) {
            // XXX - debug
            System.out.println("JarClassLoader. Bad URL: " + jarfile);
        }
    }
    
    //
    // Static methods for handling the shared instance of the JarClassLoader.
    //

    /** 
     * Returns the shared instance of the class loader.
     */
    public static JarClassLoader getJarClassLoader()  {
        return loader;
    }
    
    /** 
     * Sets the static instance of the class loader.
     */
    public static void setJarClassLoader(JarClassLoader cl)  {
        loader = cl;
    }
}
