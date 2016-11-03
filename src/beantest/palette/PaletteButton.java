/*
 * @(#)PaletteButton.java	1.4 99/11/09
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

import java.awt.Dimension;
import java.awt.Image;

import java.beans.BeanInfo;

import javax.swing.JToggleButton;
import javax.swing.ImageIcon;

/** 
 * A single button within the palette.  It signifies a single type of object
 * that can be part of a user interface created by a user in the designer.
 *
 * @version 1.4 11/09/99
 * @author Mark Davidson
 */
public class PaletteButton extends JToggleButton {
    private PaletteItem item; // encapsulated item
    
    // Default constructor
    public PaletteButton()
                throws ClassNotFoundException {
        this("javax.swing.JButton");
    }

    /**
     * Ctor, Creates a PaletteButton
     * @param beanName Full path to the Bean. i.e, "javax.swing.JButton"
     */
    public PaletteButton(String beanName)
                    throws ClassNotFoundException {
        item = new PaletteItem(beanName);
        
        setToolTipText(item.getName());
        setIconType(BeanInfo.ICON_COLOR_32x32);
    }

    /** 
     * Ctor, Creates a PaletteButton from a PaletteItem
     */    
    public PaletteButton(PaletteItem item)  {
        this.item = item;
        setToolTipText(item.getName());
        setIconType(BeanInfo.ICON_COLOR_32x32);
    }

    public Dimension getMinimumSize() {
        return new Dimension(40, 40);
    }

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    /** 
     * Returns the encapsulated PaletteItem
     */
    public PaletteItem getPaletteItem() {
        return item;
    }

    /** 
     * Sets the type of icon in the palette
     * @param icontype One of BeanInfo.ICON_... Contants which indicate which 
     *                 icon to use.
     */
    public void setIconType(int icontype)  {
        Image image = item.getIcon(icontype);
        if(image != null)
            setIcon(new ImageIcon(image));
    }

    /**
     * Converts this object to a String representation
     * @return a string representation of this object
     */
    public String toString() {
        return getClass().getName() + "[item=" + item.getName() + "]"; 
    }
}


