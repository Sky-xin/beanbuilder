/*
 * Copyright 1998 Sun Microsystems, Inc. All Rights Reserved
 */

/*
 * $Id: AbstractItemAction.java,v 1.2 1999/02/08 23:54:55 davidson Exp $
 */

package com.sun.java.swing.action;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;

/**
 * Adds support for item events like Toggle buttons and Checkbox menu items.
 * The developer should subclass this abstract class and define the
 * <code>actionPerformed</code> and <code>itemStateChanged</code> methods.
 *
 * @version $Revision: 1.2 $
 * @author  Mark Davidson
 */
public abstract class AbstractItemAction extends AbstractAction
                    implements ItemListener, ActionConstants {
    
    protected boolean selected = false;
    
    /** 
     * Returns true if the action is selcted.
     */
    public boolean isSelected()  {
        return selected;
    }
    
    /** 
     * Changes the state of the action
     * @param newValue true to set the selection state of the action.
     */
    public synchronized void setSelected(boolean newValue) {
        boolean oldValue = this.selected;
        this.selected = newValue;
        
        firePropertyChange("selected", new Boolean(oldValue), 
                                    new Boolean(newValue));
    }
}