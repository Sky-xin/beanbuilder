/*
 * @(#)InteractionHandler.java	1.4 99/10/27
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;

/**
 * A class that acts as a proxy which binds the source method to a target method invocation.
 * This is added dynamically at runtime.
 *
 * @version 1.4 10/27/99
 * @author  Mark Davidson
 */
public class InteractionHandler implements InvocationHandler  {
    
    private Method sourceMethod;
    private Method targetMethod;
    private Object source;
    private Object target;
    private Object[] targetArgs;
    
    public InteractionHandler()  {
    }
    
    public InteractionHandler(Object source, Method sourceMethod, Method targetMethod, 
                       Object target, Object[] targetArgs)  {
        this.sourceMethod = sourceMethod;
        this.targetMethod = targetMethod;
        this.target = target;
        this.targetArgs = targetArgs;
    }

    /** 
     * Method invocation.
     */
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

        Object result = null;
        Object[] callArgs = targetArgs;
        
        String methodName = method.getName();
        if (method.getDeclaringClass() == Object.class)  {
            // Handle the Object public methods.
            if (methodName.equals("hashCode"))  {
                return proxyHashCode(proxy);   
            } else if (methodName.equals("equals")) {
                return proxyEquals(proxy, args[0]);
            } else if (methodName.equals("toString")) {
                return proxyToString(proxy);
            }
        }
        
        // The rest of this method invokes the Dynamically generated EventAdapter
        // using the fields that were set when the interaction was created.
        
        // Look for a method in first argument in the target args. This method is
        // assumed to be a no arg getter method on the source object to retrieve
        // the argument for the target object method.
        // i.e., allow for the method call: target.setText(source.getText())
        if (targetArgs != null && targetArgs[0] != null 
                               && targetArgs[0] instanceof Method)  {
            Method sourceGetter = (Method)targetArgs[0];
            
            Object newArg = null;
            try {
                newArg = sourceGetter.invoke(source, new Object[]{});
                callArgs = new Object[] { newArg };
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
                throw ex.getTargetException();
            } catch (Exception ex2) {
                ex2.printStackTrace();
                throw new RuntimeException("unexpected invocation exception: " + ex2.getMessage());
            }
        }
        

        if (methodName.equals(sourceMethod.getName()))  {
            // This is the method handler that we are interested in.
            try {
                result = targetMethod.invoke(target, callArgs);
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
                throw ex.getTargetException();
            } catch (Exception ex2) {
                ex2.printStackTrace();
                throw new RuntimeException("unexpected invocation exception: " + ex2.getMessage());
            }
        }
        
        return result;
    }
    
    public void setSource(Object source)  {
        this.source = source;
    }
    
    public void setSourceMethod(Method source)  {
        this.sourceMethod = source;
    }
    
    public void setTargetMethod(Method target)  {
        this.targetMethod = target;
    }
    
    public void setTarget(Object target)  {
        this.target = target;
    }
    
    public void setTargetArgs(Object args[])  {
        this.targetArgs = args;
    }
    
    public Method getSourceMethod()  {
        return sourceMethod;
    }
    
    public Method getTargetMethod()  {
        return targetMethod;
    }
    
    public Object getTarget()  {
        return target;
    }
    
    public Object getSource()  {
        return source;
    }
    
    public Object[] getTargetArgs()  {
        return targetArgs;
    }
    
    
    protected Integer proxyHashCode(Object proxy)  {
        return new Integer(System.identityHashCode(proxy));
    }
    
    protected Boolean proxyEquals(Object proxy, Object other)  {
        return (proxy == other ? Boolean.TRUE : Boolean.FALSE);
    }
    
    protected String proxyToString(Object proxy)  {
        return proxy.getClass().getName() + '@' + Integer.toHexString(proxy.hashCode());
    }
    
} // end class InteractionHandler

