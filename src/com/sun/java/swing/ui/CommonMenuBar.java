/*
 * %W% %E%
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
package com.sun.java.swing.ui;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.sun.java.swing.action.AbstractItemAction;
import com.sun.java.swing.action.ActionManager;

/**
 * Common menu bar for an ActionManager.
 *
 * To add a new Action/command:
 * 1. Create an AbstractAction subclass.
 * 2. Create and add the Action to the ActionManager.
 * 3. Insert the command into the ui.
 *
 * @version %I% %G%
 * @author  Mark Davidson
 */
public abstract class CommonMenuBar extends JMenuBar {
    
    // Handles the interactions.
    protected ActionManager manager;
    
    /**
     * ctor
     *
     * @param manager ActionManager
     */
    public CommonMenuBar(ActionManager manager) {
        this.manager = manager;
        
        configureMenu();
    }

    /** 
     * Subclasses must implement this method to configure their menu.
     */
    protected abstract void configureMenu();

    /**
     * Allows subclasses access to the action manager.
     */
    protected ActionManager getManager() {
        return manager;
    }
    /** 
     * Helper method to ensure that menu characteristics are consistent.
     * @param name Name of the menu
     * @param mnemonic Accelerator 
     */    
    protected JMenu createMenu(String name, char mnemonic)  {
        JMenu menu = new JMenu(name);
        menu.setMnemonic(mnemonic);
        
        return menu;
    }
    
    /** 
     * Helper method to configure each item consistantly
     */
    protected void addMenuItem(JMenu menu, Action action)  {
        JMenuItem menuItem = menu.add(action);
        
        configureMenuItem(menuItem, action);
    }
    
    /** 
     * Helper method to add a checkbox menu item
     */
    protected void addCheckBoxMenuItem(JMenu menu, AbstractItemAction a)  {
        addCheckBoxMenuItem(menu, a, false);
    }

    /** 
     * Helper method to add a checkbox menu item.
     * NOTE: Uses JDK 1.3 setAction methods indirectly in the constructor
     * of the menu item.
     * Note: There is a bug here. The PropertyChangeListener that gets added
     * to the Action doesn't know how to handle the "selected" property change
     * in the meantime, the corect thing to do is to add another PropertyChangeListener
     * to the AbstractItemAction until this is fixed.
     */
    protected void addCheckBoxMenuItem(JMenu menu, AbstractItemAction a,
                            boolean selected)  {
        JCheckBoxMenuItem mi = new JCheckBoxMenuItem(a);
        mi.addItemListener(a);
        mi.setSelected(selected);
        menu.add(mi);

        configureMenuItem(mi, a);
    }
    
    /** 
     * Helper method to add a radio button menu item.
     */
    protected void addRadioButtonMenuItem(JMenu menu, ButtonGroup group, Action a)  {
       addRadioButtonMenuItem(menu, group, a, false);
    }
    
    /** 
     * Helper method to add a radio button menu item.
     * Note: There is a bug here. The PropertyChangeListener that gets added
     * to the Action doesn't know how to handle the "selected" property change
     * in the meantime, the corect thing to do is to add another PropertyChangeListener
     * to the AbstractItemAction until this is fixed.
     */
    protected void addRadioButtonMenuItem(JMenu menu, ButtonGroup group, 
                        Action a, boolean selected)  {
        JRadioButtonMenuItem mi = new JRadioButtonMenuItem(a);
        mi.setSelected(selected);
        menu.add(mi);
        group.add(mi);

        configureMenuItem(mi, a);
    }
    
    /** 
     * Helper method to configure the menu item.
     */
    private void configureMenuItem(JMenuItem menuItem, Action action)  {
        // XXX - setMnemonic not needed in JDK 1.3 RC 1. Taken care of in
        // add(Action ). Required in 1.3 beta
        Integer mnemonic = (Integer)action.getValue(Action.MNEMONIC_KEY);
        if (mnemonic != null)
            menuItem.setMnemonic(mnemonic.intValue());
        
        // The name is the key into the ActionManager action list.
        menuItem.setName((String)action.getValue(Action.NAME));

        // The manager listens to mouse events 
        menuItem.addMouseListener(manager);
    }
}