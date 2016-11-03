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

import java.io.File;

/**
 * ActionConstants Constants for naming, executing and enabling actions
 * TODO: Take out command center specific descriptions.
 *
 * @version %I% %G%
 * @author  Mark Davidson
 */
public interface ActionConstants  {

    // Properties events which affect the UI.
    
    /** Sends a message to the status bar */
    public static final String ACT_PROPERTY_MESSAGE = "message-command";
    /** Indicates the start of a long operation */
    public static final String ACT_PROPERTY_BEGIN_CMD = "begin-command";
    /** Indicates the end of a long operation */
    public static final String ACT_PROPERTY_END_CMD = "end-command";

    // Image directory URL
    public static final String IMAGE_DIR = "/images/";
    
    public static final String CMD_ACTION_NEW = "new-command";
    public static final String CMD_NAME_NEW = I18NMessages.getMessage("actionconstants.new");
    public static final String CMD_SMALL_ICON_NEW = "new16.gif";
    public static final String CMD_SHORT_DESCRIPTION_NEW = I18NMessages.getMessage("actionconstants.creates_a_new_configuration");
    public static final String CMD_LONG_DESCRIPTION_NEW = I18NMessages.getMessage("actionconstants.creates_a_new_configuration");
    public static final int CMD_MNEMONIC_NEW = 'N';
    
    public static final String CMD_ACTION_SAVE = "save-command";
    public static final String CMD_NAME_SAVE = I18NMessages.getMessage("actionconstants.save");
    public static final String CMD_SMALL_ICON_SAVE = "save16.gif";
    public static final String CMD_SHORT_DESCRIPTION_SAVE = I18NMessages.getMessage("actionconstants.save");
    public static final String CMD_LONG_DESCRIPTION_SAVE = I18NMessages.getMessage("actionconstants.saves_the_configuration");
    public static final int CMD_MNEMONIC_SAVE = 'S';

    public static final String CMD_ACTION_OPEN = "open-command";
    public static final String CMD_NAME_OPEN = I18NMessages.getMessage("actionconstants.open");
    public static final String CMD_SMALL_ICON_OPEN = "open16.gif";
    public static final String CMD_SHORT_DESCRIPTION_OPEN = I18NMessages.getMessage("actionconstants.open_dot__dot__dot_");
    public static final String CMD_LONG_DESCRIPTION_OPEN = I18NMessages.getMessage("actionconstants.opens_a_configuration");
    public static final int CMD_MNEMONIC_OPEN = 'O';

    public static final String CMD_ACTION_EXIT = "exit-command";
    public static final String CMD_NAME_EXIT = I18NMessages.getMessage("actionconstants.exit");
    public static final String CMD_SMALL_ICON_EXIT = /*NOI18N*/"";
    public static final String CMD_SHORT_DESCRIPTION_EXIT = I18NMessages.getMessage("actionconstants.exit");
    public static final String CMD_LONG_DESCRIPTION_EXIT = I18NMessages.getMessage("actionconstants.exits_the_application");
    public static final int CMD_MNEMONIC_EXIT = 'X';
 
    // Help menu
    public static final String CMD_ACTION_ABOUT = "about-command";
    public static final String CMD_NAME_ABOUT = I18NMessages.getMessage("actionconstants.about_command_center_dot__dot__dot_");
    public static final String CMD_SMALL_ICON_ABOUT = /*NOI18N*/"";
    public static final String CMD_SHORT_DESCRIPTION_ABOUT = I18NMessages.getMessage("actionconstants.about_command_center_dot__dot__dot_");
    public static final String CMD_LONG_DESCRIPTION_ABOUT = I18NMessages.getMessage("actionconstants.displays_version_information");
    public static final int CMD_MNEMONIC_ABOUT = 'A';

    public static final String CMD_ACTION_HELP = "help-command";
    public static final String CMD_NAME_HELP = I18NMessages.getMessage("actionconstants.contents_dot__dot__dot_");
    public static final String CMD_SMALL_ICON_HELP = "help16.gif";
    public static final String CMD_SHORT_DESCRIPTION_HELP = I18NMessages.getMessage("actionconstants.help_contents_dot__dot__dot_");
    public static final String CMD_LONG_DESCRIPTION_HELP = I18NMessages.getMessage("actionconstants.invokes_the_online_help_contents_page_dot__dot__dot_");
    public static final int CMD_MNEMONIC_HELP = 'C';

    public static final String CMD_ACTION_HELPINDEX = "helpindex-command";
    public static final String CMD_NAME_HELPINDEX = I18NMessages.getMessage("actionconstants.index_dot__dot__dot_");
    public static final String CMD_SMALL_ICON_HELPINDEX = /*NOI18N*/"";
    public static final String CMD_SHORT_DESCRIPTION_HELPINDEX = I18NMessages.getMessage("actionconstants.help_index_dot__dot__dot_");
    public static final String CMD_LONG_DESCRIPTION_HELPINDEX = I18NMessages.getMessage("actionconstants.invokes_the_online_help_index_page_dot__dot__dot_");
    public static final int CMD_MNEMONIC_HELPINDEX = 'I';

    public static final String CMD_ACTION_HELPSEARCH = "helpsearch-command";
    public static final String CMD_NAME_HELPSEARCH = I18NMessages.getMessage("actionconstants.search_dot__dot__dot_");
    public static final String CMD_SMALL_ICON_HELPSEARCH = /*NOI18N*/"";
    public static final String CMD_SHORT_DESCRIPTION_HELPSEARCH = I18NMessages.getMessage("actionconstants.help_search_dot__dot__dot_");
    public static final String CMD_LONG_DESCRIPTION_HELPSEARCH = I18NMessages.getMessage("actionconstants.invokes_the_online_help_search_page_dot__dot__dot_");
    public static final int CMD_MNEMONIC_HELPSEARCH = 'S';

}
