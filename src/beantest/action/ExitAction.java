/*
 * Copyright 10/01/99 Sun Microsystems, Inc. All Rights Reserved
 */

/*
 * @(#)ExitAction.java	1.2 99/10/01
 */

package beantest.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;


/**
 * ExitAction exits the application
 *
 * @version
 * @author  Mark Davidson
 */
public class ExitAction extends AbstractAction implements BeanActionConstants {

    private BeantestActionManager manager;

    /** 
     * @param manager Delegate that the actionPerformed method.
     */
    public ExitAction(BeantestActionManager manager)  {
        this.manager = manager;
        
        putValue(Action.NAME, CMD_NAME_EXIT);
        putValue(Action.SHORT_DESCRIPTION, CMD_SHORT_DESCRIPTION_EXIT);
        putValue(Action.LONG_DESCRIPTION, CMD_LONG_DESCRIPTION_EXIT);
        putValue(Action.MNEMONIC_KEY, new Integer(CMD_MNEMONIC_EXIT));
        putValue(Action.ACTION_COMMAND_KEY, CMD_ACTION_EXIT);
    }
    
    public void actionPerformed(ActionEvent evt)  {
        manager.doAction(new ActionEvent(evt.getSource(), evt.getID(), 
                                (String)getValue(Action.ACTION_COMMAND_KEY)));
    }
}
