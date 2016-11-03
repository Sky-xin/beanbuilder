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

package beantest;

import beantest.action.BeantestActionManager;

import com.sun.java.swing.ui.CommonToolBar;

/**
 * A toolbar for the Bean Builder
 *
 * @version %I% %G%
 * @author  Mark Davidson
 */
public class BeanToolBar extends CommonToolBar {
    
    /**
     * @param manager The ActionManager which has all the possible actions
     *                that this toolbar can handle
     */
    public BeanToolBar(BeantestActionManager manager) {
        super(manager);
    }
    
    /** 
     * Configures the toolbar
     */
    protected void addButtons()  {
        addButton(manager.getAction(BeantestActionManager.CMD_NAME_NEW));
        addButton(manager.getAction(BeantestActionManager.CMD_NAME_OPEN));
        addButton(manager.getAction(BeantestActionManager.CMD_NAME_SAVE));
        addButton(manager.getAction(BeantestActionManager.CMD_NAME_HELP));
    }

}