/*
 * Copyright 1998 Sun Microsystems, Inc. All Rights Reserved
 */

/*
 * $Id: OkCancelButtonPanel.java,v 1.2 1999/03/03 02:53:49 davidson Exp $
 */

package com.sun.java.swing.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * A simple panel which features an Ok and a Cancel button in a flow panel.
 *
 * This class will send a <code>CommonUI.BUTTON_CMD_OK</code> or a 
 * <code>BUTTON_CND_CANCEL</code> as the action command to the registered 
 * action listener.
 *
 * @version $Revision: 1.2 $
 * @author  Mark Davidson
 */
public class OkCancelButtonPanel extends JPanel implements ActionListener {
    
    private JButton okButton;
    private JButton cancelButton;
    
    // Listener which will be sent a message.
    private ActionListener listener;

    /**
     * ctor
     *
     * @param listener - ActionListener which will get get the ActionEvent
     */
    public OkCancelButtonPanel(ActionListener listener) {
        this.listener = listener;
        okButton = CommonUI.createButton(CommonUI.BUTTONTEXT_OK, this, 
                                            CommonUI.MNEMONIC_OK);

        cancelButton = CommonUI.createButton(CommonUI.BUTTONTEXT_CANCEL, this,
                                            CommonUI.MNEMONIC_CANCEL);        
        add(okButton);
        add(cancelButton);
        
    }
    
    /** 
     * Forward the event to the the ActionListener that constructed the panel.
     * The ActionEvetn.actionCommand contain the command.
     */
    public void actionPerformed(ActionEvent evt)  {
        Object obj = evt.getSource();
        
        if (obj instanceof JButton)  {
            JButton button = (JButton)obj;
            
            if (button == okButton)  {
                listener.actionPerformed(new ActionEvent(evt.getSource(), 
                                        evt.getID(), CommonUI.BUTTON_CMD_OK));
            }
            
            if (button == cancelButton)  {
                listener.actionPerformed(new ActionEvent(evt.getSource(), 
                                        evt.getID(), CommonUI.BUTTON_CMD_CANCEL));
            }
        }
    }
}