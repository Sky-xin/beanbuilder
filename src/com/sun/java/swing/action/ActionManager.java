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

package com.sun.java.swing.action;

import java.awt.AWTEvent;
import java.awt.AWTEventMulticaster;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.Action;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ActionManager manages and coordinates the enabling and handling of actions 
 * between all the UI elements like toolbars, menus and popup menus.
 * Acts as the glue (controller) between the UI and the model.
 *
 * It also forwards the ActionCommands to the ActionListener (typically a UI).
 *
 * How to add a new command:
 * ------------------------
 * 1. Create action class by subclassing AbstractAction
 * 2. Add the definition of the action to ActionConstants i.e., CMD_NAME...
 * 3. Add the new command to the command list in the addActions() method.
 * 4. If this command can be enabled/disabled:
 *      a. Add a flag for the action in NodeConstants ND_ACTION...
 *      b. Add the logic in NodeItem sublclasess or elsewhere which will 
 *         set the allowable actions on NodeItem.
 *      c. Set the default state in the ctors of NodeItem subclasses.
 * 5. The actionPerformed method in the new Action should call forwardAction()
 *    with a ActionEvent that has the CMD_ACTION as the action command.
 * 6. Add the actions to whichever controls require the action through 
 *    the ActionManager.
 *
 * @version %I% %G%
 * @author  Mark Davidson
 */
public abstract class ActionManager implements ActionConstants,
                            MouseListener, Runnable {
    
    // Table of all possible actions.
    // key: CMD_NAME_...    value: instanceof AbstractAction
    private Hashtable actions;
    
    // The listener to action events (usually the main UI)
    private ActionListener listener;
    
    // The listener to item events (console panel for toggle buttons);
    private ItemListener itemlistener;

    // Keeps track of property change listeners.
    private PropertyChangeSupport support;

    // This is the event queue.    
    private Vector queue = new Vector();

    // add by yue.yan@2016/11/3
    private Logger logger = Logger.getLogger("com.sun.java.swing.action.ActionManager");
    
    /**
     * Creates the action manager
     */
    public ActionManager() {
        actions = new Hashtable();

        support = new PropertyChangeSupport(this);
        listener = null;
        itemlistener = null;
    }
    
    /** 
     * Configures the action manager to manage specific actions.
     */
    protected abstract void addActions();
    
    
    /** 
     * Adds an action to the ActionManager
     */
    protected void addAction(String cmdname, Action action)  {
        actions.put(cmdname, action);
    }

    public void addActionListener(ActionListener l)  {
        this.listener = AWTEventMulticaster.add(listener, l);
    }
    
    public void removeActionListener(ActionListener l)  {
        this.listener = AWTEventMulticaster.remove(listener, l);
    }
    
    public void addItemListener(ItemListener l)  {
        this.itemlistener = AWTEventMulticaster.add(itemlistener, l);
    }
    
    public void removeItemListener(ItemListener l)  {
        this.itemlistener = AWTEventMulticaster.remove(itemlistener, l);
    }
   
    // Registration methods for PropertyChangeListeners. 
    public void addPropertyChangeListener(PropertyChangeListener l)  {
        support.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l)  {
        support.removePropertyChangeListener(l);
    }
    
    /** 
     * Returns the Icon associated with the name from the resources.
     * The resouce should be in the path.
     * @param name Name of the icon file i.e., help16.gif
     * @return the name of the image or null if the icon is not found.
     */
    public ImageIcon getIcon(String name)  {
        String imagePath = IMAGE_DIR + name;
//        /Beanbuilder/images/add16.gif
//    	String imagePath = System.getProperty("user.dir") + IMAGE_DIR + name;
        logger.info(imagePath);
        java.net.URL url = this.getClass().getResource(imagePath);
        if (url != null)  {
            return new ImageIcon(url);
        }
        return null;
    }
    
    /** 
     * Retreives the action for an widget that requires Action objects.
     * Such as JMenu and JToolBar.
     * @param key One of ActionConstants.CMD_NAME_...
     */
    public Action getAction(String key)  {
        if (key != null && key != /*NOI18N*/"")
            return (Action)actions.get(key);
        else
            return null;
    }
    
    /** 
     * Performs the action and bypasses the thread.
     */
    public void doAction(ActionEvent evt)  {
        if (listener != null)
            listener.actionPerformed((ActionEvent)evt);
    }
    
    /** 
     * Performs the ItemEvent by bypassing the action thread.
     */
    public void doItemEvent(ItemEvent evt)  {
        if (itemlistener != null)
            itemlistener.itemStateChanged((ItemEvent)evt);
    }
   
    /** 
     * Forwards the event to the ActionListener
     *
     * @param - evt The event that was constructed from one of the action commmands.
     */
    public void forwardAction(ActionEvent evt)  {
        addEvent(evt);
    }
    
    /** 
     * Forwards the item event to the itemListener. This event should be called
     * by an action that represents a toggle button or a checkbox menu item.
     * @param evt The item event. getItem() will return the Action object
     *      that was changed.
     */
    public void forwardItemEvent(ItemEvent evt)  {
        addEvent(evt);
    }

    /** 
     * Send the description of the command to the status bar.
     * Note: The name of the control is the key into the actions list.
     */
    public void mouseEntered(MouseEvent evt)  {
    
        // XXX - debug
        if (evt.getSource() instanceof AbstractButton)  {
            AbstractButton comp = (AbstractButton)evt.getSource();
            Action action = getAction(comp.getName());
        
            if (action != null)  {
                String message = (String)action.getValue(Action.LONG_DESCRIPTION);
                support.firePropertyChange(ACT_PROPERTY_MESSAGE, /*NOI18N*/"", message);
            }
        } else {
            System.out.println("MouseEntered action manager : " + evt.getSource());
        }
    }
    
    // Send the long name to the status bar
    public void mouseExited(MouseEvent evt)  {
        support.firePropertyChange(ACT_PROPERTY_MESSAGE, /*NOI18N*/"", /*NOI18N*/"");
    }
    
    public void mouseClicked(MouseEvent evt) {}
    public void mousePressed(MouseEvent evt) {}
    public void mouseReleased(MouseEvent evt) {}
    
    /** 
     * Run method processes requests on the event Queue.
     * This method cannot fail or the event queue is hosed.
     */
    public void run()  {
        while (true) {
            try {
                processNextEvent();
            } catch (NullPointerException ex2) {
                String message = "ActionManager fatal NullPointerException processing event: " + ex2.getMessage();
                support.firePropertyChange(ACT_PROPERTY_MESSAGE, "", message);
                // XXX - test
                ex2.printStackTrace();
            } catch (Exception ex) {
                String message = "ActionManager fatal exception processing event: " + ex.getMessage();
                support.firePropertyChange(ACT_PROPERTY_MESSAGE, "", message);
            }
        }
    }
    
 
    /** 
     * Adds an event to the event queue. Notifies
     */
    private synchronized void addEvent(AWTEvent evt)  {
        queue.addElement(evt);
        notify();
    }
    
    /** 
     * Retrieves the next event from the queue. If there are no events then
     * the executed thread is blocked.
     */
    private synchronized AWTEvent getNextEvent()  {
        if (queue.size() == 0) {
            try { 
                wait();
            } catch (Exception e) {
            }
        }

        AWTEvent evt = (AWTEvent)queue.firstElement();
        queue.removeElementAt(0);
        return evt;        
    }
 
    private void processNextEvent() {
        AWTEvent evt = getNextEvent();
        
        if (evt == null)
            return;
        
        if (evt instanceof ActionEvent)  {
            if (listener != null)
                listener.actionPerformed((ActionEvent)evt);
        }
        if (evt instanceof ItemEvent) {
            if (itemlistener != null)
                itemlistener.itemStateChanged((ItemEvent)evt);
        }
    }
}