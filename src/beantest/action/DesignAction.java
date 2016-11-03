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

import com.sun.java.swing.action.AbstractItemAction;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.Action;

/**
 * Two state action which toggles design or preview mode
 *
 * @version %I% %G%
 * @author  Mark Davidson
 */
public class DesignAction extends AbstractItemAction  
                                implements BeanActionConstants {

    private BeantestActionManager manager;

    public DesignAction(BeantestActionManager manager) {
        this.manager = manager;
        
        putValue(Action.NAME, CMD_NAME_DESIGN);
        putValue(Action.SHORT_DESCRIPTION, CMD_SHORT_DESCRIPTION_DESIGN);
        putValue(Action.LONG_DESCRIPTION, CMD_LONG_DESCRIPTION_DESIGN);
        putValue(Action.MNEMONIC_KEY, new Integer(CMD_MNEMONIC_DESIGN));
        
    }
    
    // 
    // XXX - these two method are common to all toggle buttons. They should
    // be moved to the base class.
    //
    
    public void actionPerformed(ActionEvent evt)  {
    }

    /** 
     * Show or hide the statistics
     */    
    public void itemStateChanged(ItemEvent evt)  {
        boolean show;
        
        if (evt.getStateChange() == ItemEvent.SELECTED)
            show = true;
        else
            show = false;
        
        // Update all objects that share this item    
        setSelected(show);
        manager.forwardItemEvent(new ItemEvent(evt.getItemSelectable(), 
                        ItemEvent.ITEM_STATE_CHANGED, this, evt.getStateChange()));
    }
    

}
