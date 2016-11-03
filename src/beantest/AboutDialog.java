/*
 * @(#)AboutDialog.java	1.3 99/10/18
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
import java.awt.Container;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.sun.java.swing.ui.CommonUI;

/**
 * About box with information.
 *
 * @version 1.3 10/18/99
 * @author  Mark Davidson
 */
public class AboutDialog extends JDialog implements ActionListener {

    private static final String TITLE_ABOUT = "About BeanBox 2000";
    private static final String PRODUCT_NAME = "BeanBox 2000";
    private static final String VERSION = "Version: 0.1";
    private static final String COLUMN_PROP = "Property";
    private static final String COLUMN_VALUE = "Value";
    
    private static final String BORDERTEXT_PROPS = "System Properties";
    
    private JButton okButton;
    
    private JTable table;
    // Data model should be updated on notification.
    private DefaultTableModel dataModel;

    /**
     * ctor for About Box
     */
    public AboutDialog(JFrame parent) {
        super(parent, TITLE_ABOUT, true);
    
        Container pane = this.getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
    
        pane.add(createTitlePanel());
        pane.add(createPropertiesPanel());
        pane.add(new JPanel(new BorderLayout()));
        pane.add(createButtonPanel());
    
        addWindowListener(new WindowAdapter()  {
            public void windowClosing(WindowEvent evt)  {
                JDialog dialog = (JDialog)evt.getSource();
                dialog.dispose();
            }
        });
    
        this.pack();
        // Center the window.
        CommonUI.centerComponent(this, parent);
    }
    
    private JPanel createTitlePanel()  {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(CommonUI.createLabel(PRODUCT_NAME), BorderLayout.NORTH);
        panel.add(CommonUI.createLabel(VERSION), BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createPropertiesPanel()  {
        // Create the data model 
        dataModel = new DefaultTableModel()  {
            public int getColumnCount() { return 2;  }
            public boolean isCellEditable(int row, int column) { return false; }
        };
    
        DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
        TableColumn column = new TableColumn();
        column.setHeaderValue(COLUMN_PROP);
        column.setPreferredWidth(150);
        column.setMinWidth(25);
        columnModel.addColumn(column);
    
        column = new TableColumn(1);
        column.setHeaderValue(COLUMN_VALUE);
        column.setPreferredWidth(200);
        columnModel.addColumn(column);
    
        table = new JTable(dataModel, columnModel);
        JScrollPane pane = new JScrollPane(table);
        pane.setPreferredSize(new Dimension(350, 200));

        // Fill the table with content.
        Properties props = System.getProperties();
        // update by yue.yan @2016/11/2 enum改为enum1 原因：java5之后enum是一个关键字 
        Enumeration enum1 = props.propertyNames();
        String key;
        Vector row;
        
        while (enum1.hasMoreElements()) {
            key = (String)enum1.nextElement();
            row = new Vector();
            row.addElement(key);
            row.addElement(props.getProperty(key));
        
            dataModel.addRow(row);
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(pane, BorderLayout.CENTER);
        panel.setBorder(CommonUI.createBorder(BORDERTEXT_PROPS));
        
        return panel;
    }
    
    private JPanel createButtonPanel()  {
        okButton = CommonUI.createButton(CommonUI.BUTTONTEXT_OK, this,
                                         CommonUI.MNEMONIC_OK);
        JPanel panel = new JPanel();
        panel.add(okButton);
        
        return panel;
    }
    
    // ActionListener method.
    public void actionPerformed(ActionEvent evt)  {
        this.dispose();
    }
    
}
