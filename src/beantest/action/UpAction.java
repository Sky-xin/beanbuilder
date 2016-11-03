/*
 * Copyright 10/01/99 Sun Microsystems, Inc. All Rights Reserved
 */

/*
 * @(#)UpAction.java	1.1 99/10/01
 */

package beantest.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;


/**
 * UpAction Up callback
 *
 * @version
 * @author  Mark Davidson
 */
public class UpAction extends AbstractAction implements BeanActionConstants {

    private BeantestActionManager manager;

    /** 
     * @param manager Delegate that the actionPerformed method.
     */
    public UpAction(BeantestActionManager manager)  {
        this.manager = manager;
        
        putValue(Action.NAME, CMD_NAME_UP);
        putValue(Action.SMALL_ICON, manager.getIcon(CMD_SMALL_ICON_UP));
        putValue(Action.SHORT_DESCRIPTION, CMD_SHORT_DESCRIPTION_UP);
        putValue(Action.LONG_DESCRIPTION, CMD_LONG_DESCRIPTION_UP);
        putValue(Action.MNEMONIC_KEY, new Integer(CMD_MNEMONIC_UP));
        putValue(Action.ACTION_COMMAND_KEY, CMD_ACTION_UP);
    }
    
    public void actionPerformed(ActionEvent evt)  {
        manager.doAction(new ActionEvent(evt.getSource(), evt.getID(), 
                                (String)getValue(Action.ACTION_COMMAND_KEY)));
    }
}
