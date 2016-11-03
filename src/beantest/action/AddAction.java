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

package beantest.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * Adds an object to the design panel.
 *
 * @version %I% %G%
 * @author  Mark Davidson
 */
public class AddAction extends AbstractAction implements BeanActionConstants {

    private BeantestActionManager manager;
    
    /**
     * @param manager Delegate that the actionPerformed method.
     */
    public AddAction(BeantestActionManager manager) {
        this.manager = manager;
        
        putValue(Action.NAME, CMD_NAME_ADD);
        putValue(Action.SMALL_ICON, manager.getIcon(CMD_SMALL_ICON_ADD));
        putValue(Action.SHORT_DESCRIPTION, CMD_SHORT_DESCRIPTION_ADD);
        putValue(Action.LONG_DESCRIPTION, CMD_LONG_DESCRIPTION_ADD);
        putValue(Action.MNEMONIC_KEY, new Integer(CMD_MNEMONIC_ADD));
        putValue(Action.ACTION_COMMAND_KEY, CMD_ACTION_ADD);
    }
    
    public void actionPerformed(ActionEvent evt)  {
        manager.forwardAction(new ActionEvent(evt.getSource(), evt.getID(), 
                                (String)getValue(Action.ACTION_COMMAND_KEY)));
    }
}
