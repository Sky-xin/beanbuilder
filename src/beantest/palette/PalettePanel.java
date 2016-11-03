/*
 * @(#)PalettePanel.java	1.3 99/10/18
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

import java.awt.Component;

import java.awt.event.MouseListener;

import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.ButtonGroup;

/**
 * Encapsulates a set of components in a panel.
 *
 * @version 1.3 10/18/99
 * @author  Mark Davidson
 */
public class PalettePanel extends JPanel {

    private ButtonGroup group;
    
    /** The name of this palette */
    private String paletteName;
    
    // For Status bar messages
    private MouseListener listener;
    
    // Mapping between PaletteItems and PaletteButtons
    private HashMap items;

    /** 
     * Creates a palette with the name.
     */
    public PalettePanel(String name)  {
        setPaletteName(name);
        group = new ButtonGroup();
        items = new HashMap();
    }
    
    /** 
     * @param mouse MouseListener which handles mouse over (for status bar);
     */
    public PalettePanel(MouseListener mouse, String name, String[] components) {
        this(name);

        setMouseListener(mouse);
        
        for (int i = 0; i < components.length; i++) {
            try {
                addItem(new PaletteItem(components[i]));
            } catch (Exception ex) {
                // if there is an error creating the item it wont be added
                // to the palette. Continue.
                System.out.println("Palette constructor Error adding " + components[i]);
            }
        }
    }
    
    /** 
     * Adds a PaletteItem to the panel. The mouse listener is registered with
     * the button.
     */    
    public void addItem(PaletteItem item)  {
        PaletteButton button = new PaletteButton(item);

        if (listener != null)
            button.addMouseListener(listener);
            
        items.put(item, button);
        group.add(button);
        add(button);
    }
    
    public void removePaletteItem(PaletteItem item)  {
        PaletteButton button = (PaletteButton)items.get(item);

        if (button != null)  {
            if (listener != null)
                button.removeMouseListener(listener);
                
            items.remove(item);
            group.remove(button);
            remove(button);
        }
    }
    
    /** 
     * Sets the type of icon in the palette
     * @param icontype One of BeanInfo.ICON_... Contants which indicate which 
     *                 icon to use.
     * Note: It may be better to have the Palette listen to the RadioButton menu
     * command instead of tunneling from BeanTest.
     */
    public void setIconType(int icontype)  {
        Component[] comps = getComponents();
        
        PaletteButton button;
        for (int i = 0; i < comps.length; i++) {
            button = (PaletteButton)comps[i];
            button.setIconType(icontype);
        }
    }

    public void setPaletteName(String paletteName) {
        this.paletteName = paletteName;
    }
    
    public String getPaletteName() {
        return paletteName;
    }
    
    public void setMouseListener(MouseListener listener)  {
        this.listener = listener;
    }
}

