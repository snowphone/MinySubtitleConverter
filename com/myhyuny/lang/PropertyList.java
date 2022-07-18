/*
 * Decompiled with CFR 0.150.
 */
package com.myhyuny.lang;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PropertyList {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("Z"));
    }

    public static Map<String, Object> decodeMap(File file) throws SAXException, IOException, ParserConfigurationException {
        return (Map)new Reader(file).getDocument();
    }

    public static Map<String, Object> decodeMap(String uri) throws SAXException, IOException, ParserConfigurationException {
        return (Map)new Reader(uri).getDocument();
    }

    public static Map<String, Object> decodeMap(InputStream is) throws SAXException, IOException, ParserConfigurationException {
        return (Map)new Reader(is).getDocument();
    }

    public static List<Object> decodeList(File file) throws SAXException, IOException, ParserConfigurationException {
        return (List)new Reader(file).getDocument();
    }

    public static List<Object> decodeList(String uri) throws SAXException, IOException, ParserConfigurationException {
        return (List)new Reader(uri).getDocument();
    }

    public static List<Object> decodeList(InputStream is) throws SAXException, IOException, ParserConfigurationException {
        return (List)new Reader(is).getDocument();
    }

    private static void write(StreamResult result, Object object) {
        new Writer(object, result);
    }

    public static void write(OutputStream out, Object object) throws IOException {
        if (!(out instanceof BufferedOutputStream)) {
            out = new BufferedOutputStream(out);
        }
        PropertyList.write(new StreamResult(out), object);
        out.flush();
    }

    public static void write(java.io.Writer out, Object object) throws IOException {
        if (!(out instanceof BufferedWriter) && !(out instanceof StringWriter)) {
            out = new BufferedWriter(out);
        }
        PropertyList.write(new StreamResult(out), object);
        out.flush();
    }

    public static String encode(Object object) {
        StringWriter writer = new StringWriter();
        PropertyList.write(new StreamResult(writer), object);
        return writer.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Reader<E>
    extends DefaultHandler {
        private Object document;
        private String key;
        private StringBuilder value;
        private LinkedList<String> keyStack;
        private LinkedList<Object> valueStack;

        private E getDocument() {
            return (E)this.document;
        }

        private SAXParser parser() throws ParserConfigurationException, SAXException {
            return SAXParserFactory.newInstance().newSAXParser();
        }

        private Reader(File file) throws SAXException, IOException, ParserConfigurationException {
            this.parser().parse(file, (DefaultHandler)this);
        }

        private Reader(String uri) throws SAXException, IOException, ParserConfigurationException {
            this.parser().parse(uri, (DefaultHandler)this);
        }

        private Reader(InputStream is) throws SAXException, IOException, ParserConfigurationException {
            this.parser().parse(is, (DefaultHandler)this);
        }

        @Override
        public void startDocument() throws SAXException {
            this.key = "";
            this.keyStack = new LinkedList();
            this.valueStack = new LinkedList();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equals("dict")) {
                if (this.document != null) {
                    this.valueStack.add(this.document);
                }
                this.document = new HashMap();
                this.keyStack.add(this.key);
            } else if (qName.equals("array")) {
                if (this.document != null) {
                    this.valueStack.add(this.document);
                }
                this.document = new ArrayList();
                this.keyStack.add(this.key);
            }
            this.value = new StringBuilder();
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            this.value.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            try {
                Object data = null;
                if (qName.equals("key")) {
                    this.key = this.value.toString().trim();
                    return;
                }
                if (qName.equals("true")) {
                    data = Boolean.TRUE;
                } else if (qName.equals("false")) {
                    data = Boolean.FALSE;
                } else if (qName.equals("string")) {
                    data = this.value.toString().trim();
                } else if (qName.equals("integer")) {
                    Integer i = Integer.parseInt(this.value.toString());
                    Long l = Long.parseLong(this.value.toString());
                    data = i.intValue() == l.intValue() ? (long)i.intValue() : l;
                } else if (qName.equals("real")) {
                    Float f = Float.valueOf(Float.parseFloat(this.value.toString()));
                    Double d = Double.parseDouble(this.value.toString());
                    data = f.floatValue() == d.floatValue() ? (double)f.floatValue() : d;
                } else if (qName.equals("date")) {
                    data = dateFormat.parse(this.value.toString());
                } else if (qName.equals("dict") || qName.equals("array")) {
                    if (this.valueStack.size() < 1) {
                        return;
                    }
                    data = this.document;
                    this.document = this.valueStack.remove(this.valueStack.size() - 1);
                    this.key = this.keyStack.remove(this.keyStack.size() - 1);
                }
                if (data == null || data.toString().length() < 1) {
                    return;
                }
                if (this.document instanceof Map) {
                    ((Map)this.document).put(this.key, data);
                } else if (this.document instanceof List) {
                    ((List)this.document).add(data);
                }
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            e.printStackTrace();
        }
    }

    private static class Writer {
        private TransformerHandler handler;

        private Writer(Object object, StreamResult result) {
            try {
                SAXTransformerFactory factory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
                this.handler = factory.newTransformerHandler();
                this.handler.setResult(result);
                this.handler.startDocument();
                this.handler.startElement(null, null, "plist", null);
                this.parseValue(object);
                this.handler.endElement(null, null, "plist");
                this.handler.endDocument();
                this.handler.setResult(result);
            }
            catch (TransformerConfigurationException e) {
                e.printStackTrace();
            }
            catch (SAXException e) {
                e.printStackTrace();
            }
        }

        public void setKeyElement(String key) throws SAXException {
            this.handler.startElement(null, null, "key", null);
            this.handler.characters(key.toCharArray(), 0, key.length());
            this.handler.endElement(null, null, "key");
        }

        public void parseValue(Object object) {
            if (object == null) {
                return;
            }
            String qName = null;
            qName = object instanceof String ? "string" : (object instanceof Number ? (object instanceof Float || object instanceof Double ? "real" : "integer") : (object instanceof Date ? "date" : (object instanceof Boolean ? ((Boolean)object != false ? "true" : "false") : (object instanceof List ? "array" : "string"))));
            try {
                this.handler.startElement(null, null, qName, null);
                if (object instanceof List) {
                    for (Object item : (List)object) {
                        this.parseValue(item);
                    }
                } else if (object instanceof Map) {
                    for (Map.Entry entry : ((Map)object).entrySet()) {
                        Object value = entry.getValue();
                        if (value == null) continue;
                        String key = (String)entry.getKey();
                        this.setKeyElement(key);
                        this.parseValue(value);
                    }
                } else if (object instanceof Date) {
                    String value = dateFormat.format(object);
                    this.handler.characters(value.toCharArray(), 0, value.length());
                } else if (!(object instanceof Boolean)) {
                    String value = object.toString();
                    this.handler.characters(value.toCharArray(), 0, value.length());
                }
                this.handler.endElement(null, null, qName);
            }
            catch (SAXException e) {
                e.printStackTrace();
            }
        }
    }
}

