/*
 * @(#)BeanPanel.java	1.9 99/10/18
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

package beantest;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.io.IOException;

import java.beans.Beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;

import beantest.palette.Palette;

import beantest.action.BeantestActionManager; // Just for Prop change constants.

import beantest.property.PropertyPane;

import com.sun.java.swing.ui.CommonUI;
import com.sun.java.swing.action.AbstractItemAction;

/**
 * Displays the tree, panel and property sheet. Also holds the Palette
 * but the palette is constructed in the main frame.
 *
 * @version 1.9 10/18/99
 * @author  Mark Davidson
 */
public class BeanPanel extends JPanel implements TreeSelectionListener,
                                                PropertyChangeListener {

    // Horizontal splitpane holds the tree and the workspace
    private JSplitPane mainPane;
    private JSplitPane propertyPane;

    private TreePanel treePanel;
    private PropertyPane propPanel;
    private DesignPanel designPanel;

    private JTextField beanTF;

    // Top level object of the design.
    private Object root;

    // Selected Component
    private Object selectedItem;

    private BeantestActionManager manager;

    /**
     * Constructs the BeanPanel
     */
    public BeanPanel(BeantestActionManager manager) {
        this.selectedItem = null;
        this.manager = manager;

        setLayout(new BorderLayout());

        // Tree panel shows containment hierarchy.
        treePanel = new TreePanel();
        treePanel.getTree().addTreeSelectionListener(this);

        // Prop panel for editing properties of the selected object.
        propPanel = new PropertyPane(manager);
        propPanel.addPropertyChangeListener(this);

        propertyPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        propertyPane.setTopComponent(treePanel);
        propertyPane.setBottomComponent(propPanel);

        // Design panel is where the App is designed.
        designPanel = new DesignPanel();
        designPanel.addPropertyChangeListener(this);

        JPanel dp = new JPanel(new BorderLayout());
        dp.add(createControlPanel(), BorderLayout.NORTH);
        dp.add(designPanel, BorderLayout.CENTER);

        mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainPane.setLeftComponent(propertyPane);
        mainPane.setRightComponent(dp);

        add(mainPane, BorderLayout.CENTER);
    }

    /**
     * Control Panel which allows for some rich DesignPanel editing.
     */
    private JPanel createControlPanel()  {

        KeyHandler handler = new KeyHandler();
        beanTF = CommonUI.createTextField("", handler);
        handler.setTextField(beanTF);

        JLabel label = CommonUI.createLabel("Instantiate Bean ", 'B', beanTF);

        // Configure the design checkbox. Note: the checkbox will not update to
        // reflect the same state as the menu item because the "selected"
        // PropertyChangeEvent is never fired. Hopefully this will be fixed in the
        // near future.
        AbstractItemAction action;
        action = (AbstractItemAction)manager.getAction(BeantestActionManager.CMD_NAME_DESIGN);
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(true);     // XXX - hack! should use action.isSelected()
        checkBox.setAction(action);     // Action set after selected state so that event is not fired.
        checkBox.addItemListener(action);
        // XXX - setMnemonic not needed in JDK 1.3 RC 1. Taken care of in
        // setAction(Action). Required in 1.3 beta
        Integer mnemonic = (Integer)action.getValue(AbstractItemAction.MNEMONIC_KEY);
        if (mnemonic != null)
            checkBox.setMnemonic(mnemonic.intValue());

        JPanel panel = new JPanel();
        panel.setBorder(CommonUI.createBorder("Control Panel"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(label);
        panel.add(beanTF);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(checkBox);

        return panel;
    }

    /**
     * Handler for the text field. Will instantiate and add Bean to 
     * the DesignPanel.
     */
    private class KeyHandler extends KeyAdapter  {
        private JTextField textField;

        public KeyHandler()  {
            this(null);
        }

        public KeyHandler(JTextField textField)  {
            setTextField(textField);
        }

        public void setTextField(JTextField textField) {
            this.textField = textField;
        }

        public void keyReleased(KeyEvent evt)  {
            if (evt.getKeyCode() == KeyEvent.VK_ENTER)  {
                // Calls outer class methods
                String bean = textField.getText();
                String message = "New Bean created: " + bean;

                try {
                    Object obj = makeNewBean(bean);
                    designPanel.addBean(obj);
                    textField.setText("");
                } catch (IOException ex) {
                    message = "I\\O Exception for: " + bean;
                    showMessageDialog(message);
                } catch (ClassNotFoundException ex2) {
                    message = "Class Not Found: " + bean;
                    showMessageDialog(message);
                }

                firePropertyChange(BeantestActionManager.ACT_PROPERTY_MESSAGE,
                            "", message);
            }
        }
        
        private void showMessageDialog(String message)  {
            JOptionPane.showMessageDialog(designPanel, message, "Control Panel", 
                                            JOptionPane.WARNING_MESSAGE);
        }

        /**
         * This is almostthe same method in PaletteItem.
         * Note: In the future we should consolidate all bean behaviours in a
         * set of utility classes.
         */
        private Object makeNewBean(String beanName) throws IOException,
                    ClassNotFoundException {
            return Beans.instantiate(null, beanName);
        }
    }

    /**
     * Sets the palette. The UI of the palette is displayed in the LayoutManager
     * of the BeanPanel. The selected item and instantiation of the palette is
     * done in the DesignPanel.
     */
    public void setPalette(Palette palette)  {
        add(palette, BorderLayout.NORTH);
        // XXX - Should try to remove this dependency.
        designPanel.setPalette(palette);
    }

    /**
     * Sets the mode of the design panel
     * @param design Flag which indicates that the panel is in design mode.
     */
    public void setDesignMode(boolean mode)  {
        designPanel.setDesignMode(mode);
    }

    /**
     * Shows or hides the Property Panel
     * @param show
     */
    public void showProperties(boolean show)  {
        if (propPanel != null)  {
            if (show)  {
                propPanel.setSelectedItem(getSelectedItem());
            }

            propPanel.setVisible(show);
            validate();
        }
    }

    /**
     * Updates the tree and the property sheet for the selected object.
     * XXX - should fire property change events
     */
    public void setSelectedItem(Object item) {
        if (getSelectedItem() != item)  {
            this.selectedItem = item;

            if (propPanel.isVisible())
                propPanel.setSelectedItem(item);

            treePanel.setSelectedItem(item);
            designPanel.setSelectedItem(item);
        }
    }

    public Object getSelectedItem()  {
        return selectedItem;
    }

    /**
     * Sets the root of the design object.
     * XXX - should fire property change events
     */
    public void setRoot(Object root)  {
        this.root = root;
        treePanel.setRoot(root);
        designPanel.setRoot(root);

        setSelectedItem(root);
    }

    public Object getRoot()  {
        return root;
    }

    //
    // Event callbacks
    //

    /**
     * Implementation of the tree selection listener
     */
    public void valueChanged(TreeSelectionEvent evt)  {
        if (!evt.isAddedPath())
            return;

        TreePath path = evt.getPath();
        Component comp = (Component)path.getLastPathComponent();

        setSelectedItem(comp);
    }

    /**
     * Implementation of the propertyChangeListener
     */
    public void propertyChange(PropertyChangeEvent evt)  {
        Object source = evt.getSource();
        String prop = evt.getPropertyName();
        Object newValue = evt.getNewValue();
        Object oldValue = evt.getOldValue();

        if (source == designPanel)  {
            // Listen to a selected Item change from the designer.
            if (prop.equals(BeantestActionManager.ACT_PROPERTY_SELECT))  {
                setSelectedItem(newValue);
            }
        } else if (prop.equals(BeantestActionManager.ACT_PROPERTY_MESSAGE)) {
            // Forward the message to the status bar.
            firePropertyChange(BeantestActionManager.ACT_PROPERTY_MESSAGE,
                                    oldValue, newValue);
        } else if (prop.equals(BeantestActionManager.ACT_PROPERTY_ADD)) {
            // Bean added should be added to the design panel.
            designPanel.addBean(newValue);
        }
    }

    public Dimension getMinimumSize()  {
        return new Dimension(800, 500);
    }

    public Dimension getPreferredSize()  {
        return getMinimumSize();
    }

}
