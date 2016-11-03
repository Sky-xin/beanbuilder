/*
 * Copyright 1999 Sun Microsystems, Inc. All Rights Reserved
 */

/*
 * %W% %E%
 */

package beantest.action;

import com.sun.java.swing.action.ActionManager;

/**
 * Provides the specific implementation of the ActionManager.
 *
 * @version %I% %G%
 * @author  Mark Davidson
 */
public class BeantestActionManager extends ActionManager 
                    implements BeanActionConstants {
    /**
     * Ctor for the action manager.
     */
    public BeantestActionManager() {
        addActions();
    }
    
    /** 
     * Adds commands to the action manager
     * Any new functionality should be added here.
     */
    protected void addActions()  {
        addAction(CMD_NAME_NEW, new NewAction(this));
        addAction(CMD_NAME_SAVE, new SaveAction(this));
        addAction(CMD_NAME_SAVEAS, new SaveAsAction(this));
        addAction(CMD_NAME_OPEN, new OpenAction(this));
        addAction(CMD_NAME_LOAD, new LoadAction(this));
        addAction(CMD_NAME_EXIT, new ExitAction(this));

        addAction(CMD_NAME_UP, new UpAction(this));
        addAction(CMD_NAME_DOWN, new DownAction(this));
        addAction(CMD_NAME_ADD, new AddAction(this));
        addAction(CMD_NAME_CUST, new CustomizerAction(this));

        addAction(CMD_NAME_ICON_32C, new IconAction(this, CMD_NAME_ICON_32C));
        addAction(CMD_NAME_ICON_16C, new IconAction(this, CMD_NAME_ICON_16C));
        addAction(CMD_NAME_ICON_32M, new IconAction(this, CMD_NAME_ICON_32M));
        addAction(CMD_NAME_ICON_16M, new IconAction(this, CMD_NAME_ICON_16M));
        
        addAction(CMD_NAME_PROP, new ViewPropertyAction(this));
        addAction(CMD_NAME_DESIGN, new DesignAction(this));

        addAction(CMD_NAME_HELP, new HelpAction(this));
        addAction(CMD_NAME_ABOUT, new AboutAction(this));
    }
}
