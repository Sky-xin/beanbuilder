/*
 * Copyright 1999 Sun Microsystems, Inc. All Rights Reserved
 */

/*
 * $Id: OkCancelDialog.java,v 1.2 1999/05/28 23:12:21 davidson Exp $
 */

package com.sun.java.swing.ui;

import java.awt.BorderLayout;
import java.awt.Container;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * Dialog which uses the OkCancelButtonPanel. Puts a panel in the center of the
 * a border layout.
 *
 * @version $Revision: 1.2 $
 * @author  Mark Davidson
 */
public class OkCancelDialog extends JDialog implements ActionListener{
    
    private boolean okPressed;
    
    /**
     * Creates the dialog as modal
     *
     * @param title Name of the dialog in the title bar
     * @param panel Panel to stick in the center of the dialog
     */
    public OkCancelDialog(String title, JPanel panel) {
        this(title, panel, true);
    }
    
    /**
     * Creates the dialog
     *
     * @param title Name of the dialog in the title bar
     * @param panel Panel to stick in the center of the dialog
     * @param modal Flag which indicates if the dialog should be modal
     */
    public OkCancelDialog(String title, JPanel panel, boolean modal) {
       this.setTitle(title);
       this.setModal(modal);
       
       Container pane = this.getContentPane();
       pane.setLayout(new BorderLayout());
       
       pane.add(panel, BorderLayout.CENTER);
       pane.add(new OkCancelButtonPanel(this), BorderLayout.SOUTH);
       
       this.pack();
       CommonUI.centerComponent(this);
    }

    /** 
     * Returns a flag which indicates if the OK button was pressed.
     */
    public boolean isOk()  {
        return okPressed;
    }
    
    //
    // Action listener methods.
    //
    
    public void actionPerformed(ActionEvent evt)  {
        String command = evt.getActionCommand();
        
        if (command.equals(CommonUI.BUTTON_CMD_OK)) {
            okPressed = true;
            this.setVisible(false);
            this.dispose();
            
        } else if (command.equals(CommonUI.BUTTON_CMD_CANCEL)) {
            okPressed = false;
            this.setVisible(false);
            this.dispose();
            
        }
    } // end actionPerformed
}