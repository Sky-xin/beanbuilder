/*
 * Copyright 1999 Sun Microsystems, Inc. All Rights Reserved
 */

/*
 * $Id: ConsoleStatusBar.java,v 1.9 1999/04/26 23:30:51 senthils Exp $
 */

package beantest;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

// Status bar for the console.
public class StatusBar extends JPanel implements ActionListener {
    
    private static final int PROGRESS_MAX = 100;
    private static final int PROGRESS_MIN = 0;
    
    private JLabel label;
    private Dimension preferredSize;

    private JProgressBar progressBar;
    private Timer timer;

    // The direction that the progress bar is moving.
    private boolean forward;

    public StatusBar()  {

        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBorder(BorderFactory.createEtchedBorder());
    
        // Create the progress bar.
        progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 
                                PROGRESS_MIN, PROGRESS_MAX);
        progressBar.setPreferredSize(new Dimension(60, progressBar.getPreferredSize().height + 2));
        progressBar.setVisible(false);
                                
        label = new JLabel(/*NOI18N*/"                                                                                        ");
        preferredSize = new Dimension(getWidth(label.getText()), 2 * getFontHeight());

        this.add(progressBar);
        this.add(label);
    }
    
    /*
     * Returns the string width
     * @param s the string
     * @return the string width
     */
    protected int getWidth(String s) {

        FontMetrics fm = this.getFontMetrics(this.getFont());
        if (fm == null) {
            return 0;
        }
        return fm.stringWidth(s);
    }

    /*
     * Returns the height of a line of text
     * @return the height of a line of text
     */
    protected int getFontHeight() {

        FontMetrics fm = this.getFontMetrics(this.getFont());
        if (fm == null) {
            return 0;
        }
        return fm.getHeight();
    }

    /**
     * Returns the perferred size
     * @return the preferred size
     */
    public Dimension getPreferredSize() {
        return preferredSize;
    }

    /**
     * Sets non-transient status bar message
     * @param message the message to display on the status bar
     */
    public void setMessage(String message) {

        label.setText(message);
        label.repaint();
    }
    
    /** 
     * Starts the busy bar.
     */
    public void startBusyBar()  {
        forward = true;
        if (timer == null)  {
            setMessage(/*NOI18N*/"");
            progressBar.setVisible(true);
            timer = new Timer(15, this);
            timer.start();
        }
    }
    
    /** 
     * Stops the busy bar. No penalty for calling it.
     */
    public void stopBusyBar()  {
        if (timer != null)  {
            timer.stop();
            timer = null;
        }
        setMessage(/*NOI18N*/"");
        progressBar.setVisible(false);
        progressBar.setValue(PROGRESS_MIN);
    }
    
    //
    // ActionListener method.
    //
    
    
    
    public void actionPerformed(ActionEvent evt)  {
        int value = progressBar.getValue();
        
        if (forward)  {
            // The progress bar is incresing
            if (value < PROGRESS_MAX)  {
                progressBar.setValue(value + 1);
            } else {
                forward = false;
                progressBar.setValue(value - 1);
            }
        } else {
            // The progress bar is decreasing
            if (value > PROGRESS_MIN)  {
                progressBar.setValue(value - 1);
            } else {
                forward = true;
                progressBar.setValue(value + 1);
            }
        }
    }

} // end class StatusBar


