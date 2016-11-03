/*
 * @(#)PropertyPane.java	1.16 99/11/11
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
package beantest.property;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.beans.Customizer;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager; // Prop Editor registration for types.

import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import beantest.action.BeantestActionManager;

import javax.swing.border.Border;

import com.sun.java.swing.ui.CommonUI;

/**
 * The UI for listing, sorting, and setting component
 * properties.
 *
 * @version 1.16 11/11/99
 * @author  John J. Walker
 * @author  Mark Davidson
 */

public class PropertyPane extends JPanel implements ActionListener,
                                                TableModelListener {

    private Object bean;        // Current Bean.
    private Stack beanStack;    // Stack of beans for walking bean hierarchy.

    private JTable table;
    private PropertyColumnModel columnModel;
    private PropertyTableModel tableModel;

    // UI for the property control panel.
    private JLabel nameLabel;
    private JComboBox viewCombo;
    private JButton up;
    private JButton down;
    private JButton add;
    private JButton cust;

    private BeantestActionManager manager;

    private static final int ROW_HEIGHT = 20;

    // View options.
    private static final String[] VIEW_CHOICES = { "All", "Standard", "Expert", 
                                        "Read Only", "Bound", "Constrained", "Hidden", "Preferred" };

    private Dimension buttonSize = new Dimension(CommonUI.buttconPrefSize);
    private Insets buttonInsets = new Insets(0,0,0,0);

    /**
     * Constructor
     */
    public PropertyPane(BeantestActionManager manager)  {
        super(new BorderLayout());
        this.manager = manager;
        
        registerPropertyEditors();
        
        tableModel = new PropertyTableModel();
        tableModel.addTableModelListener(this);
        
        columnModel = new PropertyColumnModel();
        table = new JTable(tableModel, columnModel);
        table.setRowHeight(ROW_HEIGHT);
        table.setAutoResizeMode(table.AUTO_RESIZE_LAST_COLUMN);
        table.addMouseListener(new MouseAdapter()  {
            public void mouseClicked(MouseEvent evt)  {
                if (evt.getClickCount() == 2 && 
                        table.getSelectedColumn() == 0)
                    // Double clicking on the first column will call the down
                    // action on the current object.
                    handleDownAction();
            }
        });

        add(createControlPanel(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(createNamePanel(), BorderLayout.SOUTH);
    }
    
    /** 
     * Creates the panel for selecting the view and sorting order.
     */
    private JToolBar createControlPanel()  {
        viewCombo = CommonUI.createComboBox(VIEW_CHOICES, this, false);
        viewCombo.setSelectedItem(VIEW_CHOICES[PropertyTableModel.VIEW_STANDARD]);
        
        up = new JButton();
        configureButton(up, manager.getAction(BeantestActionManager.CMD_NAME_UP));
        
        down = new JButton();
        configureButton(down, manager.getAction(BeantestActionManager.CMD_NAME_DOWN));
        
        add = new JButton();
        configureButton(add, manager.getAction(BeantestActionManager.CMD_NAME_ADD));

        cust = new JButton();
        configureButton(cust, manager.getAction(BeantestActionManager.CMD_NAME_CUST));

        JToolBar toolbar = new JToolBar();
        toolbar.add(up);
        toolbar.add(down);
        toolbar.addSeparator();
        toolbar.add(add);
        toolbar.add(cust);
        toolbar.addSeparator();
        toolbar.add(viewCombo);

        setButtonState();

        return toolbar;
    }
    
    /** 
     * Handler for UI interactions.
     */
    public void actionPerformed(ActionEvent evt)  {
        Object obj = evt.getSource();
        
        if (obj instanceof JButton)  {
            if (obj == up)  {
                handleUpAction();
            } else if (obj == down) {
                handleDownAction();
            } else if (obj == add) {
                handleAddAction();
            } else if (obj == cust) {
                handleCustomizerAction();
            }
        }
        
        if (obj instanceof JComboBox)  {
            if (obj == viewCombo)  {
                tableModel.filterTable(viewCombo.getSelectedIndex());
            }
        }
    }
    
    /** 
     * Configures the button according to the Action.
     * These properties are added by default in CommonMenuBar 
     * and CommonToolBar. See the comparable methods in those classes
     * for details.
     */
    private void configureButton(JButton button, Action action)  {
        
        button.setAction(action);
        // identification key for ActionManager
        button.setName((String)action.getValue(Action.NAME)); 

        button.setText("");
        
        button.setAlignmentY(0.5f);
        
	    button.setMargin(buttonInsets);
        
        button.setSize(buttonSize);
        
        button.addActionListener(this);
        
        // Grrrr. Basic button UI does't use these values!!
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);

        button.addMouseListener(manager);
    }
  
    
    /** 
     * The name panel shows the current selected item
     */
    private JPanel createNamePanel()  {
        JLabel label = new JLabel("Properties for: ");
        nameLabel = new JLabel("< Current Object >");
        
        JPanel panel = new JPanel();
        FlowLayout layout = (FlowLayout)panel.getLayout();
        layout.setAlignment(FlowLayout.LEFT);
        
        panel.add(label);
        panel.add(nameLabel);
        
        return panel;
    }
    
    /** 
     * Handler for the down action
     */
    public void handleDownAction()  {
        int index = table.getSelectedRow();
        if (index != -1)  {
            Object obj = tableModel.getValueAt(index, PropertyTableModel.COL_VALUE);
            
            if (beanStack == null)  {
                beanStack = new Stack();
            }
            beanStack.push(bean);
            setBean(obj);
        }
        setButtonState();
    }
    
    /** 
     * Handler for the up action
     */
    public void handleUpAction()  {
        if (beanStack != null && !beanStack.empty())  {
            setBean(beanStack.pop());
        }
        setButtonState();
    }
    
    /** 
     * Handle the add gesture. Informs prop change listener to add the selected
     * current property sheet component.
     */
    public void handleAddAction()  {
        int index = table.getSelectedRow();
        if (index != -1)  {
            Object obj = tableModel.getValueAt(index, PropertyTableModel.COL_VALUE);
            
            if (obj != null && !(obj instanceof Component)) {
                String message = obj.getClass().getName();
                message += " sent to design panel";
                firePropertyChange(BeantestActionManager.ACT_PROPERTY_MESSAGE,
                                            "", message);
                firePropertyChange(BeantestActionManager.ACT_PROPERTY_ADD, null, obj);
            }
        }
    }
    
    /** 
     * Handle the customizer action. Will display a customizer in a dialog
     */
    public void handleCustomizerAction()  {
        Component comp = tableModel.getCustomizer();
        
        if (comp != null)  {
            CustomizerDialog dlg = new CustomizerDialog(comp);
            dlg.setVisible(true);
        }
    }
    
    /** 
     * A customizer dialog which takes a Component which implements the 
     * customizer interface.
     */
    private class CustomizerDialog extends JDialog implements ActionListener {
    
        public CustomizerDialog(Component comp)  {
            super(new JFrame(), "Customizer Dialog");

            Customizer customizer = (Customizer)comp;
            customizer.setObject(bean);

            JPanel okpanel = new JPanel();
            okpanel.add(CommonUI.createButton(CommonUI.BUTTONTEXT_OK, this,
                                               CommonUI.MNEMONIC_OK));
            Container pane = getContentPane();
            pane.add(comp, BorderLayout.CENTER);
            pane.add(okpanel, BorderLayout.SOUTH);
            pack();
            
            CommonUI.centerComponent(this, PropertyPane.this);
        }
    
        public void actionPerformed(ActionEvent evt)  {
            this.dispose();
        }
        
    }

    /** 
     * Method which registers property editors for types.
     */
    private void registerPropertyEditors()  {
        PropertyEditorManager.registerEditor(Color.class, javax.swing.beaninfo.SwingColorEditor.class);
        PropertyEditorManager.registerEditor(Font.class, javax.swing.beaninfo.SwingFontEditor.class);
        PropertyEditorManager.registerEditor(Border.class, javax.swing.beaninfo.SwingBorderEditor.class);
        PropertyEditorManager.registerEditor(Boolean.class, javax.swing.beaninfo.SwingBooleanEditor.class);
        PropertyEditorManager.registerEditor(boolean.class, javax.swing.beaninfo.SwingBooleanEditor.class);
        
        PropertyEditorManager.registerEditor(Integer.class, javax.swing.beaninfo.SwingIntegerEditor.class);
        PropertyEditorManager.registerEditor(int.class, javax.swing.beaninfo.SwingIntegerEditor.class);
        
        PropertyEditorManager.registerEditor(Float.class, javax.swing.beaninfo.SwingNumberEditor.class);
        PropertyEditorManager.registerEditor(float.class, javax.swing.beaninfo.SwingNumberEditor.class);
        
        PropertyEditorManager.registerEditor(java.awt.Dimension.class, javax.swing.beaninfo.SwingDimensionEditor.class);
        PropertyEditorManager.registerEditor(java.awt.Rectangle.class, javax.swing.beaninfo.SwingRectangleEditor.class);
        PropertyEditorManager.registerEditor(java.awt.Insets.class, javax.swing.beaninfo.SwingInsetsEditor.class);
        
        PropertyEditorManager.registerEditor(String.class, javax.swing.beaninfo.SwingStringEditor.class);
        PropertyEditorManager.registerEditor(Object.class, javax.swing.beaninfo.SwingObjectEditor.class);
    }
    
    /** 
     * Sets the bean that this PropertyPane represents.
     */
    public void setSelectedItem(Object bean)  {
        beanStack = null;   // Reset the bean stack.
        
        if (bean != null)  {
            setBean(bean);
            setButtonState();
        }
    }
    
    /** 
     * Sets the state of the up and down buttons based on the contents of the stack.
     */
    private void setButtonState()  {
        if (beanStack == null || beanStack.isEmpty())  {
            up.setEnabled(false);
        } else {
            up.setEnabled(true);
        }
        
        // Enable customizer button if the model has a customizer.
        cust.setEnabled(tableModel.hasCustomizer());
    }


    /** 
     * Sets the PropertyPane to show the properties of the named bean.
     */
    protected void setBean(Object bean)  {
        this.bean = bean;

        if (bean != null)  {
            nameLabel.setText(bean.getClass().getName());

            tableModel.setObject(bean);
            tableModel.filterTable(viewCombo.getSelectedIndex());
            tableModel.fireTableDataChanged();
        }
    }


    //
    // Table Model Listener methods
    //
    public void tableChanged(TableModelEvent evt)  {
        // Adjust the preferred height of the row to the the same as
		// the property editor.
        table.setRowHeight(ROW_HEIGHT);
        
        PropertyEditor editor;
        Component comp;
        Dimension prefSize;
        
        for (int i = 0; i < table.getRowCount(); i++) {
            editor = tableModel.getPropertyEditor(i);
            if (editor != null)  {
                comp = editor.getCustomEditor();
                if (comp != null)  {
                    prefSize = comp.getPreferredSize();
                    if (prefSize.height > ROW_HEIGHT)  {
                        table.setRowHeight(i, prefSize.height);
                    }
                }
            }
        }
    }
}
