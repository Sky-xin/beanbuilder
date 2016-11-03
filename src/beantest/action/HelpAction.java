/*
 * Copyright 10/01/99 Sun Microsystems, Inc. All Rights Reserved
 */

/*
 * @(#)HelpAction.java	1.2 99/10/01
 */

package beantest.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

/**
 * Help Contents
 *
 * @version 1.2 10/01/99
 * @author  Mark Davidson
 */
public class HelpAction extends AbstractAction implements BeanActionConstants {

    private BeantestActionManager manager;

    /** 
     * @param manager Delegate that the actionPerformed method.
     */
    public HelpAction(BeantestActionManager manager)  {
        this.manager = manager;
        
        putValue(Action.NAME, CMD_NAME_HELP);
        putValue(Action.SMALL_ICON, manager.getIcon(CMD_SMALL_ICON_HELP));
        putValue(Action.SHORT_DESCRIPTION, CMD_SHORT_DESCRIPTION_HELP);
        putValue(Action.LONG_DESCRIPTION, CMD_LONG_DESCRIPTION_HELP);
        putValue(Action.MNEMONIC_KEY, new Integer(CMD_MNEMONIC_HELP));
        putValue(Action.ACTION_COMMAND_KEY, CMD_ACTION_HELP);
    }
    
    public void actionPerformed(ActionEvent evt)  {
        manager.forwardAction(new ActionEvent(evt.getSource(), evt.getID(), 
                                (String)getValue(Action.ACTION_COMMAND_KEY)));
    }
}
