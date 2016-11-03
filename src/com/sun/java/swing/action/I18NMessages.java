/*
 * Copyright 1998 Sun Microsystems, Inc. All Rights Reserved
 */

/*
 * 
 */

package com.sun.java.swing.action;

import java.util.MissingResourceException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;

public class I18NMessages {
    private static ResourceBundle bundle;

    public static String getMessage(String key){
        if (bundle == null) {
            try{
                bundle = ResourceBundle.getBundle(/*NOI18N*/"com.sun.java.swing.action.resources.ActionProps");
            } catch(MissingResourceException e){
                System.out.println("action.I18NMessages: " + e.getMessage());
                System.out.println("action.I18NMessages: " + e.getClassName());
                System.out.println("action.I18NMessages: " + e.getKey());
                System.exit(0);
            }
        }
        String value = /*NOI18N*/"";
        try{
            value = (String) bundle.getObject(key);
        }catch (MissingResourceException e){
            System.out.println(/*NOI18N*/"action.I18NMessages: Could not find " + key);
        }
        return value;
    }
}
