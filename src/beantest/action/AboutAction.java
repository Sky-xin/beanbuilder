/*
 * @(#)AboutAction.java	1.1 99/10/18
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
package beantest.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

/**
 * About Box Action.
 *
 * @version 1.1 10/18/99
 * @author  Mark Davidson
 */
public class AboutAction extends AbstractAction implements BeanActionConstants {

    private BeantestActionManager manager;

    /** 
     * @param manager Delegate that the actionPerformed method.
     */
    public AboutAction(BeantestActionManager manager)  {
        this.manager = manager;
        
        putValue(Action.NAME, CMD_NAME_ABOUT);
        putValue(Action.SHORT_DESCRIPTION, CMD_SHORT_DESCRIPTION_ABOUT);
        putValue(Action.LONG_DESCRIPTION, CMD_LONG_DESCRIPTION_ABOUT);
        putValue(Action.MNEMONIC_KEY, new Integer(CMD_MNEMONIC_ABOUT));
        putValue(Action.ACTION_COMMAND_KEY, CMD_ACTION_ABOUT);
    }
    
    public void actionPerformed(ActionEvent evt)  {
        manager.forwardAction(new ActionEvent(evt.getSource(), evt.getID(), 
                                (String)getValue(Action.ACTION_COMMAND_KEY)));
    }
}
