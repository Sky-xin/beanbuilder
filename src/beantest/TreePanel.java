/*
 * @(#)TreePanel.java	1.9 99/11/09
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import java.beans.BeanInfo;
import java.beans.BeanDescriptor;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;

import javax.swing.event.TreeModelEvent;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import beantest.util.BeanInfoFactory;
import beantest.util.TreeModelSupport;

/**
 * Panel which encapsulates a Containment hierarchy in a scrollpane.
 * This tree only displays visual beans.
 *
 * @version 1.9 11/09/99
 * @author  Mark Davidson
 */
public class TreePanel extends JScrollPane {

    private JTree tree;
    private ContainerTreeModel treeModel;

    /**
     * Creates a tree panel with the tree model
     */
    public TreePanel()  {
        tree = new JTree();

        // Show tooltips for each item.
        ToolTipManager.sharedInstance().registerComponent(tree);

        getViewport().add(tree);
    }

    /**
     * Sets the root of the tree.
     * @param root Root of the tree node.
     */
    public void setRoot(Object root)  {
    
        if (root instanceof Container)  {
            treeModel = new ContainerTreeModel((Container)root);
            tree.setModel(treeModel);
            tree.setCellRenderer(new BeanCellRenderer());

            // Expand all nodes
            int row = 0;
            while(row < tree.getRowCount())  {
                tree.expandRow(row);
                row++;
            }
        }
    }
    /**
     * Updates the tree selection. Currently supports visual hierarchy.
     */
    public void setSelectedItem(Object item) {
        
        if (item instanceof Component)  {
            Object[] path = treeModel.getPathToRoot((Component)item);
            tree.setSelectionPath(new TreePath(path));
        }
    }

    public Dimension getPreferredSize()  {
        return new Dimension(200, 200);
    }

    /**
     * Returns the tree (for listener registration).
     */
    public JTree getTree()  {
        return tree;
    }

    /**
     * Returns the tree model
     */
    public TreeModel getModel()  {
        return treeModel;
    }

    /**
     * Renders a node in the tree.
     */
    public class BeanCellRenderer extends DefaultTreeCellRenderer {

        // Caches the icons from the BeanInfo on a per type basis.
        Hashtable icons = new Hashtable();

        /**
         * This is called from JTree whenever it needs to get the size
         * of the component or it wants to draw it.
         */
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                    boolean sel,
                                                    boolean expanded,
                                                    boolean leaf, int row,
                                                    boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded,
                                        leaf, row, hasFocus );

            if (value instanceof Component)  {
                Class cls = value.getClass();
                Icon icon = (Icon)icons.get(cls);
                if (icon == null)  {
                    // Sets the icon for the component.
                    ContainerTreeModel model = (ContainerTreeModel)tree.getModel();
                    icon = model.getIcon(cls);
                    if (icon != null)  {
                        icons.put(cls, icon);
                        this.setIcon(icon);
                    }
                } else {
                    this.setIcon(icon);
                }

                this.setText(cls.getName());
            }
            return this;
        }
        
    }

    /**
     * A custom tree model which is based on the containment hiearchy.
     * A container is added as the root of the tree model. 
     * This model listens to the root container for Components added or removed.
     * <p>
     * This model class uses BeanInfo to retrieve custom attributes
     * and the Icon.
     */
    public class ContainerTreeModel extends TreeModelSupport implements ContainerListener  {
        private Container root;

        public ContainerTreeModel(Container root)  {
            this.root = root;
            this.root.addContainerListener(this);
        }

        public Object getChild(Object parent, int index)  {
            if (parent instanceof Container)  {
                Container c = (Container)parent;
                return c.getComponent(index);
            }
            return null;
        }

        public int getChildCount(Object parent)  {
            if (parent instanceof Container)  {
                Container c = (Container)parent;
                return c.getComponentCount();
            }
            return 0;
        }

        public int getIndexOfChild(Object parent, Object child)  {
            if (parent instanceof Container)  {
                Container c = (Container)parent;

                Component[] comps = c.getComponents();
                for (int i = 0; i < comps.length; i++) {
                    if (comps[i] == child)  {
                        return i;
                    }
                }
            }
            return -1;
        }

        public Object getRoot()  {
            return root;
        }

        /** 
         * Retrieves the BeanInfo icon for the class.
         */
        public Icon getIcon(Class cls)  {
            Icon icon = null;
            
            if (cls == null)
                return null;
               
            BeanInfo info = BeanInfoFactory.getBeanInfo(cls);
            
            if (info != null)  {
                Image image = info.getIcon(BeanInfo.ICON_COLOR_16x16);
                if (image != null)  {
                    icon = new ImageIcon(image);
                } else {
                    return getIcon(cls.getSuperclass());
                }
            }
            return icon;
        }

        /**
         * Determines if this component is a container by looking at the
         * "isContainer" BeanInfo attribute.
         */
        public boolean isContainer(Object node)  {
            BeanInfo info = BeanInfoFactory.getBeanInfo(node.getClass());

            if (info != null)  {
                BeanDescriptor desc = info.getBeanDescriptor();
                Boolean flag = (Boolean)desc.getValue("isContainer");
                if (flag != null)  {
                    return flag.booleanValue();
                }
            }

            // The isContainer attribute is not found.
            if (node instanceof Container)
                return true;
            else
                return false;
        }


        /**
         * Tests to see if the node is a leaf.
         */
        public boolean isLeaf(Object node)  {
            if (isContainer(node))  {
                Container container = (Container)node;
                if (container.getComponentCount() == 0)
                    return true;
                else
                    return false;
            }

            return true;
        }

        /**
         * Builds the parents of a component up to and including the root node.
         * @see javax.swing.tree.DefaultTreeModel#getPathToRoot
         */
        public Object[] getPathToRoot(Component comp)  {
            return getPathToRoot(comp, 0);
        }

        /**
         * Builds the parents of a component up to and including the root node.
         * @see javax.swing.tree.DefaultTreeModel#getPathToRoot
         */
        protected Object[] getPathToRoot(Component comp, int depth)  {
            Object[] retObjs;

            if (comp == null)  {
                if (depth == 0)
                    return null;
                else
                    retObjs = new Object[depth];
            } else {
                depth++;
                if (comp == getRoot())
                    retObjs = new Object[depth];
                else
                    retObjs = getPathToRoot(comp.getParent(), depth);
                retObjs[retObjs.length - depth] = comp;
            }
            return retObjs;
        }

        //
        // ContainerListener methods.
        //

        public void componentAdded(ContainerEvent evt)  {
            Container container = evt.getContainer();
            Component comp = evt.getChild();
            int[] indexes = new int[] { getIndexOfChild(container, comp) };

            // If this container is not the root then add the container event listener.
            if (container != getRoot())  {
                container.addContainerListener(this);
            }

            fireTreeNodesInserted(this, getPathToRoot(container), indexes, new Object[]{ comp });
        }

        public void componentRemoved(ContainerEvent evt)  {
            Container container = evt.getContainer();
            Component comp = evt.getChild();

            // Unregister the container listener this when container is removed.
            if (container != getRoot())  {
                container.removeContainerListener(this);
            }

            Object[] parentPath = getPathToRoot(evt.getContainer());

            fireTreeNodesRemoved(new TreeModelEvent(this, parentPath));
        }

        public void valueForPathChanged(TreePath path, Object newValue)  { }
    }
}
