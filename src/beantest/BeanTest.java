/**
 * @(#)BeanTest.java	1.18 99/11/18
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

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.beans.Beans;
import java.beans.BeanInfo;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import javax.swing.filechooser.FileFilter;

import beantest.action.BeantestActionManager;

import beantest.palette.Palette;

import com.sun.java.swing.ui.CommonUI;

// JavaBeans Archiving classes.
import archiver.XMLInputStream;
import archiver.XMLOutputStream;
import archiver.BeanScriptInputStream;
import archiver.BeanScriptOutputStream;
import archiver.JavaOutputStream;

/**
 * Top level frame for the bean tester.
 *
 * @version 1.18 11/18/99
 * @author  Mark Davidson
 */
public class BeanTest extends JFrame implements ActionListener, ItemListener,
                                                PropertyChangeListener {
    // String constants
    public static final String MAIN_TITLE = "Bean Box 2000";
    public static final String TITLE_DESIGN = MAIN_TITLE + " - Design Mode";
    public static final String TITLE_RUNTIME = MAIN_TITLE + " - Runtime Mode";

    // Chooser for file IO
    private JFileChooser chooser;
    
    // Chooser for IO of Jar files
    private JFileChooser jarchooser;

    // Widgets that are managed.
    private BeanMainMenu menu;
    private BeanToolBar toolbar;

    private BeanPanel panel;

    // The Component Palette
    private Palette palette;

    // Status bar
    private StatusBar status;

    // Manages all actions.
    private BeantestActionManager actionManager;

    // The thread for the action manager.
    private Thread actionThread;

    // This frame
    private static JFrame frame;

    // Current design filename
    private String filename = "TestOut.xml";

    /**
     * Creates the main UI for the Builder.
     */
    public BeanTest() {
        super(TITLE_DESIGN);

        Beans.setDesignTime(true);

        // Create first so that side effect messages have someplace to go.
        status = new StatusBar();

        // Create the main action manager
        actionManager = new BeantestActionManager();
        // The action manager sends messages to the status bar...
        actionManager.addPropertyChangeListener(this);
        // This class is the delegate that handles the actions.
        actionManager.addActionListener(this);
        actionManager.addItemListener(this);

        if (actionThread == null)  {
            actionThread = new Thread(actionManager, /*NOI18N*/"Action Thread");
            actionThread.start();
        }

        // clean up nicely
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });

        Container pane = getContentPane();

        palette = new Palette(); // TODO: Pass a palette file.
        palette.addPropertyChangeListener(this);

        panel = new BeanPanel(actionManager);
        panel.setPalette(palette);
        // The panel forwards messages to the status bar and selected item
        // notifications.
        panel.addPropertyChangeListener(this);

        menu = new BeanMainMenu(actionManager);
        toolbar = new BeanToolBar(actionManager);

        // Add all the components
        this.setJMenuBar(menu);
        pane.add(toolbar, BorderLayout.NORTH);
        pane.add(panel, BorderLayout.CENTER);
        pane.add(status, BorderLayout.SOUTH);

        this.pack();
        this.frame = this;
    }

    public static void main(String[] args)  {
        BeanTest beantest = new BeanTest();

        if (args.length == 0)  {
            // Create a new design.
            beantest.handleNew();
        } else {
            beantest.handleOpen(args[0]);
        }

        beantest.pack();
        beantest.setVisible(true);
    }

    /**
     * Action performed handles the command from the ActionManager.
     */
    public void actionPerformed(ActionEvent evt)  {
        CommonUI.setWaitCursor(getFrame());

        String command = evt.getActionCommand();

        if (command.equals(BeantestActionManager.CMD_ACTION_OPEN))  {
            handleOpen();
        } else if (command.equals(BeantestActionManager.CMD_ACTION_SAVE)) {
            handleSave();
        } else if (command.equals(BeantestActionManager.CMD_ACTION_SAVEAS)) {
            handleSaveAs();
        } else if (command.equals(BeantestActionManager.CMD_ACTION_EXIT)) {
            handleExit();
        } else if (command.equals(BeantestActionManager.CMD_ACTION_LOAD)) {
            handleLoad();
        } else if (command.equals(BeantestActionManager.CMD_ACTION_NEW)) {
            handleNew();
        } else if (command.equals(BeantestActionManager.CMD_ACTION_HELP)) {
            // handleHelp();
            handleAbout();
        } else if (command.equals(BeantestActionManager.CMD_ACTION_ABOUT)) {
            handleAbout();
        } else if (command.equals(BeantestActionManager.CMD_NAME_ICON_32C)) {
            handleIconChange(BeanInfo.ICON_COLOR_32x32);
        } else if (command.equals(BeantestActionManager.CMD_NAME_ICON_32M)) {
            handleIconChange(BeanInfo.ICON_MONO_32x32);
        } else if (command.equals(BeantestActionManager.CMD_NAME_ICON_16C)) {
            handleIconChange(BeanInfo.ICON_COLOR_16x16);
        } else if (command.equals(BeantestActionManager.CMD_NAME_ICON_16M)) {
            handleIconChange(BeanInfo.ICON_MONO_16x16);
        } else {
//            setStatusLine(command + " not implemented yet");
        }

        CommonUI.setDefaultCursor(getFrame());
    }

    /**
     * Responds to changes in state from the toggle buttons or
     * checkbox menu items.
     */
    public void itemStateChanged(ItemEvent evt)  {
        Action action = (Action)evt.getItem();
        String name = (String)action.getValue(Action.NAME);
        int state = evt.getStateChange();

        if (name.equals(BeantestActionManager.CMD_NAME_DESIGN))  {
            // Set the mode to Design or Runtime.
            if (state == ItemEvent.SELECTED)  {
                Beans.setDesignTime(true);
            } else {
                Beans.setDesignTime(false);
            }
            panel.setDesignMode(Beans.isDesignTime());
            setTitle();
        }

        if (name.equals(BeantestActionManager.CMD_NAME_PROP))  {
            // Show or hide the property panel. This is done for
            // performance reasons.
            if (state == ItemEvent.SELECTED)  {
                panel.showProperties(true);
            } else {
                panel.showProperties(false);
            }
        }
    }

    public void propertyChange(PropertyChangeEvent evt)  {
        String propName = evt.getPropertyName();

        if (propName.equals(BeantestActionManager.ACT_PROPERTY_MESSAGE)) {
            // Send the message to the status bar.
            String value = (String)evt.getNewValue();
            setStatusLine(value);
        }
    }

    /**
     * Handles all the cleanup for closing.
     */
    public void handleExit()  {
        frame.dispose();
        System.exit(0);
    }

    /**
     * Creates a new design
     */
    public void handleNew()  {
        JPanel p = new JPanel();
        p.setLayout(null);
        p.setPreferredSize(new Dimension(200, 200));

        this.filename = null;
        setTitle();
        panel.setRoot(p);
    }

    /**
     * Opens an archive from disk using the JFileChooser.
     */
    public void handleOpen()  {
        JFileChooser chooser = getFileChooser();
        
        if (chooser.showOpenDialog(getFrame()) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            String filename = selectedFile.getAbsolutePath();

            String extension = BuilderFileFilter.getExtension(filename);
            if (extension == null || extension.equals(""))  {
                filename += ".";

                FileFilter filter = chooser.getFileFilter();
                if (filter instanceof BuilderFileFilter)  {
                    // Use the selected item to set the extension.
                    filename += ((BuilderFileFilter)filter).getExtension();
                }
            }
            handleOpen(filename);
        }
    }

    /**
     * Reads a design into the builder.
     * @param filename Name of the file without the full path i.e., "TestOut.xml"
     */
    public void handleOpen(String filename)  {
        FileInputStream in = null;
        try {
             in = new FileInputStream(filename);
        } catch (Exception ex1) {
            // Send a message to the status bar and create a new design.
            setStatusLine("File not found: " + filename);
            handleNew();
            return;
        }

        String extension = BuilderFileFilter.getExtension(filename);
        ObjectInput s = null;

        try {
            if (extension.equals(XMLFileFilter.EXT)) {
                s = new XMLInputStream(in);
            } else if ( extension.equals(BSFileFilter.EXT) ) {
                s = new BeanScriptInputStream(in);
            } else if ( extension.equals(SerFileFilter.EXT) ) {
                s = new ObjectInputStream(in);
            } else {
                setStatusLine("File extension not recognized: " + filename);
                handleNew();
                return;
            }
        } catch (Exception ex) {
            setStatusLine("IO exception for: " + filename);
            handleNew();
            return;
        }

        Object root = null;
        try {
            root = s.readObject();
            s.close();
        } catch (Exception ex) {
            setStatusLine("Exception reading object: " + filename);
            handleNew();
            return;
        }

        if (root != null)  {
            panel.setRoot(root);
            this.filename = filename;
            setTitle();
        }
    }

    /**
     * Saves an object graph to the class variable filename. If filename is
     * null or empty then the file chooser is used to select a filename.
     * Note: This method has the side effect of setting the class variable
     *       filename
     */
    public void handleSave()  {
        if (filename == null || filename.equals(""))  {
            filename = getFilename();
            if (filename == null || filename.equals(""))  {
                return;
            }
        }
        handleSave(filename);
    }
    
    /**
     * Saves an object graph to the filename selected with JFileChooser.
     * Note: This method has the side effect of setting the class variable
     *       filename.
     */
    public void handleSaveAs()  {
        String retFilename = getFilename();
        if (retFilename == null || retFilename.equals(""))  {
            return;
        }
        this.filename = retFilename;
        
        handleSave(filename);
    }

    /**
     * Gets a file name with an extension using the FileChooser dialog.
     * @return User selected filename or the empty string if the operation
     *         was cancelled.
     */
    private String getFilename()  {
        JFileChooser chooser = getFileChooser();
        String filename = "";
        
        // Prompt for a filename.
        if (chooser.showSaveDialog(getFrame()) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            filename = selectedFile.getAbsolutePath();

            String ext = BuilderFileFilter.getExtension(selectedFile);
            if (ext == null)  {
                filename += ".";

                FileFilter filter = chooser.getFileFilter();
                if (filter instanceof BuilderFileFilter)  {
                    // Use the selected item to set the extension.
                    filename += ((BuilderFileFilter)filter).getExtension();
                } else {
                    // File will be an XML file by default.
                    filename += XMLFileFilter.EXT;
                }

            }
        }
        
        return filename;
    }

    /**
     * Saves the object graph that was constructed in the design panel
     * out to disk.
     * @param filename Name of the file to save as.
     */
    public void handleSave(String filename)  {
        try {
            setStatusLine("Writing " + filename + "...");
            FileOutputStream out = new FileOutputStream(filename);

            String ext = BuilderFileFilter.getExtension(filename);

            ObjectOutput s = null;

            if (ext.equals(XMLFileFilter.EXT))  {
                s = new XMLOutputStream(out);
            } else if (ext.equals(BSFileFilter.EXT))  {
                s = new BeanScriptOutputStream(out);
            } else if (ext.equals(JavaFileFilter.EXT)) {
                s = new JavaOutputStream(out);
            } else {
                s = new ObjectOutputStream(out);
            }
                        
            s.writeObject(panel.getRoot());
            s.close();

            this.filename = filename;
            setTitle();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /** 
     * Pops up a jar file chooser to select a jar file.
     */
    public void handleLoad()  {
        if (jarchooser == null)  {
            // File chooser for jar files
            jarchooser = new JFileChooser(new File("."));
            jarchooser.addChoosableFileFilter(new JarFileFilter());
        }

        if (jarchooser.showOpenDialog(getFrame()) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jarchooser.getSelectedFile();
            handleLoad(selectedFile.getAbsolutePath());
        }
        
    }
    
    /** 
     * Loads the jar file.
     */
    public void handleLoad(String filename)  {
        palette.addJarFile(filename);
    }

    /**
     * Displays the online help contents.
     */
    public void handleHelp()  {
//        HelpBrowser help = new HelpBrowser(getFrame());
//        help.showHelpContents();
        setStatusLine("Help not implemented yet");
    }

    /**
     * Displays the About box.
     */
    public void handleAbout()  {
        AboutDialog dlg = new AboutDialog(this);
        dlg.setVisible(true);
    }


    /**
     * Sets the type of icon in the palette
     * @param icon One of BeanInfo.ICON_...
     */
    public void handleIconChange(int icon)  {
        palette.setIconType(icon);
    }

    /**
     * Sets the text of the status bar
     */
	public void setStatusLine(String message) {
        status.setMessage(message);
	}

    /**
     * Sets the title of the frame based on the current state.
     */
    public void setTitle()  {
        String title;

        if (Beans.isDesignTime())  {
            title = TITLE_DESIGN;
        } else {
            title = TITLE_RUNTIME;
        }

        if (filename != null)  {
            title += " [" + filename + "]";
        }

        super.setTitle(title);
    }

    /**
     * Returns the Top level frame for the application.
     */
	public JFrame getFrame() {
	    return frame;
	}
    
    
    /** 
     * Gets the file chooser for saving/loading documents.
     */
    private JFileChooser getFileChooser()  {
        if (chooser == null)  {
            // Application file chooser for Open and Save.
            chooser = new JFileChooser(new File("."));
            chooser.addChoosableFileFilter(new JavaFileFilter());
            chooser.addChoosableFileFilter(new SerFileFilter());
            chooser.addChoosableFileFilter(new BSFileFilter());
            chooser.addChoosableFileFilter(new XMLFileFilter());
        }
        return chooser;
    }
}

/**
 * Filter for Bean Script files *.bs
 */
class BSFileFilter extends BuilderFileFilter  {

    public static final String EXT = "bs";

    public String getExtension()  {
        return EXT;
    }

    /** Returns the description of this filter */
    public String getDescription()  {
        return "Bean Script (*.bs)";
    }
}

/**
 * Filter for XML files *.xml
 */
class XMLFileFilter extends BuilderFileFilter  {

    public static final String EXT = "xml";

    public String getExtension()  {
        return EXT;
    }

    /** Returns the description of this filter */
    public String getDescription()  {
        return "XML Designs (*.xml)";
    }

}

/**
 * Filter for Serializd files *.ser
 */
class SerFileFilter extends BuilderFileFilter  {

    public static final String EXT = "ser";

    public String getExtension()  {
        return EXT;
    }

    /** Returns the description of this filter */
    public String getDescription()  {
        return "Serialized Files (*.ser)";
    }

}

/**
 * Filter for Jar files *.jar
 */
class JarFileFilter extends BuilderFileFilter  {

    public static final String EXT = "jar";

    public String getExtension()  {
        return EXT;
    }

    /** Returns the description of this filter */
    public String getDescription()  {
        return "Java Archives (*.jar)";
    }

}

/**
 * Filter for Java files *.java
 */
class JavaFileFilter extends BuilderFileFilter  {

    public static final String EXT = "java";

    public String getExtension()  {
        return EXT;
    }

    /** Returns the description of this filter */
    public String getDescription()  {
        return "Java Source (*.java)";
    }

}

/**
 * Generic FileFilter to handle Builder File types.
 */
abstract class BuilderFileFilter extends FileFilter  {

    /**
     * Return true if this file should be shown in the directory pane,
     * false if it shouldn't.
     */
    public boolean accept(File f)  {
        if (f != null)  {
            if (f.isDirectory())  {
                return true;
            }
        }
        String extension = getExtension(f);
        if (extension != null && extension.equals(getExtension()))
            return true;

        return false;
    }

    /** Returns the extension for the file */
    public static String getExtension(File file)  {
        if (file != null)  {
            String filename = file.getName();
            int i = filename.lastIndexOf('.');
            if (i > 0 && i < (filename.length() - 1))  {
                return filename.substring(i + 1).toLowerCase();
            }
        }
        return null;
    }

    /** Returns the extension for the file */
    public static String getExtension(String filename)  {
        if (filename != null)  {
            int i = filename.lastIndexOf('.');
            if (i > 0 && i < (filename.length() - 1))  {
                return filename.substring(i + 1).toLowerCase();
            }
        }
        return null;
    }

    /**
     * Returns the extension for this filter.
     */
    public abstract String getExtension();

    /**
     * Determines if the filename is the type described by the FileFilter.
     */
    public boolean isFileType(String filename)  {
        String ext = getExtension(filename);
        if (ext != null && ext.length() > 0)  {
            return getExtension().equals(ext);
        } else {
            return false;
        }
    }
}

