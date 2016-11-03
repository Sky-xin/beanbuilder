/*
 * Copyright 1998 Sun Microsystems, Inc. All Rights Reserved
 */

/*
 * 
 */

package com.sun.java.swing.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18NMessages {
    private static ResourceBundle bundle;

    public static String getMessage(String key){
        if (bundle == null) {
            try{
                bundle = ResourceBundle.getBundle(/*NOI18N*/"com.sun.java.swing.ui.resources.UIConstants");
            } catch(MissingResourceException e){
                System.out.println("ui.I18NMessages: " + e.getMessage());
                System.out.println("ui.I18NMessages: " + e.getClassName());
                System.out.println("ui.I18NMessages: " + e.getKey());
                System.exit(0);
            }
        }
        
        String value = /*NOI18N*/"";
        try{
            value = (String) bundle.getObject(key);
        }catch (MissingResourceException e){
            System.out.println(/*NOI18N*/"ui.I18NMessages:Could not find " + key);
        }
        return value;
    }
}
