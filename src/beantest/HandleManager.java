/*
 * %W% %E%
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

import java.awt.Rectangle;

/**
 * Interface for manipulating a set of handles.
 *
 * @version %I% %G%
 * @author  Mark Davidson
 */
public interface HandleManager {
    
    /**
     * Repositions all handles in the manager to the edges of the 
     * bounding rectangle. Called when the bounding rectangle changes
     * @param rect The bounding rectangle
     */
    public void repositionHandles(Rectangle rect);

}