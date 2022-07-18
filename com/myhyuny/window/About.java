/*
 * Decompiled with CFR 0.150.
 */
package com.myhyuny.window;

import com.myhyuny.application.AppInfo;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.PrintWriter;
import java.io.StringWriter;

public class About
extends Dialog
implements WindowListener,
ActionListener {
    private static final long serialVersionUID = -173859703961811969L;

    public About(Frame owner) {
        super(owner);
        AppInfo info = new AppInfo(owner);
        this.setTitle(String.format("About %s", info.getName() != null ? info.getName() : owner.getTitle()));
        this.setLayout(new BorderLayout());
        this.add((Component)new Panel(), "North");
        this.add((Component)new Panel(), "East");
        this.add((Component)new Panel(), "West");
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        out.println(this.getTitle());
        if (info.getVersion() != null) {
            out.printf("\nVersion: %s", info.getVersion());
        }
        if (info.getEMail() != null) {
            out.printf("\nE-Mail: %s", info.getEMail());
        }
        if (info.getHomepage() != null) {
            out.printf("\nHomepage: %s", info.getHomepage());
        }
        if (info.getTwitter() != null) {
            out.printf("\nTwitter: @%s", info.getTwitter());
        }
        TextArea text = new TextArea(writer.toString(), 0, 0, 3);
        text.setEditable(false);
        text.setFocusable(false);
        this.add((Component)text, "Center");
        out.close();
        Panel panel = new Panel(new FlowLayout(2));
        Button button = new Button("OK");
        button.addActionListener(this);
        panel.add(button);
        this.add((Component)panel, "South");
        this.setModal(true);
        this.setSize(320, 180);
        this.setResizable(false);
        this.addWindowListener(this);
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            Rectangle rectangle = this.getOwner().getBounds();
            this.setBounds(rectangle.x + rectangle.width / 2 - this.getWidth() / 2, rectangle.y + rectangle.height / 2 - this.getHeight() / 2, this.getWidth(), this.getHeight());
        }
        super.setVisible(b);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.setVisible(false);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.setVisible(false);
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }
}

