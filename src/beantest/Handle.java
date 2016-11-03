/*
 * @(#)Handle.java	1.2 99/10/27
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

/**
 * Represents a resizing rectangle. This class implements the behavior such
 * that when the object is dragged, it will send a message to it's parent component
 * to set the constraints to itself.
 *
 * @version 1.2 10/27/99
 * @author  Mark Davidson
 */
public class Handle extends JComponent implements MouseListener, 
    MouseMotionListener {
  
    public static final int DOT_DIM = 6;
  
    // Const for handle locations
    public static final int NW_HANDLE = 0;
    public static final int N_HANDLE  = 1;
    public static final int NE_HANDLE = 2;
    public static final int E_HANDLE  = 3;
    public static final int SE_HANDLE = 4;
    public static final int S_HANDLE  = 5;
    public static final int SW_HANDLE = 6;
    public static final int W_HANDLE  = 7;
    public static final int C_HANDLE  = 8;
  
    private int cursorType; // One of Cursor.xxx constants
    private int handleType; // a handle location constant
    private final static Color handleColor = Color.blue;
      
    private Component component;
    private HandleDragInfo dinfo;
    
    // The container which manages a set of handles.
    private HandleManager manager;

    // Constructors
    public Handle(HandleManager manager, Component component, int type) {
        this.manager = manager;
        this.handleType = type;
        setComponent(component);
      
        // Set the bounds of the Handle depending on the type of cursor
        switch(type) {
            case NW_HANDLE:   cursorType = Cursor.NW_RESIZE_CURSOR; break;
            case N_HANDLE:    cursorType =  Cursor.N_RESIZE_CURSOR; break;
            case NE_HANDLE:   cursorType = Cursor.NE_RESIZE_CURSOR; break;
            case W_HANDLE:    cursorType = Cursor.W_RESIZE_CURSOR;  break;
            case E_HANDLE:    cursorType = Cursor.E_RESIZE_CURSOR;  break;
            case SW_HANDLE:   cursorType = Cursor.SW_RESIZE_CURSOR; break;
            case S_HANDLE:    cursorType = Cursor.S_RESIZE_CURSOR;  break;
            case SE_HANDLE:   cursorType = Cursor.SE_RESIZE_CURSOR; break;
            case C_HANDLE:    cursorType = Cursor.MOVE_CURSOR; break;
        }

        // Register this to recieve events
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    
    public void setComponent(Component component)  {
        this.component = component;
        positionHandle();
    }
    
    public void mouseClicked(MouseEvent evt)  {
    }
    
    public void mouseEntered(MouseEvent evt)  {
        if (isEnabled())
            setCursor(Cursor.getPredefinedCursor(cursorType));
    }
    
    public void mouseExited(MouseEvent evt)  {
        if (isEnabled())
            setCursor(Cursor.getDefaultCursor());
    }
    
    public void mousePressed(MouseEvent evt)  {
        if (isEnabled())
            startDragGesture(evt.getX(), evt.getY());
    }
    
    public void mouseReleased(MouseEvent evt)  {
        if (isEnabled())
            endDragGesture(evt.getX(), evt.getY());
    }
    
    public void mouseDragged(MouseEvent evt)  {
        if (isEnabled())
            dragGesture(evt.getX(), evt.getY());
    }
    
    public void mouseMoved(MouseEvent evt)  {
    }
    
    // Paint the object
    protected void paintComponent(Graphics g) {
        g.setPaintMode();
              
        Dimension size = getSize();
      
        if (isEnabled())  {
            g.setColor(handleColor);
            g.fillRect(0, 0, size.width, size.height);
        } else {
            g.setColor(Color.white);
            g.fillRect(0, 0, size.width, size.height);
            g.setColor(handleColor);
            g.drawRect(0, 0, size.width - 1, size.height - 1);
        }
    }
    
    /********************************************************************
      Private gesture methods.
     ********************************************************************/
    
    /**
     * Start of a drag gesture.
     * @param x the x coordinate.
     * @param y the y coordinate.
     * @param rightBtn was it the right button.
     */
    private void startDragGesture(int x, int y) {
        Point pt = new Point(x, y);
        dinfo = null;
        
        // set up the node drag information.
        HandleDragInfo info = new HandleDragInfo();
        info.comp = component;
        info.startPt = pt;
        info.lastPt = null;
        info.bounds = new Rectangle(info.comp.getBounds());
        info.btext = null;
        
        dinfo = info;
    }

    /**
     * On going dragging gesture.
     * 
     * Note: The point in this mouse event is relative to the location 
     * of the handle and represents the delta for which the component will
     * be resized.
     *
     * @param x the x coordinate.
     * @param y the y coordinate.
     */
    private void dragGesture(int ptx, int pty) {
        if (dinfo == null) 
            return;

        Rectangle bounds = dinfo.comp.getBounds();
        Rectangle newbounds = new Rectangle(bounds);

        // Calculate the new width, height and position 
        // XXX - there may be a simpler way of calculating this using
        // diffs on dinfo.bounds.
        
        int x = bounds.x + ptx;
        int y = bounds.y + pty;
        int width = bounds.width + (bounds.x - x);
        int height = bounds.height + (bounds.y - y);
        
        // Recalc for the width
        if (handleType == NE_HANDLE || handleType == E_HANDLE || 
            handleType == SE_HANDLE)
            width = bounds.width + (ptx - dinfo.startPt.x);
            
        // Recalc for the height
        if (handleType == SW_HANDLE || handleType == S_HANDLE || 
            handleType == SE_HANDLE)
            height = bounds.height + (pty - dinfo.startPt.y);
        
        // don't let the component have -ve values.
        if (handleType != C_HANDLE && (height < 0 || width < 0))
            return;
        
        switch (handleType) {
            case NW_HANDLE:
                newbounds.setBounds(x, y, width, height);
                break;
        
            case N_HANDLE:
                newbounds.setBounds(bounds.x, y, bounds.width, height);
                break;
        
            case NE_HANDLE:
                newbounds.setBounds(bounds.x, y, width, height);
                break;
        
            case W_HANDLE:
                newbounds.setBounds(x, bounds.y, width, bounds.height);
                break;
        
            case E_HANDLE:
                newbounds.setSize(width, bounds.height);
                break;
        
            case SW_HANDLE:
                newbounds.setBounds(x, bounds.y, width, height);
                break;
        
            case S_HANDLE:
                newbounds.setSize(bounds.width, height);
                break;
        
            case SE_HANDLE:
                newbounds.setSize(width, height);
                break;
        
            case C_HANDLE:
                newbounds.setLocation(x, y);
                break;
        }
            
        // Don't draw if the new rectangle is the same as the last one
        if (newbounds.x == dinfo.bounds.x && 
            newbounds.y == dinfo.bounds.y && 
            newbounds.width == dinfo.bounds.width && 
            newbounds.height == dinfo.bounds.height) {
            return;
        }

        // Tricky: draw on the graphics context of the component's parent.
        Graphics g = dinfo.comp.getParent().getGraphics();
        if (g != null) {

            // erase the old XOR residue if it exists.
            if (dinfo.lastPt != null)  {
                g.setXORMode(getBackground());
                dinfo.draw(g);
                g.setPaintMode();
            } else {
                dinfo.lastPt = dinfo.startPt;
            }
            
            // Set the new bounds values and text.
            dinfo.bounds = newbounds;
            dinfo.btext = String.valueOf(dinfo.bounds.width) + 'x' + 
                            dinfo.bounds.height;
            
            // draw the new stuff.
            g.setXORMode(getBackground());
            dinfo.draw(g);
            g.setPaintMode();
            g.dispose();
        }
    }

    /**
     * End of the drag gesture.
     * @param x the x coordinate.
     * @param y the y coordinate.
     */
    private void endDragGesture(int x, int y) {
        if (dinfo == null) 
            return;

        // Tricky: draw on the graphics context of the component's parent. Not
        // the enclosed LayoutEditor which may or may not be the same thing.
        Graphics g = dinfo.comp.getParent().getGraphics();
        if (g != null) {
            if (dinfo.lastPt != null)  {
                g.setXORMode(getBackground());
                dinfo.draw(g);
                g.setPaintMode();
            }
            g.dispose();
        }

        // Set the new bounds on the component
        component.setBounds(dinfo.bounds);
        
        // Move the handles based on the new bounds
        manager.repositionHandles(dinfo.bounds);
        
        dinfo = null;
    }
    
    /** 
     * Position the handle based on the bounds of the component.
     */
    public void positionHandle()  {
        Rectangle rect = component.getBounds();
        Rectangle bounds = new Rectangle(rect.x, rect.y, DOT_DIM, DOT_DIM);
        
        switch(handleType) {
            case NW_HANDLE: 
                break;
            case N_HANDLE:  
                bounds.translate((rect.width - DOT_DIM)/2, 0); 
                break;
            case NE_HANDLE: 
                bounds.translate((rect.width - DOT_DIM), 0); 
                break;
            case W_HANDLE:  
                bounds.translate(0, (rect.height - DOT_DIM)/2);  
                break;
            case SW_HANDLE: 
                bounds.translate(0, (rect.height - DOT_DIM)); 
                break;
            case E_HANDLE:  
                bounds.translate((rect.width - DOT_DIM), (rect.height - DOT_DIM)/2);  
                break;
            case S_HANDLE:
                bounds.translate((rect.width - DOT_DIM)/2, (rect.height - DOT_DIM));  
                break;
            case SE_HANDLE:
                bounds.translate((rect.width - DOT_DIM), (rect.height - DOT_DIM));  
                break;            
            case C_HANDLE:
                bounds.translate((rect.width - DOT_DIM)/2, (rect.height - DOT_DIM)/2);  
                break;
        }
        this.setBounds(bounds);
    }
    

    /** 
     * Holds information on the drag handle information
     */
    private class HandleDragInfo {

        // Starting point of the drag.
        public Point startPt;

        // Last saved point in the drag.
        public Point lastPt;

        // The component getting resized.
        public Component comp;
        
        // Bounds of the handle after it's resized.
        public Rectangle bounds;
        
        // String that corresponds to the rect bounds.
        public String btext;
        
        /** 
         * Return the bounding retangle
         */
        public Rectangle getBounds()  {
            return bounds;
        }
        
        /** 
         * Paint the drag mode
         * 
         * @param g the graphics context
         */
        public void draw(Graphics g)  {
            g.setColor(Color.black);
            g.drawRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
            
            if (btext != null)  {
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(btext);
                int textHeight = fm.getHeight();
                
                g.drawString(btext, bounds.x + (bounds.width - textWidth)/2, 
                                          bounds.y + (bounds.height + textHeight)/2);
            }

        }
    }

}
