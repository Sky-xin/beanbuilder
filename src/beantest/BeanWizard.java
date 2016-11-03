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

package beantest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.BeanInfo;

import java.lang.reflect.Method;

import beantest.util.BeanInfoFactory;
import beantest.util.DescriptorComparator;

import com.sun.java.swing.ui.WizardDlg;

/**
 * A base class which handles the wizard interactions for Bean
 * components. This class creates the beaninfos for the source
 * and target Objects.
 * 
 * Subclasses are responsible for creating the panels and the Wizard
 * and implementing the handler for the next button.
 *
 * @version %I% %G%
 * @author  Mark Davidson
 */
public class BeanWizard {

    protected Object source;
    protected Object target;
    
    protected BeanInfo sourceInfo;
    protected BeanInfo targetInfo;

    // Shared instance of a comparator
    protected static DescriptorComparator comparator = new DescriptorComparator();
    
    // Wizard that handles the panel vector.
    protected WizardDlg wizard;

    // Flag to indicate that the finished button was pressed.
    private boolean finished = false;
    
    /**
     * ctor
     *
     * @param
     * @exception
     * @see
     */
    public BeanWizard(Object source, Object target) {
        this.source = source;
        this.target = target;
        
        sourceInfo = BeanInfoFactory.getBeanInfo(source.getClass());
        targetInfo = BeanInfoFactory.getBeanInfo(target.getClass());
    }

    /** 
     * Check to see if the Finished button was pressed.
     */
    public boolean isFinished()  {
        return finished;
    }
    
    /** 
     * Shows/Hides the wizard dialog
     */
    public void setVisible(boolean visible)  {
        //  nameTF.requestFocus();
        wizard.setVisible(true);
    }
    
    /** 
     * Returns the name of the root class from a full class path.
     * @param fullName The full name of the class i.e, java.awt.event.ActionListener
     * @return The root name of the class i.e., ActionListener
     */
    public String getRootName(String fullName)  {
        int i = fullName.lastIndexOf('.');
        String name = fullName.substring(i + 1);
        return name;
    }

    /** 
     * Returns a string that formats the parameters for a methods for display
     * @param method The method to format.
     */
    public String formatParameters(Method method)  {
        StringBuffer params = new StringBuffer("( ");
        
        Class[] paramTypes = method.getParameterTypes();
        if (paramTypes != null)  {
            for (int i = 0; i < paramTypes.length; i++) {
                params.append(getRootName(paramTypes[i].getName()));;
                params.append((i == paramTypes.length - 1) ? " " : ", ");
            }
        }
        params.append(" )");
        
        return params.toString();
    }

    /** 
     * Handles the cancel button
     */
    protected class CancelHandler implements ActionListener  {
        public void actionPerformed(ActionEvent evt)  {
        }        
    }

    /** 
     * Handles the wizard Finish button.
     */
    protected class FinishHandler implements ActionListener  {
        public void actionPerformed(ActionEvent evt)  {
            finished = true;
        }
    }


}