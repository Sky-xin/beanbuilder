/*
 * Copyright %G% Sun Microsystems, Inc. All Rights Reserved
 */

/*
 * %W% %E%
 */

package beantest;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;

import com.sun.java.swing.ui.CommonMenuBar;

import com.sun.java.swing.action.AbstractItemAction;

import beantest.action.BeantestActionManager;

/**
 * Specific menubar for the BeanTest appliction.
 *
 * @version %I% %G%
 * @author  Mark Davidson
 */
public class BeanMainMenu extends CommonMenuBar {
    
    public BeanMainMenu(BeantestActionManager manager)   {
        super(manager);
    }

    /** 
     * Overloaded method to configure the menu.
     */
    protected void configureMenu()  {
        add(createFileMenu("File", 'f'));
        add(createViewMenu("View", 'v'));
        add(createIconsMenu("Icons", 'i'));
        add(createHelpMenu("Help", 'h'));
    }

    /** 
     * Create the file menu
     */
    private JMenu createFileMenu(String name, char mnemonic)  {
        JMenu menu = createMenu(name, mnemonic);

        // Add commands
        addMenuItem(menu, manager.getAction(BeantestActionManager.CMD_NAME_NEW));
        addMenuItem(menu, manager.getAction(BeantestActionManager.CMD_NAME_OPEN));
        addMenuItem(menu, manager.getAction(BeantestActionManager.CMD_NAME_SAVE));
        addMenuItem(menu, manager.getAction(BeantestActionManager.CMD_NAME_SAVEAS));
        menu.addSeparator();
        addMenuItem(menu, manager.getAction(BeantestActionManager.CMD_NAME_LOAD));
        menu.addSeparator();
        addMenuItem(menu, manager.getAction(BeantestActionManager.CMD_NAME_EXIT));
        
        return menu;
    }

    /** 
     * Create the view menu
     */
    private JMenu createViewMenu(String name, char mnemonic)  {
        JMenu menu = createMenu(name, mnemonic);

        // Add commands (both on by default)
        addCheckBoxMenuItem(menu, (AbstractItemAction)manager.getAction(BeantestActionManager.CMD_NAME_DESIGN), true);
        addCheckBoxMenuItem(menu, (AbstractItemAction)manager.getAction(BeantestActionManager.CMD_NAME_PROP), true);
        
        return menu;
    }
    
    /** 
     * Create the icons menu
     */
    private JMenu createIconsMenu(String name, char mnemonic)  {
        JMenu menu = createMenu(name, mnemonic);

        ButtonGroup group = new ButtonGroup();

        // Add Radio buttons. The first item is the default
        addRadioButtonMenuItem(menu, group, manager.getAction(BeantestActionManager.CMD_NAME_ICON_32C), true);
        addRadioButtonMenuItem(menu, group, manager.getAction(BeantestActionManager.CMD_NAME_ICON_16C));
        addRadioButtonMenuItem(menu, group, manager.getAction(BeantestActionManager.CMD_NAME_ICON_32M));
        addRadioButtonMenuItem(menu, group, manager.getAction(BeantestActionManager.CMD_NAME_ICON_16M));
        
        return menu;
    }
    
    /** 
     * Create the help menu
     */
    private JMenu createHelpMenu(String name, char mnemonic)  {
        JMenu menu = createMenu(name, mnemonic);
        
        addMenuItem(menu, manager.getAction(BeantestActionManager.CMD_NAME_HELP));
        menu.addSeparator();
        addMenuItem(menu, manager.getAction(BeantestActionManager.CMD_NAME_ABOUT));
        
        return menu;
    }
}