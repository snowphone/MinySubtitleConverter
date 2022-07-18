/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  com.apple.eawt.AboutHandler
 *  com.apple.eawt.AppEvent$AboutEvent
 *  com.apple.eawt.AppEvent$AppForegroundEvent
 *  com.apple.eawt.AppEvent$AppReOpenedEvent
 *  com.apple.eawt.AppEvent$OpenFilesEvent
 *  com.apple.eawt.AppEventListener
 *  com.apple.eawt.AppForegroundListener
 *  com.apple.eawt.AppReOpenedListener
 *  com.apple.eawt.Application
 *  com.apple.eawt.OpenFilesHandler
 */
package com.myhyuny.MinySubtitleConverter;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent;
import com.apple.eawt.AppEventListener;
import com.apple.eawt.AppForegroundListener;
import com.apple.eawt.AppReOpenedListener;
import com.apple.eawt.Application;
import com.apple.eawt.OpenFilesHandler;
import com.myhyuny.MinySubtitleConverter.MainFrame;

public class AppleApplication
extends com.myhyuny.application.AppleApplication
implements OpenFilesHandler,
AppForegroundListener,
AppReOpenedListener,
AboutHandler {
    private MainFrame frame;

    public AppleApplication(Object owner) {
        super(owner);
        this.frame = (MainFrame)owner;
        this.getApplication().setOpenFileHandler((OpenFilesHandler)this);
        this.getApplication().addAppEventListener((AppEventListener)this);
        this.getApplication().setAboutHandler((AboutHandler)this);
    }

    public Application getApplication() {
        return (Application)super.getApplication();
    }

    public void openFiles(AppEvent.OpenFilesEvent e) {
        this.frame.setVisible(true);
        this.frame.convert(e.getFiles(), 0);
    }

    public void appReOpened(AppEvent.AppReOpenedEvent e) {
        this.frame.setVisible(true);
    }

    public void handleAbout(AppEvent.AboutEvent e) {
        this.frame.showAbout();
    }

    public void appMovedToBackground(AppEvent.AppForegroundEvent e) {
        if (!this.frame.isVisible()) {
            this.frame.exit();
        }
    }

    public void appRaisedToForeground(AppEvent.AppForegroundEvent e) {
    }
}

