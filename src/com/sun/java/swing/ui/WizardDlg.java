/*
 * Copyright 1998 Sun Microsystems, Inc. All Rights Reserved
 */

/*
 * $Id: WizardDlg.java,v 1.4 1999/03/04 21:02:57 kebbs Exp $
 */

package com.sun.java.swing.ui;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.CardLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * Generic Wizard dialog which has support for navigating between panels.
 *
 * @version $Revision: 1.4 $
 * @author  Mark Davidson
 */
public class WizardDlg extends JDialog {
    
    private CardLayout panesLayout;
    private JPanel panesPanel;
    
    private JButton backButton;
    private JButton nextButton;
    private JButton finishButton;
    private JButton cancelButton;

    private ActionListener finishListener = null;
    private ActionListener cancelListener = null;
    private ActionListener nextListener = null;
    private ActionListener backListener = null;

    private int numCards;
    private int cardShowing;
    
    private String title;
    private Vector panels;
    private Vector images;
    
    // For testing
    private static WizardDlg wizardDlg;

    /**
     * Creates the Wizard dialog
     *
     * @param frame - The parent frame.
     * @param title - Title of the panel.
     * @param panels - A vector of panels to insert
     * @param images - A vector of images
     */
    public WizardDlg(JFrame frame, String title, 
                     Vector panels, Vector images) {
        super(frame, title, true);
        
        this.title = title;
        this.images = images;
        
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());

	    panesLayout = new CardLayout();
	    panesPanel = new JPanel(panesLayout);

        pane.add(panesPanel, BorderLayout.CENTER);
        pane.add(createButtonPanel(), BorderLayout.SOUTH);

        setPanels(panels);        

        this.pack();
        
        // Center the window.
        CommonUI.centerComponent(this);
    }
    
    public WizardDlg(JFrame frame, String title, Vector panels) {
        this(frame, title, panels, null);
    }

    public WizardDlg(String title, Vector panels) {
        this(new JFrame(), title, panels, null);
    }

    /** 
     * Sets the panels used in the Wizard.
     */    
    public void setPanels(Vector panels)  {
        this.numCards = panels.size();
        this.cardShowing = 1;
        this.panels = panels;
	    
	    panesPanel.removeAll();

        for (int i = 0; i < numCards; i++) {
            panesPanel.add((JPanel)panels.elementAt(i), 
                           (new Integer(i)).toString());
        }

        this.validate();

		enableBackNextButtons();
    }

    /** 
     * Sets the western panel. This panel could be an image that could be 
     * set from a NextHandler.
     */
    public void setWestPanel(JPanel panel)  {
        Container pane = getContentPane();
        pane.add(panel, BorderLayout.WEST);
    }
    
    /** 
     * For testing purposes.
     */
    public static void main(String[] args)  {
        
        JPanel p1 = new JPanel();
        p1.add(new JButton("One"));
        
        JPanel p2 = new JPanel();
        p2.add(new JButton("Two"));
        
        JPanel p3 = new JPanel();
        p3.add(new JButton("Three"));
        
        JPanel p4 = new JPanel();
        p4.add(new JButton("Four"));
        
        Vector panels = new Vector();
        
        panels.addElement(p1);
        panels.addElement(p2);
        panels.addElement(p3);
        panels.addElement(p4);
        
        wizardDlg = new WizardDlg("Test Dialog", panels);
        wizardDlg.addFinishListener(new ActionListener()  {
            public void actionPerformed(ActionEvent evt)  {
                System.exit(0);     
            }
        });
        
        wizardDlg.addCancelListener(new ActionListener()  {
            public void actionPerformed(ActionEvent evt)  {
                System.exit(0);     
            }
        });
        
        wizardDlg.setVisible(true);
    }
    
    /** 
     * Creates the button panel.
     */
    private JPanel createButtonPanel()  {
        JPanel panel = new JPanel();

        backButton = CommonUI.createButton("< " + CommonUI.BUTTONTEXT_BACK,
                                    new BackListener(), CommonUI.MNEMONIC_BACK);
        backButton.setEnabled(false);
        panel.add(backButton);

        nextButton = CommonUI.createButton(CommonUI.BUTTONTEXT_NEXT + " >",
                                    new NextListener(), CommonUI.MNEMONIC_NEXT);
        panel.add(nextButton);
        
        finishButton = CommonUI.createButton(CommonUI.BUTTONTEXT_FINISH, 
                                    new FinishListener(), CommonUI.MNEMONIC_FINISH);
        finishButton.setEnabled(false);
        panel.add(finishButton);

        cancelButton = CommonUI.createButton(CommonUI.BUTTONTEXT_CANCEL, 
                                    new CancelListener(), CommonUI.MNEMONIC_CANCEL);
        panel.add(cancelButton);
        
        JPanel p2 = new JPanel(new BorderLayout());
        p2.add(panel, BorderLayout.CENTER);
        p2.add(new JSeparator(), BorderLayout.NORTH);

        return p2;
    }


    /** 
     * Constrols the enabling policy of the Next and Back buttons.
     */
    private void enableBackNextButtons() {
        if (cardShowing == 1) {
            backButton.setEnabled(false);
            finishButton.setEnabled(false);
            if (numCards > 1) {
                nextButton.setEnabled(true);
            } else {
				finishButton.setEnabled(true);
                nextButton.setEnabled(false);
            }
        } else if (cardShowing == numCards) {
            nextButton.setEnabled(false);
            finishButton.setEnabled(true);
            if (numCards > 1) {
                backButton.setEnabled(true);
            } else {
                backButton.setEnabled(false);
            } 
        } else {
            backButton.setEnabled(true);
            nextButton.setEnabled(true);
            finishButton.setEnabled(false);
        }

        // Set the title to reflect the current panel.
        this.setTitle();
    }

    /** 
     * Sets the title of the dialog with the name of the current panel.
     */
    private void setTitle()  {
        JPanel panel = (JPanel)panels.elementAt(cardShowing - 1);
        
        String newTitle = title;
        String panelTitle = panel.getName();
        
        if (panelTitle != null || panelTitle.equals(""))  {
            newTitle += " - ";
            newTitle += panelTitle;
        }
        super.setTitle(newTitle);
    }

    //
    // Listeners for Wizard navigation buttons
    //

    private class BackListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            cardShowing--;
            if (cardShowing < 1) {
                cardShowing = 1;
            } else {
                panesLayout.previous(panesPanel);
            }
            
            if (backListener != null) {
                backListener.actionPerformed(evt);
            }
            enableBackNextButtons();
        }
    }
    
    private class NextListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            cardShowing++;
            if (cardShowing > numCards) {
                cardShowing = numCards;
            } else {
                panesLayout.next(panesPanel);
            }

            if (nextListener != null) {
                nextListener.actionPerformed(evt);
            }
            enableBackNextButtons();
        }
    }

    private class FinishListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            if (finishListener != null) {
                finishListener.actionPerformed(evt);
            }
            setVisible(false);
        }
    }

    private class CancelListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            if (cancelListener != null) {
                cancelListener.actionPerformed(evt);
            }
            setVisible(false);
        }
    }

    //
    // Event declarations for Finish & Cancel events
    //
    public synchronized void addFinishListener(ActionListener l) {
        finishListener = AWTEventMulticaster.add(finishListener, l);
    }
    public synchronized void removeFinishListener(ActionListener l) {
        finishListener = AWTEventMulticaster.remove(finishListener, l);
    }
    
    public synchronized void addCancelListener(ActionListener l) {
        cancelListener = AWTEventMulticaster.add(cancelListener, l);
    }
    public synchronized void removeCancelListener(ActionListener l) {
        cancelListener = AWTEventMulticaster.remove(cancelListener, l);
    }

    public synchronized void addNextListener(ActionListener l) {
        nextListener = AWTEventMulticaster.add(nextListener, l);
    }
    public synchronized void removeNextListener(ActionListener l) {
        nextListener = AWTEventMulticaster.remove(nextListener, l);
    }

    public synchronized void addBackListener(ActionListener l) {
        backListener = AWTEventMulticaster.add(backListener, l);
    }
    public synchronized void removeBackListener(ActionListener l) {
        backListener = AWTEventMulticaster.remove(backListener, l);
    }
}