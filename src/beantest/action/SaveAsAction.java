/*
 * @(#)SaveAsAction.java	1.1 99/11/18
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
 * SaveAsAction
 *
 * @version 1.1 11/18/99
 * @author  Mark Davidson
 */
public class SaveAsAction extends AbstractAction implements BeanActionConstants {

    private BeantestActionManager manager;

    /** 
     * @param manager Delegate that the actionPerformed method.
     */
    public SaveAsAction(BeantestActionManager manager)  {
        this.manager = manager;
        
//        putValue(Action.SMALL_ICON, manager.getIcon(CMD_SMALL_ICON_SAVEAS));
        putValue(Action.NAME, CMD_NAME_SAVEAS);
        putValue(Action.SHORT_DESCRIPTION, CMD_SHORT_DESCRIPTION_SAVEAS);
        putValue(Action.LONG_DESCRIPTION, CMD_LONG_DESCRIPTION_SAVEAS);
        putValue(Action.MNEMONIC_KEY, new Integer(CMD_MNEMONIC_SAVEAS));
        putValue(Action.ACTION_COMMAND_KEY, CMD_ACTION_SAVEAS);
    }
    
    public void actionPerformed(ActionEvent evt)  {
        manager.forwardAction(new ActionEvent(evt.getSource(), evt.getID(), 
                                (String)getValue(Action.ACTION_COMMAND_KEY)));
    }
}
