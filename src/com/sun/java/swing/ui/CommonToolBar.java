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

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import com.sun.java.swing.action.AbstractItemAction;
import com.sun.java.swing.action.ActionManager;

/**
 * A configurable toolbar which uses the action manager.
 *
 * @version %I% %G%
 * @author  Mark Davidson
 */
public abstract class CommonToolBar extends JToolBar {
    
    // Handles the interactions.
    protected ActionManager manager;
    
    private Dimension buttonSize;
    private Insets buttonInsets;

    /**
     * ctor
     *
     * @param manager The action manager which manages interactions.
     */
    public CommonToolBar(ActionManager manager) {
        this.manager = manager;

        buttonSize = new Dimension(CommonUI.buttconPrefSize);
        buttonInsets = new Insets(0,0,0,0);
        
        addButtons();
    }
    
    /** 
     * Adds the buttons to the toolbar. Subclasses must implement this method
     */
    protected abstract void addButtons();

    /** 
     * Returns the ActionManager to subclasses.
     */
    protected ActionManager getManager()  {
        return manager;
    }
    
    /** 
     * Adds and configures the button based on the action. 
     */
    protected void addButton(Action action)  {
        JButton button = add(action);

        configureButton(button, action);
    }
    
    /** 
     * Adds and configures a toggle button.
     * Note: Passing an Action as an argument to the ctor of JToggleButton is
     * new in JDK 1.3.
     * Note: There is a bug here. The PropertyChangeListener that gets added
     * to the Action doesn't know how to handle the "selected" property change
     * in the meantime, the corect thing to do is to add another PropertyChangeListener
     * to the AbstractItemAction until this is fixed.
     * @param a An AbstractItemAction which is an abstraction of a toggle action.
     */
    protected void addToggleButton(AbstractItemAction a)  {
        JToggleButton button = new JToggleButton(a);
        button.addItemListener(a);
        button.setSelected(a.isSelected());
        
        add(button);

        configureButton(button, a);
    }

    /** 
     * Helper method to configure the button 
     */
    private void configureButton(AbstractButton button, Action action)  {
	    button.setToolTipText((String)action.getValue(Action.NAME));
        // XXX - setMnemonic not needed in JDK 1.3 RC 1. Taken care of in
        // add(Action ). Required in 1.3 beta
        button.setMnemonic(((Integer)action.getValue(Action.MNEMONIC_KEY)).intValue());
        
        // Don't show the text under the toolbar buttons.
        button.setText("");
        
        // The name is the key into the ActionManager action list.
        // XXX - This doesn't work with the new setAction/configureProperties stuff.
        // The new mechanism assumes that Action.NAME is the name of the text. should
        // use new key.
        button.setName((String)action.getValue(Action.NAME));
        button.setAlignmentY(0.5f);
        
	    button.setMargin(buttonInsets);
        
        button.setSize(buttonSize);
        
        // Grrrr. Basic button UI does't use these values!!
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);

        // The action manager will set the status bar.
        button.addMouseListener(manager);
    }
}
