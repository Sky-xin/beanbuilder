/*
 * Copyright %G% Sun Microsystems, Inc. All Rights Reserved
 */

/*
 * %W% %E%
 */

package beantest.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

/**
 * Action which represents an icon selection. Toggles among a button group.
 *
 * @version $Revision$
 * @author  Mark Davidson
 */
public class IconAction extends AbstractAction implements BeanActionConstants {
    
    private BeantestActionManager manager;
    private String commandName;

    /** 
     * @param manager Delegate that the actionPerformed method.
     * @param name Name of the icon command. 
     *             One of BeanTestActionManager.CMD_NAME_ICON_...
     */
    public IconAction(BeantestActionManager manager, String name)  {
        this.manager = manager;
        this.commandName = name;
        
        putValue(Action.NAME, name);
        putValue(Action.SHORT_DESCRIPTION, "Changes the palette icon");
        putValue(Action.LONG_DESCRIPTION, "Changes the palette icon");
    }
    
    public void actionPerformed(ActionEvent evt)  {
        manager.forwardAction(new ActionEvent(evt.getSource(), evt.getID(), 
                                commandName));
    }
}