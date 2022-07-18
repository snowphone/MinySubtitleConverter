/*
 * Decompiled with CFR 0.150.
 */
package com.myhyuny.window;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class ImageView
extends Container {
    private static final long serialVersionUID = -6264035147099624163L;
    private BufferedImage image;

    public ImageView() {
    }

    public ImageView(BufferedImage image) {
        this.setImage(image);
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        if (this.image == null) {
            return;
        }
        this.setSize(this.image.getWidth(), this.image.getHeight());
        g.drawImage(this.image, 0, 0, this.getWidth(), this.getHeight(), this);
    }
}

