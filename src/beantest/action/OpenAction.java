/*
 * Copyright 10/01/99 Sun Microsystems, Inc. All Rights Reserved
 */

/*
 * @(#)OpenAction.java	1.2 99/10/01
 */

package beantest.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

/**
 * OpenAction 
 *
 * @version 1.2 10/01/99
 * @author  Mark Davidson
 */
public class OpenAction extends AbstractAction implements BeanActionConstants {

    private BeantestActionManager manager;

    /** 
     * @param manager Delegate that the actionPerformed method.
     */
    public OpenAction(BeantestActionManager manager)  {
        this.manager = manager;
        
        putValue(Action.SMALL_ICON, manager.getIcon(CMD_SMALL_ICON_OPEN));
        putValue(Action.NAME, CMD_NAME_OPEN);
        putValue(Action.SHORT_DESCRIPTION, CMD_SHORT_DESCRIPTION_OPEN);
        putValue(Action.LONG_DESCRIPTION, CMD_LONG_DESCRIPTION_OPEN);
        putValue(Action.MNEMONIC_KEY, new Integer(CMD_MNEMONIC_OPEN));
        putValue(Action.ACTION_COMMAND_KEY, CMD_ACTION_OPEN);
    }
    
    public void actionPerformed(ActionEvent evt)  {
        manager.forwardAction(new ActionEvent(evt.getSource(), evt.getID(), 
                                (String)getValue(Action.ACTION_COMMAND_KEY)));
    }
}
