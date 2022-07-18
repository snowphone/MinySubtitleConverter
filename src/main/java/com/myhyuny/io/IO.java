/*
 * Decompiled with CFR 0.150.
 */
package com.myhyuny.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.FileChannel;

public class IO {
    public static byte[] readAll(InputStream in) throws IOException {
        byte[] arrby;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            IO.copy(in, out);
            arrby = out.toByteArray();
        }
        catch (Throwable throwable) {
            IO.close(out);
            throw throwable;
        }
        IO.close(out);
        return arrby;
    }

    public static String readAll(Reader in) throws IOException {
        String string;
        StringWriter out = new StringWriter();
        try {
            IO.copy(in, out);
            string = out.toString();
        }
        catch (Throwable throwable) {
            IO.close(out);
            throw throwable;
        }
        IO.close(out);
        return string;
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        int len;
        if (in instanceof FileInputStream && out instanceof FileOutputStream) {
            FileChannel channel = ((FileInputStream)in).getChannel();
            channel.transferTo(0L, channel.size(), ((FileOutputStream)out).getChannel());
            out.flush();
            return;
        }
        if (!(in instanceof BufferedInputStream) && !(in instanceof ByteArrayInputStream)) {
            in = new BufferedInputStream(in);
        }
        if (!(out instanceof BufferedOutputStream) && !(out instanceof ByteArrayOutputStream)) {
            out = new BufferedOutputStream(out);
        }
        byte[] b = new byte[8192];
        while ((len = in.read(b)) != -1) {
            out.write(b, 0, len);
        }
        out.flush();
    }

    public static void copy(File file, File copy) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(file);
            out = new FileOutputStream(copy);
            IO.copy(in, out);
        }
        catch (Throwable throwable) {
            IO.close(in, out);
            throw throwable;
        }
        IO.close(in, out);
    }

    public static void copy(Reader in, Writer out) throws IOException {
        int len;
        if (!(in instanceof BufferedReader) && !(in instanceof StringReader)) {
            in = new BufferedReader(in);
        }
        if (!(out instanceof BufferedWriter) && !(out instanceof StringWriter)) {
            out = new BufferedWriter(out);
        }
        char[] cbuf = new char[8192];
        while ((len = in.read(cbuf)) != -1) {
            out.write(cbuf, 0, len);
        }
        out.flush();
    }

    public static void close(Closeable ... closeable) {
        Closeable[] arrcloseable = closeable;
        int n = closeable.length;
        for (int i = 0; i < n; ++i) {
            Closeable closable = arrcloseable[i];
            try {
                closable.close();
                continue;
            }
            catch (NullPointerException nullPointerException) {
                continue;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(Socket ... sockets) {
        Socket[] arrsocket = sockets;
        int n = sockets.length;
        for (int i = 0; i < n; ++i) {
            Socket socket = arrsocket[i];
            try {
                socket.close();
                continue;
            }
            catch (NullPointerException nullPointerException) {
                continue;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(ServerSocket ... sockets) {
        ServerSocket[] arrserverSocket = sockets;
        int n = sockets.length;
        for (int i = 0; i < n; ++i) {
            ServerSocket socket = arrserverSocket[i];
            try {
                socket.close();
                continue;
            }
            catch (NullPointerException nullPointerException) {
                continue;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

