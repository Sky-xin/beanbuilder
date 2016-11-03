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

import com.sun.java.swing.action.ActionConstants;

/**
 * Constants used for Builder actions.
 *
 * @version %I% %G%
 * @author  Mark Davidson
 */
public interface BeanActionConstants extends ActionConstants {
    
    // Property change event keys.
    //
    
    // Notification that a bean should be added.
    public final static String ACT_PROPERTY_ADD = "add-command";
    // Selected object change
    public final static String ACT_PROPERTY_SELECT = "selected-command";

    public final static String CMD_NAME_ICON_32C = "Color 32x32";
    public final static String CMD_NAME_ICON_16C = "Color 16x16";
    public final static String CMD_NAME_ICON_32M = "Mono 32x32";
    public final static String CMD_NAME_ICON_16M = "Mono 16x16";
    
    public static final String CMD_ACTION_DESIGN = "design-command";
    public static final String CMD_NAME_DESIGN = "Design Mode";
    public static final String CMD_SHORT_DESCRIPTION_DESIGN = "Design Mode";
    public static final String CMD_LONG_DESCRIPTION_DESIGN = "Toggles design or preview mode";
    public static final int CMD_MNEMONIC_DESIGN = 'D';

    public static final String CMD_ACTION_PROP = "view-property-command";
    public static final String CMD_NAME_PROP = "View Properties";
    public static final String CMD_SHORT_DESCRIPTION_PROP = "View Properties";
    public static final String CMD_LONG_DESCRIPTION_PROP = "Toggles the display of the property pane";
    public static final int CMD_MNEMONIC_PROP = 'P';

    public static final String CMD_ACTION_SAVEAS = "save-as-command";
    public static final String CMD_NAME_SAVEAS = "Save As";
    public static final String CMD_SHORT_DESCRIPTION_SAVEAS = "Save As";
    public static final String CMD_LONG_DESCRIPTION_SAVEAS = "Saves the current design as another design";
    public static final int CMD_MNEMONIC_SAVEAS = 'A';

    public static final String CMD_ACTION_UP = "up-command";
    public static final String CMD_NAME_UP = "Move Up";
    public static final String CMD_SMALL_ICON_UP = "up16.gif";
    public static final String CMD_SHORT_DESCRIPTION_UP = "Move Up hierarchy";
    public static final String CMD_LONG_DESCRIPTION_UP = "Go to the parent object in the hierarchy";
    public static final int CMD_MNEMONIC_UP = 'P';

    public static final String CMD_ACTION_DOWN = "down-command";
    public static final String CMD_NAME_DOWN = "Move down";
    public static final String CMD_SMALL_ICON_DOWN = "down16.gif";
    public static final String CMD_SHORT_DESCRIPTION_DOWN = "Move down the hierarchy";
    public static final String CMD_LONG_DESCRIPTION_DOWN = "Step into current object";
    public static final int CMD_MNEMONIC_DOWN = 'N';
    
    public static final String CMD_ACTION_ADD = "add-command";
    public static final String CMD_NAME_ADD = "Add to design";
    public static final String CMD_SMALL_ICON_ADD = "add16.gif";
    public static final String CMD_SHORT_DESCRIPTION_ADD = "Adds selected object to design panel";
    public static final String CMD_LONG_DESCRIPTION_ADD = "Places the selected object into the design panel for manipulation";
    public static final int CMD_MNEMONIC_ADD = 'A';

    public static final String CMD_ACTION_LOAD = "load-command";
    public static final String CMD_NAME_LOAD = "Load Jar File";
    public static final String CMD_SHORT_DESCRIPTION_LOAD = "Loads a selected jar file into the palette";
    public static final String CMD_LONG_DESCRIPTION_LOAD = "Loads a jar file into the user palette";
    public static final int CMD_MNEMONIC_LOAD = 'L';

    public static final String CMD_ACTION_CUST = "customizer-command";
    public static final String CMD_NAME_CUST = "Shows the Customizer";
    public static final String CMD_SMALL_ICON_CUST = "prefs16.gif";
    public static final String CMD_SHORT_DESCRIPTION_CUST = "Customizer for the selected object";
    public static final String CMD_LONG_DESCRIPTION_CUST = "Displays the customizer for the selected object in a dialog";
    public static final int CMD_MNEMONIC_CUST = 'C';
}
