/*
 * @(#) @(#)SplashScreen.java	1.5
 *
 * Copyright 07/17/98 Sun Microsystems, Inc. All Rights Reserved.
 *
 */

package com.sun.java.swing.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Window;

/**
 * This class puts up the splash screen.
 *
 * @version 1.5
 * @author John Cho
 */
public class SplashScreen extends Window {
    
    private Image screen;

    /**
     * Constructor.
     * @param f the parent frame.
     */
    public SplashScreen(Frame f) {
        super(f);
        setBackground(Color.white);

        screen = getToolkit().getImage(/*NOI18N*/"images/SplashScreen.gif");
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(screen, 0);
        try {
            mt.waitForAll();
        } catch (Exception e) {
        }
    }

    /**
     * Override the setVisible call.
     * @param val show or not show.
     */
    public void setVisible(boolean val) {
        if (val == true) {
            setSize(442, 347);
            setLocation(-500, -500);
            super.setVisible(true);
        
            Dimension d = getToolkit().getScreenSize();
            Insets i = getInsets();
            int w = 442 + i.left + i.right;
            int h = 347 + i.top + i.bottom;
            setSize(w, h);
            setLocation(d.width / 2 - w / 2, d.height / 2 - h / 2);
        } else {
            super.setVisible(false);
        }
    }

    /**
     * Override the paint call.
     * @param g the graphics context.
     */
    public void paint(Graphics g) {
        if (screen != null) {
            Dimension d = getSize();
            g.setColor(Color.black);
            g.drawRect(0, 0, d.width - 1, d.height - 1);
            g.drawImage(screen, 1, 1, this);
        }
    }

}
