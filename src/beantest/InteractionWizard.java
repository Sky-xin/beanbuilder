/*
 * @(#)InteractionWizard.java	1.13 99/11/05
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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.beans.ParameterDescriptor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

import java.lang.reflect.Method;

import java.util.Arrays;    // Collections class for sorting Since JDK 1.2
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EventListener;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import com.sun.java.swing.ui.CommonUI;
import com.sun.java.swing.ui.WizardDlg;

/**
 * A Wizard which allows for the selection of interaction components. This
 * wizard also uses the JDK 1.2 collection classes to implement sorting.
 *
 * @version 1.13 11/05/99
 * @author  Mark Davidson
 */
public class InteractionWizard extends BeanWizard implements ActionListener {

    private final static String COLUMN_TYPE = "Type";
    private final static String COLUMN_VALUE = "Value";

    private final static int COLUMN_TYPE_NUM = 0;
    private final static int COLUMN_VALUE_NUM = 1;

    private boolean isPropertyHookup = true;

    // Components in the control panel.
    private JRadioButton propertyRB;
    private JRadioButton eventRB;
    
    // Property panels in the wizard
    private JPanel propPanel;

    private JList targetPropsList;

    // Event panels in the wizard
    private JPanel sourcePanel;
    private JPanel targetPanel;
    private JPanel argsPanel;
    private JPanel summaryPanel;

    // Components in the wizard
    private JList eventsList;
    private JList methodList;
    private JList targetMethodsList;
    private JList sourceMethodsList;

    // Argument label on Arguments page will change according to method selection.
    private JLabel argsLabel;

    private JTable argsTable;
    private ArgumentsModel argsModel;

    private final static Dimension preferredSize = new Dimension(550, 400);

    private final static Font labelFont = new Font("Dialog", Font.BOLD, 16);

    /**
     * Creates the interaction wizard
     *
     * @param frame Parent frame of the wizard
     * @param source Source component.
     * @param target Target component.
     */
    public InteractionWizard(Object source, Object target) {
        this(new JFrame(), source, target);
    }

    /**
     * Creates the interaction wizard
     *
     * @param frame Parent frame of the wizard
     * @param source Source component.
     * @param target Target component.
     */
    public InteractionWizard(JFrame frame, Object source, Object target) {
        super(source, target);

        // This wizard is configured for Property Hookups by default
        wizard = new WizardDlg(frame, "Interaction Wizard", createPropertyPanels());
        wizard.setWestPanel(createControlPanel());
        
        // Regsister listeners
        wizard.addCancelListener(new CancelHandler());
        wizard.addFinishListener(new FinishHandler());
    }

    /** 
     * The control panel is the west panel in the wizard which allows for
     * the selection of Property vs. Event Adapter.
     */
    private JPanel createControlPanel()  {
        propertyRB = CommonUI.createRadioButton("Set Property", 'S', this, true);
        propertyRB.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        eventRB = CommonUI.createRadioButton("Event Adapter", 'E', this);
        eventRB.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        ButtonGroup group = new ButtonGroup();
        group.add(propertyRB);
        group.add(eventRB);

        // Build HTML text for JEditorPane.
        StringBuffer message = new StringBuffer("<html><b>Source Object:</b><ul><li>");
        message.append(getRootName(source.getClass().getName()));
        message.append("</li></ul><p><b>Target Object:</b><ul><li>");
        message.append(getRootName(target.getClass().getName()));
        message.append("</li></ul></html>");
        
        JEditorPane pane = new JEditorPane("text/html", message.toString());
        pane.setEditable(false);
        pane.setAlignmentX(Component.LEFT_ALIGNMENT);
        pane.setBackground(UIManager.getColor("control"));

        JPanel panel = new JPanel();
        panel.setBorder(CommonUI.createBorder("Create Interaction"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(propertyRB);
        panel.add(eventRB);
        panel.add(pane);
        
        return panel;
    }

    /** 
     * Handles the RadioButton selection.
     */
    public void actionPerformed(ActionEvent evt)  {
        Object obj = evt.getSource();
        if (obj instanceof JRadioButton)  {
            
            // Reconfigurure the Wizard depending on the user selection.
            // Not very effecient since everything will be recreated if
            // the user switches Radio buttons more than once.
        
            if (obj == propertyRB)  {
                // Property hookup selected
                isPropertyHookup = true;
                wizard.setPanels(createPropertyPanels());
            } else if (obj == eventRB) {
                // Event hookup selected
                isPropertyHookup = false;
                wizard.setPanels(createEventPanels());
            }
        }
    }
    
    /** 
     * Flag which indicates if the mode is property hookup.
     */
    public boolean isPropertyHookup()  {
        return isPropertyHookup;
    }
    
    /** 
     * Construct the panels for an Event adapter hookup.
     * @return A vector containing the panels.
     */
    private Vector createEventPanels()  {
        Vector panels = new Vector(3);

        sourcePanel = createSourcePanel();
        targetPanel = createTargetPanel();
        argsPanel = createArgsPanel();

        panels.addElement(sourcePanel);
        panels.addElement(targetPanel);
        panels.addElement(argsPanel);
        
        return panels;
    }

    /** 
     * Construct the panels for a property assocation.
     */    
    private Vector createPropertyPanels()  {
        Vector panels = new Vector(1);

        propPanel = createPropertyPanel();
        panels.addElement(propPanel);
        
        return panels;
    }

    /**
     * Retrieves the selected source event descriptor or null.
     */
    public EventSetDescriptor getSourceEvent()  {
        return (EventSetDescriptor)eventsList.getSelectedValue();
    }

    /**
     * Retrieves the selected source method descriptor or null if a
     * method hasn't been selected.
     */
    public MethodDescriptor getSourceMethod()  {
        return (MethodDescriptor)methodList.getSelectedValue();

    }

    /**
     * Retrieves the selected target method descriptor or null if a
     * method hasn't been selected.
     */
    public MethodDescriptor getTargetMethod()  {
        MethodDescriptor desc = null;
        
        if (isPropertyHookup)  {
            // Get the method descriptor from the selected property
            // XXX - maybe we should treat properties differently from method selection.
            PropertyDescriptor prop;
            prop = (PropertyDescriptor)targetPropsList.getSelectedValue();
            if (prop != null)  {
                desc = new MethodDescriptor(prop.getWriteMethod());
            }
        } else {
            desc = (MethodDescriptor)targetMethodsList.getSelectedValue();
        }
        return desc;
    }

    /**
     * Returns an array of objects which represents the argument array.
     */
    public Object[] getTargetArguments()  {
        Object[] args = null;

        // Use the matching source method selection as the precedence.
        MethodDescriptor desc = (MethodDescriptor)sourceMethodsList.getSelectedValue();
        if (desc != null)  {
            args = new Object[] { desc.getMethod() };
        } else {
            int count = argsModel.getRowCount();

            if (count > 0)  {
                args = new Object[count];
                for (int i = 0; i < count; i++) {
                    args[i] = argsModel.getValueAt(i, COLUMN_VALUE_NUM);
                }
            }
        }
        return args;
    }

    //
    // Private methods.
    //

    /** 
     * Creates the panel for selecting the target property type
     * Only PropertyDescriptors which has the same type as the source object
     * will be displayed.
     */
    private JPanel createPropertyPanel()  {
        PropertyDescriptor[] properties = targetInfo.getPropertyDescriptors();
        
        // Property List. Use collections to filter out unwritable properties
        ArrayList list = new ArrayList();
        list.addAll(Arrays.asList(properties));
        
        ListIterator iterator = list.listIterator();
        PropertyDescriptor desc;
        while (iterator.hasNext()) {
            desc = (PropertyDescriptor)iterator.next();
            Class propType = desc.getPropertyType();
            Class sourceType = source.getClass();
            
            if (desc.getWriteMethod() == null || !propType.isAssignableFrom(sourceType))  {
                // Remove read only methods.
                iterator.remove();
            }
        }
        properties = (PropertyDescriptor[])list.toArray(new PropertyDescriptor[list.size()]);
        Arrays.sort(properties, comparator);

        targetPropsList = new JList(properties);
        targetPropsList.setCellRenderer(new PropertyListRenderer());

        JPanel p1 = new JPanel();
        JLabel label = CommonUI.createLabel("Source Object will be the Property Value");
        label.setFont(labelFont);
        label.setForeground(Color.black);
        p1.add(label);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setName("Property Type Selection");
        panel.add(p1, BorderLayout.NORTH);
        panel.add(CommonUI.createListPane(targetPropsList, "Property Set Methods on Target"), BorderLayout.CENTER);
        panel.setPreferredSize(preferredSize);

        return panel;
    }

    /** 
     * Renders a Property in a list of Properties
     */
    private class PropertyListRenderer extends DefaultListCellRenderer  {
        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus)  {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof PropertyDescriptor)  {
                PropertyDescriptor desc = (PropertyDescriptor)value;
                this.setText(desc.getDisplayName() + formatParameters(desc.getWriteMethod()));
            }
            return this;
        }
    }

    /**
     * A panel which allows for the selection of a Event activity.
     */
    private JPanel createSourcePanel()  {
        EventSetDescriptor[] descriptors = sourceInfo.getEventSetDescriptors();
        Arrays.sort(descriptors, comparator);

        eventsList = new JList(descriptors);
        eventsList.setCellRenderer(new EventSetListRenderer());
        eventsList.addListSelectionListener(new EventSetListListener());

        // The list of methods is loaded depending on the Event set selection.
        methodList = new JList();
        methodList.setCellRenderer(new MethodListRenderer());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setName("Source Events and Methods");

        JPanel p1 = new JPanel();
        JLabel label = CommonUI.createLabel("Select Event Method for " + 
                                    getRootName(source.getClass().getName()));
        label.setFont(labelFont);
        label.setForeground(Color.black);
        p1.add(label);

        JPanel p2 = new JPanel(new GridLayout(1, 2));
        p2.add(CommonUI.createListPane(eventsList, "Event Sets"));
        p2.add(CommonUI.createListPane(methodList, "Event Methods"));

        panel.add(p1, BorderLayout.NORTH);
        panel.add(p2, BorderLayout.CENTER);
        panel.setPreferredSize(preferredSize);

        return panel;
    }

    /** 
     * Creates the panel which allows for the selection of the target object
     * method to be invoked.
     * Available methods will only have a zero or one parameter.
     */
    private JPanel createTargetPanel()  {
        MethodDescriptor[] mdescriptors = targetInfo.getMethodDescriptors();

        // Filter the methods
        ArrayList list = new ArrayList();
        list.addAll(Arrays.asList(mdescriptors));

        ListIterator iterator = list.listIterator();
        MethodDescriptor desc;
        Class type;
        Class[] smParams;
        while (iterator.hasNext()) {
            desc = (MethodDescriptor)iterator.next();
            smParams = desc.getMethod().getParameterTypes();

            if (smParams != null)  {
                // Reject listener registration methods and methods which 
                // require more than one Parameter.
                if (smParams.length > 1 || 
                    smParams.length == 1 && EventListener.class.isAssignableFrom(smParams[0])) {
                    iterator.remove();
                }
            }
        }

        // Write the filtered MethodDescriptor array and sort
        mdescriptors = (MethodDescriptor[])list.toArray(new MethodDescriptor[list.size()]);
        Arrays.sort(mdescriptors, comparator);

        targetMethodsList = new JList(mdescriptors);
        targetMethodsList.setCellRenderer(new MethodListRenderer());
        targetMethodsList.addListSelectionListener(new MethodListListener());

        JPanel p1 = new JPanel();
        JLabel label = CommonUI.createLabel("Select Target Method for " + 
                                        getRootName(target.getClass().getName()));
        label.setFont(labelFont);
        label.setForeground(Color.black);
        p1.add(label);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setName("Target Method Selection");
        panel.add(p1, BorderLayout.NORTH);
        panel.add(CommonUI.createListPane(targetMethodsList, "Target Methods"),
                                    BorderLayout.CENTER);
        panel.setPreferredSize(preferredSize);

        return panel;
    }

    /**
     * Handler when the event listeners list changes. The methods from the
     * selected EventSetDescriptor are loaded into the methods list.
     */
    private class EventSetListListener implements ListSelectionListener  {
        public void valueChanged(ListSelectionEvent evt)  {
            if (!evt.getValueIsAdjusting())  {
                EventSetDescriptor value = (EventSetDescriptor)eventsList.getSelectedValue();

                if (value != null)  {
                    MethodDescriptor[] descriptors =  value.getListenerMethodDescriptors();
                    Arrays.sort(descriptors, comparator);
                    DefaultListModel model = new DefaultListModel();

                    for (int i = 0; i < descriptors.length; i++) {
                        model.addElement(descriptors[i]);
                    }
                    methodList.setModel(model);
                    methodList.setSelectedIndex(0);
                }
            }
        }
    }

    /**
     * Renders an EventSetDescriptor in the list of event sets
     */
    private class EventSetListRenderer extends DefaultListCellRenderer  {
        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus)  {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof EventSetDescriptor)  {
                EventSetDescriptor desc = (EventSetDescriptor)value;
                this.setText(desc.getDisplayName());
            }
            return this;
        }
    }


    /**
     * Handler when the Target method list changes. The arguments types from
     * the selected method are populated in the Arguments panel.
     */
    private class MethodListListener implements ListSelectionListener  {
        public void valueChanged(ListSelectionEvent evt)  {
            if (!evt.getValueIsAdjusting())  {
                // Set the argument panel based on the target method.
                MethodDescriptor value = getTargetMethod();

                if (value != null)  {
                    Method method = value.getMethod();
                    Class[] params = method.getParameterTypes();
                    if (params != null && params.length > 0)  {
                        // Set the label of the class.method.
                        StringBuffer buffer = new StringBuffer();

                        buffer.append(getRootName(target.getClass().getName()));
                        buffer.append(".");
                        buffer.append(method.getName());
                        buffer.append(formatParameters(method));

                        argsLabel.setText("Arguments for: " + buffer.toString());

                        configArgumentPanel(method);
                    } else {
                        // No argments, reset the models.
                        sourceMethodsList.setModel(new DefaultListModel());
                        argsModel = new ArgumentsModel();
                        argsTable.setModel(argsModel);
                        
                        argsLabel.setText("No Arguments Required");
                    }
                }
            }
        }

        /**
         * Set the argument panel based on the target method arguments
         * and source methods whose return value matches the arguments.
         */
        private void configArgumentPanel(Method method)  {

            // Load the arguments table model with the parameter types
            Class[] params = method.getParameterTypes();
            argsModel.setRowCount(params.length);
            for (int i = 0; i < params.length; i++) {
                argsModel.setValueAt(params[i], i, COLUMN_TYPE_NUM);
            }

            // If there is a single parameter, then allow a choice of getting the
            // value from the source object that matches the signature.
            // i.e., allow for the configuration of target.setText(source.getText())
            if (params.length == 1) {
                MethodDescriptor[] descs = sourceInfo.getMethodDescriptors();
                ArrayList list = new ArrayList();
                list.addAll(Arrays.asList(descs));

                ListIterator iterator = list.listIterator();
                MethodDescriptor desc;
                Class type;
                Class[] smParams;
                while (iterator.hasNext()) {
                    desc = (MethodDescriptor)iterator.next();
                    type = desc.getMethod().getReturnType();
                    smParams = desc.getMethod().getParameterTypes();

                    if (!params[0].isAssignableFrom(type) || smParams.length > 0)  {
                        // Reject null return types, type mismatches, or methods
                        // that requires additional parameters.
                        iterator.remove();
                    }
                }

                // Write the filtered MethodDescriptor array.
                descs = (MethodDescriptor[])list.toArray(new MethodDescriptor[list.size()]);
                DefaultListModel model = new DefaultListModel();

                for (int i = 0; i < descs.length; i++) {
                    model.addElement(descs[i]);
                }
                sourceMethodsList.setModel(model);
            } else {
                // Reset the list.
                sourceMethodsList.setModel(new DefaultListModel());
            }
        } // end configArgumentPanel

    }

    // The data model for the arguments array.
    private class ArgumentsModel extends DefaultTableModel  {
     
        public int getColumnCount() { 
            return 2;  
        }
        
        public boolean isCellEditable(int row, int column) {
            if (column == 0)
                return false;
            else
                return true;
        }
    }


    /**
     * Renders a Method in the list of methods.
     */
    private class MethodListRenderer extends DefaultListCellRenderer  {
        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus)  {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof MethodDescriptor)  {
                MethodDescriptor desc = (MethodDescriptor)value;

                /* XXX - this is the way that the paramter list should be built.
                         however, the MethodDescriptor only returns an array that
                         was set. It's not contstructed dynamically from reflecting on
                         the enclosed Method.
                ParameterDescriptor[] descriptors = desc.getParameterDescriptors();
                if (descriptors != null) {
                    for (int i = 0; i < descriptors.length; i++) {
                        params += descriptors[i].getDisplayName();
                        params += (i == descriptors.length - 1) ? " " : ", ";
                    }
                } */

                this.setText(desc.getDisplayName() + formatParameters(desc.getMethod()));
            }
            return this;
        }
    }

    /**
     * Create the panel which will prompt for the target arguments.
     */
    private JPanel createArgsPanel()  {

        argsLabel = CommonUI.createLabel("<target class>.<target method>(param types)");
        argsLabel.setFont(labelFont);
        argsLabel.setForeground(Color.black);
        JPanel p1 = new JPanel();
        p1.add(argsLabel);

        // Create the data model
        argsModel = new ArgumentsModel();

        DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
        TableColumn column = new TableColumn();
        column.setHeaderValue(COLUMN_TYPE);
        column.setPreferredWidth(75);
        column.setMinWidth(25);
        columnModel.addColumn(column);

        column = new TableColumn(1);
        column.setHeaderValue(COLUMN_VALUE);
        column.setPreferredWidth(100);
        column.setCellEditor(new PropertyCellEditor());
        columnModel.addColumn(column);

        argsTable = new JTable(argsModel, columnModel);
        argsTable.setRowHeight(16); // not very Java but it looks nicer.
        JScrollPane pane = new JScrollPane(argsTable);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setName("Arguments");
        panel.add(p1, BorderLayout.NORTH);
        panel.add(pane, BorderLayout.CENTER);

        // A list of source method which match the target args
        sourceMethodsList = new JList();
        sourceMethodsList.setCellRenderer(new MethodListRenderer());

        panel.add(CommonUI.createListPane(sourceMethodsList, "Matching Source \"getter\" Methods"),
                                    BorderLayout.SOUTH);
        panel.setPreferredSize(preferredSize);
        return panel;
    }

    /**
     * Editors for cells in property table
     * XXX Note: This is very similar to PropertyValueEditor and
     * PropertyValueRenderer. There should be some way to consolidate all this.
     */
    private class PropertyCellEditor extends AbstractCellEditor implements TableCellEditor {

        private PropertyEditor editor;
        private JTextField textField;
        private DefaultCellEditor cellEditor;

        public PropertyCellEditor()  {
            cellEditor = new DefaultCellEditor(new JTextField());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected,
                                                     int row, int column)  {
            Class cls = (Class)argsModel.getValueAt(row, COLUMN_TYPE_NUM);

            // XXX - This method call is very expensive should be cached by type.
            editor = PropertyEditorManager.findEditor(cls);
            if (editor != null)  {
                editor.setValue(value);
                editor.addPropertyChangeListener(new PropertyChangeListener()  {
                    public void propertyChange(PropertyChangeEvent evt)  {
                        stopCellEditing();
                    }
                });
                Component comp = editor.getCustomEditor();
                if (comp != null)  {
                    comp.setEnabled(isSelected);
                    return comp;
                }
            }
            return cellEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
        }

        public Object getCellEditorValue() {
            Object obj = null;

            if (editor != null)  {
                obj = editor.getValue();
            }

            if (obj == null)  {
        	    // most likely to return a string.
                obj = cellEditor.getCellEditorValue();
            }
            return obj;
        }
    }
    
    // Testing
    public static void main(String[] args)  {
        JFrame frame = new JFrame();
        
        javax.swing.JButton button = new javax.swing.JButton("Button");
        javax.swing.JTextField field = new javax.swing.JTextField("TextField");

        
        InteractionWizard wizard = new InteractionWizard(frame, button, field);
        wizard.setVisible(true);
        
    }

}
