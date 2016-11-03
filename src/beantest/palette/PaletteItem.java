/*
 * @(#)PaletteItem.java	1.5 99/11/09
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
package beantest.palette;

import java.awt.Image;

import java.beans.BeanInfo;
import java.beans.BeanDescriptor;
import java.beans.Beans;

import java.io.IOException;

import beantest.util.BeanInfoFactory;
import beantest.util.JarClassLoader;

/**
 * Encapsulates an item that can be instantiated by the tool
 * A palette item also knows how to instantiate itself.
 * 
 * Contains most of the BeanInfo fields
 *
 * @version 1.5 11/09/99
 * @author Mark Davidson
 */
public class PaletteItem {
    private String beanName;
    private Class beanClass;

    private BeanInfo info;
    private BeanDescriptor descriptor;
    
    /**
     * Constructs a palette item from the fully qualified bean name
     *
     * @param beanName - The name of the bean that this item represents.
     *      i.e. javax.swing.JButton
     *
     * @exception java.lang.ClassNotFoundException
     */
    public PaletteItem(String beanName) throws ClassNotFoundException {
        this.beanName = beanName;

        beanClass = Class.forName(beanName);
        info = BeanInfoFactory.getBeanInfo(beanClass);
        descriptor = info.getBeanDescriptor();
    }
    
    /** 
     * This is the ctor to call for creating a PaletteItem from a serialized
     * bean.
     * @param obj An instance of the deserizizd bean.
     * @param beanName The name of the resouce without the extension 
     *                 i.e, sun.demo.buttons.OrangeButton
     */
    public PaletteItem(Object obj, String beanName)  {
        this.beanName = beanName;
        
        beanClass = obj.getClass();
        info = BeanInfoFactory.getBeanInfo(beanClass);
        descriptor = info.getBeanDescriptor();
        
        // Should tweak the contents of the BeanDescriptor since the
        // BeanInfo will reflect the base class 
        descriptor.setName(beanName);
        descriptor.setShortDescription("Serialized Object: " + beanName);
        
        String shortName = beanName;
        int ix = beanName.lastIndexOf('.');
        if (ix >= 0) {
		    shortName = beanName.substring(ix+1);
		}
        descriptor.setDisplayName(shortName);
    }
    
    /** 
     * Constructs a palette item from a class.
     * @param cls - A valid java bean class.
     */
    public PaletteItem(Class cls)  {
        beanClass = cls;
        beanName = cls.getName();
        
        info = BeanInfoFactory.getBeanInfo(beanClass);
        descriptor = info.getBeanDescriptor();
    }

    /**
     * Creates a new bean from the encapsulated class. Sets some initial values
     * from the method description such as setText.
     * Usually called from the Palette class.
     *
     * @exception java.lang.ClassNotFoundException
     * @exception java.io.IOException
     * @see java.beans.Beans#instantiate
     */
    public Object makeNewBean() throws IOException, ClassNotFoundException {
        return Beans.instantiate(JarClassLoader.getJarClassLoader(), beanName);
    }

    // getter methods 
    public String getBeanName() {
        return beanName;
    }
        
    public Class getBeanClass() {
        return beanClass;
    }
    
    public BeanInfo getBeanInfo()  {
        return info;
    }
    
    public BeanDescriptor getBeanDescriptor()  {
        return descriptor;
    }
        
    /** 
     * Retrieves the icon for the beaninfo
     * @param type - One of BeanInfo.ICON_
     */
    public Image getIcon(int type) {
        return info.getIcon(type);
    }
    
    public String getName() {
        return descriptor.getName();
    }
    
    public String getDisplayName() {
        return descriptor.getDisplayName();
    }
    
    public String getShortDescription() {
        return descriptor.getShortDescription();
    }
    
    public Class getCustomizerClass() {
        return descriptor.getCustomizerClass();
    }
}
