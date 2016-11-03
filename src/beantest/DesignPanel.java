/*
 * @(#)DesignPanel.java	1.12 99/11/09
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Rectangle;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.beans.BeanInfo;
import java.beans.BeanDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeListener;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import beantest.palette.Palette;

import beantest.action.BeantestActionManager; // Just for Prop change constants.

import beantest.util.BeanInfoFactory;

/**
 * A Root pane which manages the manipulation of the components
 *
 * @version 1.12 11/09/99
 * @author  Mark Davidson
 */
public class DesignPanel extends JRootPane implements HandleManager {

    private JPanel panel;       // The container which gets manipulated.
    private Object item;        // The current selected item.
    private GlassPane glass;    // Layer in which objects are visually manipulated.
    private Palette palette;    // Reference to the current component palette.

    private Handle[] handles;

    private HashMap wrappers;   // hashtable which holds non-visual beans and wrappers.

    public static final int GRID_SIZE = 10;
    public static final int WRAPPER_SIZE = 50;

    // Default location of a new non-visual bean.
    private Point newPoint = new Point();

    public DesignPanel()  {
        getContentPane().setLayout(new BorderLayout());

        this.handles = null;
        this.panel = null;

        this.wrappers = new HashMap();

        glass = new GlassPane();
        setGlassPane(glass);

        // Design mode on by default
        setDesignMode(true);
    }

    /**
     * Sets the root container. JPanel for now.
     */
    public void setRoot(Object root)  {
        if (root instanceof JPanel)  {
            if (panel != null)  {
                // Remove old design.
                this.getContentPane().removeAll();
                glass.removeObjects();

                // Hide the handles. We could delete them as well but
                if (handles != null)  {
                    for (int i = 0; i < handles.length; i++) {
                        handles[i].setVisible(false);
                    }
                }
            }

            panel = (JPanel)root;
            this.getContentPane().add(panel, BorderLayout.CENTER);
        }
    }

    public Object getRoot()  {
        return panel;
    }

    /**
     * Sets the Palette in which the manipulated panel will get the components
     */
    public void setPalette(Palette palette)  {
        this.palette = palette;
    }

    /**
     * Overloaded so that the glass pane can send property events.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)  {
        super.addPropertyChangeListener(listener);
        if (glass != null)
            glass.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)  {
        super.removePropertyChangeListener(listener);
        if (glass != null)
            glass.removePropertyChangeListener(listener);
    }

    /**
     * Sets the mode of the design panel
     * @param design Flag which indicates that the panel is in design mode.
     */
    public void setDesignMode(boolean design)  {
        glass.setVisible(design);
    }

    /**
     * Sets the selected item for a component. This is a bound property.
     * @param item Object to select. This can be a component or a non-visual bean.
     * You should never pass a BeanWrapper as a selected item.
     */
    public void setSelectedItem(Object item)  {
        if (item instanceof BeanWrapper)  {
            throw new IllegalArgumentException("A BeanWrapper cannot be the selected item");
        }

        Object oldItem = this.item;
        this.item = item;
        if (oldItem == item)  {
            return;
        }

        firePropertyChange(BeantestActionManager.ACT_PROPERTY_SELECT, oldItem, item);

        // Set the visual in the UI
        Component component;

        if (item instanceof Component) {
            component = (Component)item;
        } else {
            // The selected item is non-visual so create a wrapper and add it
            // to the glass pane.
            component = (Component)wrappers.get(item);

            if (component == null)  {
                glass.doAddObject(item, getDefaultPoint());
            }
        }

        if (handles == null)  {
            // Handles have not been created so create them and put them
            // on the glass pane layer.
            handles = new Handle[9];

            handles[Handle.NW_HANDLE] = new Handle(this, component, Handle.NW_HANDLE);
            handles[Handle.N_HANDLE] = new Handle(this, component, Handle.N_HANDLE);
            handles[Handle.NE_HANDLE] = new Handle(this, component, Handle.NE_HANDLE);
            handles[Handle.E_HANDLE] = new Handle(this, component, Handle.E_HANDLE);
            handles[Handle.SE_HANDLE] = new Handle(this, component, Handle.SE_HANDLE);
            handles[Handle.S_HANDLE] = new Handle(this, component, Handle.S_HANDLE);
            handles[Handle.SW_HANDLE] = new Handle(this, component, Handle.SW_HANDLE);
            handles[Handle.W_HANDLE] = new Handle(this, component, Handle.W_HANDLE);
            handles[Handle.C_HANDLE] = new Handle(this, component, Handle.C_HANDLE);

            for (int i = 0; i < handles.length; i++) {
                glass.add(handles[i]);
            }
        }

        if (handles != null)  {
            for (int i = 0; i < handles.length; i++) {
                handles[i].setComponent(component);
                handles[i].setVisible(true);
            }
        }

        glass.requestFocus();
    }

    /**
     * Moves the handles as a result of a resize.
     *
     * A better thing to do would be to implement a layout manager on the
     * JLayeredPane to do this. This assumes that the component has been
     * resized to reflect the size of its enclosed ui component.
     */
    public void repositionHandles(Rectangle rect)  {

        if (handles != null)  {
            Dimension d = new Dimension(rect.width, rect.height);
            Point position = new Point(rect.x, rect.y);

            handles[Handle.NW_HANDLE].setBounds(position.x, position.y,
                    Handle.DOT_DIM, Handle.DOT_DIM);

            position.translate((d.width - Handle.DOT_DIM)/2, 0);
            handles[Handle.N_HANDLE].setBounds(position.x, position.y,
                    Handle.DOT_DIM, Handle.DOT_DIM);

            position.translate((d.width - Handle.DOT_DIM)/2, 0);
            handles[Handle.NE_HANDLE].setBounds(position.x, position.y,
                    Handle.DOT_DIM, Handle.DOT_DIM);

            position.translate(0, (d.height - Handle.DOT_DIM)/2);
            handles[Handle.E_HANDLE].setBounds(position.x, position.y,
                    Handle.DOT_DIM, Handle.DOT_DIM);

            position.translate(0, (d.height - Handle.DOT_DIM)/2);
            handles[Handle.SE_HANDLE].setBounds(position.x, position.y,
                    Handle.DOT_DIM, Handle.DOT_DIM);

            position.translate(-(d.width - Handle.DOT_DIM)/2, 0);
            handles[Handle.S_HANDLE].setBounds(position.x, position.y,
                    Handle.DOT_DIM, Handle.DOT_DIM);

            position.translate(-(d.width - Handle.DOT_DIM)/2, 0);
            handles[Handle.SW_HANDLE].setBounds(position.x, position.y,
                    Handle.DOT_DIM, Handle.DOT_DIM);

            position.translate(0, -(d.height - Handle.DOT_DIM)/2);
            handles[Handle.W_HANDLE].setBounds(position.x, position.y,
                    Handle.DOT_DIM, Handle.DOT_DIM);

            position.translate((d.width - Handle.DOT_DIM)/2, 0);
            handles[Handle.C_HANDLE].setBounds(position.x, position.y,
                    Handle.DOT_DIM, Handle.DOT_DIM);
        }
    }

    /** XXX - experimental
     * Notification that the drag gesture has completed. 
     * If the component was dropped onto a container then add the component 
     * to the container.
     *
     * @param point The point where the gesture ended
     * @param comp The component on which the gesture was acted upon.
     *
    public void endDragGesture(Point point, Component comp)  {
    
        // XXX - debug
        System.out.println("DesignPanel.endDragGesture - pt: " + point);
        Container container = glass.getDropContainer(point);
        
        if (container != null)  {
            panel.remove(comp);
            container.add(comp);
        }
    } */



    /**
     * Adds the interaction on the target object to the source object. This method uses the
     * JDK 1.3 Dynamic Proxy Class API.
     * Formerly, this type of behaviour was achieved by dynamically generating listener classes
     * using a technique that's described in
     * http://java.sun.com/products/jfc/tsc/tech_topics/generic-listener/listener.html
     * <p>
     * @param source The object which is the originator of the interaction.
     * @param sourceEvent The event which triggers the interaction.
     * @param sourceMethod The method that the source object will call.
     * @param target The object wish is the target of the interaction.
     * @param targetMethod The target method that will be called.
     * @param targetArgs An array of arguments for the target method.
     */
    public void addInteraction(Object source, EventSetDescriptor sourceEvent,
                               MethodDescriptor sourceMethod, Object target,
                               MethodDescriptor targetMethod, Object[] targetArgs) {

        InteractionHandler handler = new InteractionHandler();

        handler.setSourceMethod(sourceMethod.getMethod());
        handler.setTargetMethod(targetMethod.getMethod());
        handler.setSource(source);
        handler.setTarget(target);
        handler.setTargetArgs(targetArgs);

        Object proxy = Proxy.newProxyInstance(source.getClass().getClassLoader(),
                            new Class[] { sourceEvent.getListenerType() }, handler);

        Method addMethod = sourceEvent.getAddListenerMethod();

        try {
            // Add the listener to the source.
            addMethod.invoke(source, new Object[] { proxy });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Adds a bean to the designer. Public method which doesn't rely on
     * GlassPane interaction
     */
    public void addBean(Object bean)  {
        if (bean instanceof Component)  {
            glass.doAddComponent((Component)bean, getDefaultPoint());
        } else {
            glass.doAddObject(bean, getDefaultPoint());
        }

        setSelectedItem(bean);
    }

    /**
     * Removes the bean from the designer. Side effect is to remove it from other
     * places in the application.
     */
    public void removeBean(Object bean)  {
        if (bean != null && bean != panel)  {
            if (bean instanceof Component)  {
                glass.doRemoveComponent((Component)bean);
            } else {
                glass.doRemoveObject(bean);
            }

            setSelectedItem(getRoot());
        }
    }

    /**
     * Returns a default point based on the last location of the point and an
     * increment.
     */
    protected Point getDefaultPoint()  {
        Dimension size = this.getSize();

        if (newPoint.x + WRAPPER_SIZE > size.width ||
            newPoint.y + WRAPPER_SIZE > size.height)  {
            // Component will be placed off the panel to reset it to the origin.
            newPoint.move(0, 0);
        }
        newPoint.translate(GRID_SIZE, GRID_SIZE);

        return newPoint;
    }

    /**
     * A pane which is on top of the design panel which intercepts mouse events.
     * The pane allows for the instantiation of Components from the palette
     * and it also allows for the selection of source and target objects for
     * event interactions.
     */
    public class GlassPane extends JPanel implements MouseListener,
                                MouseMotionListener, KeyListener {
        private Point startPt;
        private Point endPt;

        private Object source;
        private Object target;

        private boolean eventHookup = false; // indicates event hookup mode.

        private final int MIN_WIDTH = 10;
        private final int MIN_HEIGHT = 10;

        public GlassPane()  {
            this.setOpaque(false);
            this.setLayout(null);

            this.source = null;
            this.target = null;

            this.startPt = null;
            this.endPt = null;

            // Register this to recieve events
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
            this.addKeyListener(this);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (target != null && target instanceof Component)  {
                Rectangle rect = ((Component)target).getBounds();
                g.setColor(Color.green);
                g.drawRect(rect.x, rect.y, rect.width, rect.height);
            }

            if (startPt != null && endPt != null)  {
                g.setColor(Color.blue);

                if (eventHookup)  {
                    // Draw a line for component interaction
                    g.drawLine(startPt.x, startPt.y, endPt.x, endPt.y);
                } else {
                    g.drawRect(startPt.x, startPt.y,
                               endPt.x - startPt.x, endPt.y - startPt.y);
                }
            }
        }

        //
        // KeyListener methods
        //

        public void keyTyped(KeyEvent evt) {}
        public void keyPressed(KeyEvent evt) {}

        /**
         * Deletes the selected bean.
         */
        public void keyReleased(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_DELETE)  {
                String message = "Bean Removed: " + item.getClass().getName();
                removeBean(item);
                firePropertyChange(BeantestActionManager.ACT_PROPERTY_MESSAGE,
                                        "", message);
            }
        }

        /**
         * Select the component under the mouse cursor
         */
        public void mouseClicked(MouseEvent evt)  {
            Object obj = resolveObject(evt);
            if (obj != null)
                setSelectedItem(obj);
        }

        public void mouseEntered(MouseEvent evt)  {}
        public void mouseExited(MouseEvent evt)  {}

        /**
         * Start a gesture. Event or Property hookup or Bean
         * instantiation.
         */
        public void mousePressed(MouseEvent evt)  {
            source = resolveObject(evt);

            if (source != null)  {
                startPt = evt.getPoint();
            } else {
                startPt = null;
            }

            if (palette.isItemSelected())  {
                // If a Palette item is selected then the mouse gesture will
                // instantiate an item.
                eventHookup = false;
            } else {
                eventHookup = true;
            }
        }

        /**
         * Determines if the mouse has been clicked on a non-visual component
         * which is represented on the glass pane or a visual compnent which
         * is in the panel of the content pane.
         * <p>
         * If the event was on a BeanWrapper then it will translate the point to
         * the parent co-ordinates.
         */
        public Object resolveObject(MouseEvent evt)  {
            Object obj = null;

            if (evt.getSource() instanceof BeanWrapper)  {
                // Translate to glass pane co-ords.
                obj = evt.getSource();
                Point location = ((Component)obj).getLocation();
                evt.translatePoint(location.x, location.y);
            }

            // Look for the object on the glassPane first.
            obj = this.getComponentAt(evt.getPoint());

            if (obj == null || obj == this)  {
                obj = panel.getComponentAt(evt.getPoint());
            }

            if (obj instanceof BeanWrapper)  {
                BeanWrapper wrapper = (BeanWrapper)obj;
                obj = wrapper.getBean();
            }

            return obj;
        }

        public void mouseDragged(MouseEvent evt)  {
            target = resolveObject(evt);
            endPt = evt.getPoint();
            if (target == source || target == panel)  {
                target = null;
            }

            this.repaint();
        }

        public void mouseMoved(MouseEvent evt)  {}

        /**
         * This gesture is overloaded to hookup events, properties and
         * instantiate beans from the Palette.
         */
        public void mouseReleased(MouseEvent evt)  {

            if (eventHookup)  {
                if (source != null && target != null && source != target && target != panel)  {
                    doEventHookup(source, target);
                }
            } else {
                // Add the palette bean to the design.
                doAddBean(evt.getPoint());
            }

            eventHookup = false;

            // Reset the values
            source = null;
            target = null;

            startPt = null;
            endPt = null;

            this.repaint();
        }

        /**
         * Add the selected Palette item to the design.
         */
        private void doAddBean(Point mousePt)  {
            // Instantiate a new component from the palette
            Object bean = palette.getNewPaletteBean();
            if (bean == null)  {
                /*
                // XXX - Experiment perhaps a component was dragged
                if (source != null && target != null)  {
                    if (target instanceof Container && target != panel)  {
                        // XXX - debug
                        System.out.print("doAddBean source: " + source);
                        System.out.println(" drop target: " + target);
                    }
                } */
                return;
            }

            if (bean instanceof Component)  {
                doAddComponent((Component)bean, mousePt);
            } else {
                doAddObject(bean, mousePt);
            }

            setSelectedItem(bean);
        }

        /**
         * Adds a visual bean to the design and the root panel.
         * Determines the location and size of the component from 
         * it's the preferred size and the mouse point.
         */
        public void doAddComponent(Component comp, Point mousePt)  {
            // Set the location
            if (startPt == null)  {
                startPt = mousePt;
            }

            if (endPt == null)  {
                endPt = (Point)startPt.clone();
                endPt.translate(MIN_WIDTH, MIN_HEIGHT);
            }

            Dimension size = new Dimension(endPt.x - startPt.x, endPt.y - startPt.y);
            Dimension minSize = comp.getMinimumSize();
            size.width = Math.max(size.width, minSize.width);
            size.height = Math.max(size.height, minSize.height);

            comp.setBounds(startPt.x, startPt.y, size.width, size.height);

            Container container = getDropContainer(mousePt);
            if (container == null)  {
                container = (Container)panel;
            }
            
            container.add(comp);
            container.validate();
        }
        
        /** 
         * Returns a container delegate for the swing component under the mouse point.
         * <p>
         * This method uses the "isContainer" and "containerDelegate"
         * flags defined in the SwingBeanInfo classes. That is, components are
         * added to containers which are managed by Swing containers. The
         * container delegate accessor method is made available with the
         * container delegte flag.
         *
         * @return Container container delegate under the mouse or null
         */
        private Container getDropContainer(Point point)  {
            Container container = null;

            // Add the component to the target container. Add it to the
            // root panel by default.
            target = panel.getComponentAt(point);
            if (target != null)  {
                // Determine if this target is a swing container.
                BeanInfo info = BeanInfoFactory.getBeanInfo(target.getClass());

                if (info != null)  {
                    BeanDescriptor desc = info.getBeanDescriptor();

                    Boolean flag = (Boolean)desc.getValue("isContainer");
                    if (flag != null && flag.booleanValue())  {
                        // If the target is a Swing container then get the container
                        // delegate and use that component as the container to add.
                        String methodName = (String)desc.getValue("containerDelegate");
                        if (methodName != null)  {
                            try {
                                Method method = target.getClass().getMethod(methodName,
                                                                    new Class[]{});
                                Object delegate = method.invoke(target, new Object[]{});
                                container = (Container)delegate;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    } // end flag
                }
            }
            return container;
        }

        /**
         * Removes the component from the root panel.
         */
        public void doRemoveComponent(Component comp)  {
            // XXX - how do we support removal from a container delegate?
            // Better yet. How do we select a Component within a container
            // delegate from the designer?
            panel.remove(comp);
            panel.validate();
        }

        /**
         * Adds the non visual bean to the design panel.
         * <p>
         * Non visual beans are wrapped with component and added to the glass pane.
         */
        public void doAddObject(Object obj, Point point)  {
            BeanWrapper wrapper = new BeanWrapper(obj);
            wrapper.setLocation(point);

            // The glass pane should listen to mouse events for control.
            wrapper.addMouseListener(this);
            wrapper.addMouseMotionListener(this);

            // Add the wrapper to the glass pane
            this.add(wrapper);
            this.validate();

            wrappers.put(obj, wrapper);
        }

        /**
         * Remove the non-visual bean from the designer.
         */
        public void doRemoveObject(Object obj)  {
            BeanWrapper wrapper = (BeanWrapper)wrappers.get(obj);

            if (wrapper != null) {
                wrapper.removeMouseListener(this);
                wrapper.removeMouseMotionListener(this);

                this.remove(wrapper);
                this.validate();
            }
        }

        /**
         * Removes and unregisters all the BeanWrappers.
         */
        public void removeObjects()  {
            Iterator iterator = wrappers.values().iterator();

            BeanWrapper wrapper;
            while (iterator.hasNext()) {
                wrapper = (BeanWrapper)iterator.next();
                wrapper.removeMouseListener(this);
                wrapper.removeMouseMotionListener(this);

                this.remove(wrapper);
            }
        }

        /**
         * Callback for event hookup gesture. Presents a wizard to the user in order to
         * prompt for the source and target methods
         */
        private void doEventHookup(Object source, Object target)  {
            InteractionWizard wizard = new InteractionWizard(source, target);

            wizard.setVisible(true);

            if (wizard.isFinished())  {
            
                if (wizard.isPropertyHookup())  {
                    // Perform a property association between the source object and
                    // the target object The target method is called on the target with the
                    // source object as the argument.
                    // i.e., If the source object is a TableModel and the target is a JTable,
                    // the targetMethod should be setModel(TableModel).
                    MethodDescriptor targetMethod = wizard.getTargetMethod();
                    if (targetMethod != null)  {
                        Method method = targetMethod.getMethod();
                        try {
                            // Set the source on the target listener.
                            method.invoke(target, new Object[] { source });
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    // Get the information from wizard and set up the interaction.
                    // This creates an event adapter from the Proxy API with the
                    // selected methods and arguments from the Wizard.
                    EventSetDescriptor sourceEvent = wizard.getSourceEvent();
                    MethodDescriptor sourceMethod = wizard.getSourceMethod();
                    MethodDescriptor targetMethod = wizard.getTargetMethod();
                    Object[] targetArgs = wizard.getTargetArguments();

                    if (sourceEvent != null && sourceMethod != null && targetMethod != null)  {
                        addInteraction(source, sourceEvent, sourceMethod, target, targetMethod, targetArgs);
                    }
                }
            }

        }

    } // end class GlassPane


}

/**
 * This is a component class that wraps a non-visual bean.
 */
class BeanWrapper extends JComponent  {
    private Object bean;

    private static Dimension preferredSize = new Dimension(DesignPanel.WRAPPER_SIZE,
                                                          DesignPanel.WRAPPER_SIZE);
    public BeanWrapper()  {
        this(null);
    }

    public BeanWrapper(Object bean)  {
        setBean(bean);
        setToolTipText(bean.getClass().getName());
        setSize(getPreferredSize());
        setBorder(BorderFactory.createEtchedBorder(Color.black, Color.darkGray));
    }

    public void setBean(Object bean)  {
        this.bean = bean;
    }

    public Object getBean()  {
        return bean;
    }

    public Dimension getPreferredSize()  {
        return preferredSize;
    }
}
