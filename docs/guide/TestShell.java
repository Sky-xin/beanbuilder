/*
 * @(#)TestShell.java	1.1 99/10/10
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

import archiver.*;
import java.io.*;
import javax.swing.*;

/**
 * @version 1.1 10/10/99
 * @author Philip Milne
 */

public class TestShell { 
    public static void main(String[] args) {
        try {
            if (args.length == 1) { 
            	String name = args[0]; 
            	String extension = name.substring(name.lastIndexOf('.')+1);
            	InputStream s = new FileInputStream(name); 
            	ObjectInput i = new BeanScriptInputStream(s);
                if (extension.equals("xml")) { i = new XMLInputStream(s); }
                if (extension.equals("ser")) { i = new ObjectInputStream(s); }
                if (extension.equals("bs")) { i = new BeanScriptInputStream(s); }
            	Object result = i.readObject(); 
                if (result instanceof JPanel) { 
                    JPanel panel = (JPanel)result; 
                    JFrame frame = new JFrame();
                    frame.setContentPane(panel);
                    frame.setSize(panel.getSize());
                    frame.show();                
                } 
                else { 
                    System.out.println("Unarchived: " + result); 
                }
            }
            else { 
                ObjectInput i = new BeanScriptInputStream(System.in);
                while(true) {
                    System.out.print("> "); 
		    System.out.println(i.readObject());
                    Thread.yield();
                }
                //i.close(); 
           }

        }
	catch (Exception e) {
            e.printStackTrace();
	}    
    }
}



