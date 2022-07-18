/*
 * Decompiled with CFR 0.150.
 */
package com.myhyuny.application;

import com.myhyuny.application.information.Author;
import com.myhyuny.application.information.EMail;
import com.myhyuny.application.information.Homepage;
import com.myhyuny.application.information.Name;
import com.myhyuny.application.information.Twitter;
import com.myhyuny.application.information.Version;

public class AppInfo {
    private Object o;

    public AppInfo(Object o) {
        this.o = o;
    }

    public String getName() {
        Class<?> c = this.o.getClass();
        if (!c.isAnnotationPresent(Name.class)) {
            return null;
        }
        return c.getAnnotation(Name.class).value();
    }

    public String getAuthor() {
        Class<?> c = this.o.getClass();
        if (!c.isAnnotationPresent(Author.class)) {
            return null;
        }
        return c.getAnnotation(Author.class).value();
    }

    public String getVersion() {
        Class<?> c = this.o.getClass();
        if (!c.isAnnotationPresent(Version.class)) {
            return null;
        }
        return c.getAnnotation(Version.class).value();
    }

    public String getEMail() {
        Class<?> c = this.o.getClass();
        if (!c.isAnnotationPresent(EMail.class)) {
            return null;
        }
        return c.getAnnotation(EMail.class).value();
    }

    public String getHomepage() {
        Class<?> c = this.o.getClass();
        if (!c.isAnnotationPresent(Homepage.class)) {
            return null;
        }
        return c.getAnnotation(Homepage.class).value();
    }

    public String getTwitter() {
        Class<?> c = this.o.getClass();
        if (!c.isAnnotationPresent(Twitter.class)) {
            return null;
        }
        return c.getAnnotation(Twitter.class).value();
    }
}

