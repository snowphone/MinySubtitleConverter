/*
 * Decompiled with CFR 0.150.
 */
package com.myhyuny.application;

import java.awt.Window;
import java.lang.reflect.InvocationTargetException;

public abstract class AppleApplication {
    private static Boolean SUPPORT = null;
    private static AppleApplication INSTANCE = null;
    private Object application;

    public AppleApplication(Object owner) {
        try {
            Class<?> c = Class.forName("com.apple.eawt.Application");
            this.application = c.getMethod("getApplication", new Class[0]).invoke(c, new Object[0]);
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (NoSuchMethodException noSuchMethodException) {
        }
        catch (IllegalAccessException illegalAccessException) {
        }
        catch (InvocationTargetException invocationTargetException) {
            // empty catch block
        }
    }

    public Object getApplication() {
        return this.application;
    }

    public static AppleApplication getApplication(String name, Object owner) {
        try {
            if (INSTANCE == null && AppleApplication.isSupport()) {
                INSTANCE = (AppleApplication)Class.forName(name).getConstructor(Object.class).newInstance(owner);
            }
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return INSTANCE;
    }

    public static boolean isSupport() {
        if (SUPPORT == null) {
            try {
                SUPPORT = System.getProperty("os.name").indexOf("OS X") != -1 && Class.forName("com.apple.eawt.Application") != null;
            }
            catch (ClassNotFoundException e) {
                SUPPORT = false;
            }
        }
        return SUPPORT;
    }

    public static void setWindowCanFullScreen(Window w, boolean b) {
        try {
            Class<?> c = Class.forName("com.apple.eawt.FullScreenUtilities");
            c.getMethod("setWindowCanFullScreen", Window.class, Boolean.TYPE).invoke(c, w, b);
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (NoSuchMethodException noSuchMethodException) {
        }
        catch (IllegalAccessException illegalAccessException) {
        }
        catch (InvocationTargetException invocationTargetException) {
            // empty catch block
        }
    }

    public void requestToggleFullScreen(Window w) {
        try {
            Class<?> c = this.application.getClass();
            c.getMethod("requestToggleFullScreen", Window.class).invoke(this.application, w);
        }
        catch (NoSuchMethodException noSuchMethodException) {
        }
        catch (IllegalAccessException illegalAccessException) {
        }
        catch (InvocationTargetException invocationTargetException) {
            // empty catch block
        }
    }
}

