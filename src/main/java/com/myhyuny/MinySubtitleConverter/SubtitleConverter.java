/*
 * Decompiled with CFR 0.150.
 */
package com.myhyuny.MinySubtitleConverter;

import com.myhyuny.io.IO;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubtitleConverter
extends Thread {
    private static final Pattern patternExtention = Pattern.compile("[^\\.]+$");
    private static final Pattern patternSubRip = Pattern.compile("\\s{2,}\\d+\\s+");
    private static final Pattern patternSubRipData = Pattern.compile("(\\d{2}:\\d{2}:\\d{2},\\d{1,3})\\s+-->\\s+(\\d{2}:\\d{2}:\\d{2},\\d{1,3})\\s+(.+)", 32);
    private static final Pattern patternSAMI = Pattern.compile("\\s*<sync\\s+", 2);
    private static final Pattern patternSAMIData = Pattern.compile("start=['\"]?(\\d+)['\"]?\\s*[^>]*>\\s*(.*)\\s*", 34);
    private static final Pattern patternSAMINewLineTag = Pattern.compile("<br[^>]*/?>", 2);
    private static final Pattern patternSAMITag = Pattern.compile("</?\\w+\\s*[^>]*\\s*/?>");
    private static final Pattern patternNewLine = Pattern.compile("\\n");
    private static final Pattern patternLeftTrim = Pattern.compile("\\n\\s+");
    private static final Pattern patternRightTrim = Pattern.compile("\\s+\\n");
    private static final Pattern patternSpace = Pattern.compile("[\u3000  ]+");
    private static final Pattern patternComments = Pattern.compile("<!--.*?-->", 32);
    private static final Charset defaultCharset;
    public static final Pattern PATTERN_FILE_EXTENTION;
    private static final String FILE_EXTENTION_SAMI = "smi";
    private static final String FILE_EXTENTION_SUBRIP = "srt";
    public static final int FILE_TYPE_SAMI = 1;
    public static final int FILE_TYPE_SUBRIP = 2;
    public static final String LINE_DELIMITER_UNIX = "\n";
    public static final String LINE_DELIMITER_WINDOWS = "\r\n";
    private final SimpleDateFormat formatSubRip = new SimpleDateFormat("HH:mm:ss,SSS");
    private ArrayDeque<Subtitle> subtitleList;
    private File inputFile;
    private String inputType;
    private Charset inputCharset = null;
    private Charset outputCharset = StandardCharsets.UTF_8;
    private String lineDelimiter;
    private int outputType = 0;
    private long sync;

    static {
        PATTERN_FILE_EXTENTION = Pattern.compile("\\.(sa?mi|srt)$", 2);
        String language = System.getProperty("user.language");
        String id = TimeZone.getDefault().getID();
        defaultCharset = language.equals("ko") || id.equals("Asia/Seoul") ? Charset.forName("x-windows-949") : Charset.forName(System.getProperty("sun.jnu.encoding"));
    }

    public SubtitleConverter() {
        this.formatSubRip.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public SubtitleConverter(File input) {
        this();
        this.inputFile = input;
    }

    public SubtitleConverter(File input, Charset inputCharset) {
        this(input);
        this.inputCharset = inputCharset;
    }

    public SubtitleConverter(File input, Charset inputCharset, int outputType) {
        this(input, inputCharset);
        this.outputType = outputType;
    }

    public void setInputCharset(Charset inputCharset) {
        this.inputCharset = inputCharset;
    }

    public Charset getInputCharset() {
        return this.inputCharset;
    }

    public void setOutputCharset(Charset outputCharset) {
        this.outputCharset = outputCharset;
    }

    public Charset getOutputCharset() {
        return this.outputCharset;
    }

    public void setLineDelimiter(String lineDelimiter) {
        this.lineDelimiter = lineDelimiter;
    }

    public String getLineDelimiter() {
        return this.lineDelimiter;
    }

    public void setSync(long sync) {
        this.sync = sync;
    }

    public long getSync() {
        return this.sync;
    }

    public void fileOpen(File file) {
        String inputSubtitle;
        block26: {
            inputSubtitle = null;
            if (this.inputCharset == null) {
                BufferedInputStream in = null;
                try {
                    try {
                        in = new BufferedInputStream(new FileInputStream(file));
                        byte[] bytes = IO.readAll(in);
                        IO.close(in);
                        if (bytes[0] == 0 && bytes[1] == 0 && bytes[2] == -2 && bytes[3] == -1) {
                            this.setInputCharset(Charset.forName("UTF-32BE"));
                        } else if (bytes[0] == -17 && bytes[1] == -69 && bytes[2] == -65) {
                            this.setInputCharset(StandardCharsets.UTF_8);
                        } else if (bytes[0] == -2 && bytes[1] == -1) {
                            this.setInputCharset(StandardCharsets.UTF_16BE);
                        } else if (bytes[0] == -1 && bytes[1] == -2) {
                            if (bytes[2] == 0 && bytes[3] == 0) {
                                this.setInputCharset(Charset.forName("UTF-32LE"));
                            } else {
                                this.setInputCharset(StandardCharsets.UTF_16LE);
                            }
                        } else if (defaultCharset.equals(Charset.defaultCharset())) {
                            this.setInputCharset(defaultCharset);
                        } else {
                            String jnu = new String(bytes, defaultCharset);
                            String def = new String(bytes, Charset.defaultCharset());
                            if (jnu.length() < def.length()) {
                                inputSubtitle = jnu;
                                this.setInputCharset(defaultCharset);
                            } else {
                                inputSubtitle = def;
                                this.setInputCharset(Charset.defaultCharset());
                            }
                        }
                        if (inputSubtitle == null) {
                            inputSubtitle = new String(bytes, this.getInputCharset());
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        IO.close(in);
                        break block26;
                    }
                }
                catch (Throwable throwable) {
                    IO.close(in);
                    throw throwable;
                }
                IO.close(in);
            } else {
                BufferedReader reader = null;
                try {
                    try {
                        reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), this.inputCharset));
                        inputSubtitle = IO.readAll(reader);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        IO.close(reader);
                        break block26;
                    }
                }
                catch (Throwable throwable) {
                    IO.close(reader);
                    throw throwable;
                }
                IO.close(reader);
            }
        }
        this.loading(file, inputSubtitle.replaceAll(LINE_DELIMITER_WINDOWS, LINE_DELIMITER_UNIX));
    }

    private void loading(File file, String subtitle) {
        this.inputFile = file;
        Matcher matcher = patternExtention.matcher(file.getName());
        matcher.find();
        this.inputType = matcher.group(0).toLowerCase();
        if (this.inputType.equals(FILE_EXTENTION_SAMI)) {
            if (!this.loadingSAMI(subtitle)) {
                this.loadingAuto(subtitle);
            }
        } else if (this.inputType.equals(FILE_EXTENTION_SUBRIP) && !this.loadingSubRip(subtitle)) {
            this.loadingAuto(subtitle);
        }
    }

    private void loadingAuto(String text) {
        if (this.loadingSAMI(text)) {
            return;
        }
        if (this.loadingSubRip(text)) {
            return;
        }
        System.out.println("?");
    }

    private boolean loadingSAMI(String sami) {
        String[] split = patternSAMI.split(sami = patternComments.matcher(sami).replaceAll(""));
        if (split.length < 2) {
            return false;
        }
        this.inputType = FILE_EXTENTION_SAMI;
        long end = 0L;
        String text = "";
        this.subtitleList = new ArrayDeque();
        Subtitle subtitle = new Subtitle(1);
        String[] arrstring = split;
        int n = split.length;
        for (int i = 0; i < n; ++i) {
            String sync = arrstring[i];
            Matcher matcher = patternSAMIData.matcher(sync);
            if (!matcher.find()) continue;
            long start = end;
            end = Long.valueOf(matcher.group(1));
            if (text.length() > 0) {
                if (subtitle.text != null && subtitle.text.equals(text)) {
                    subtitle.end = end;
                } else {
                    if (subtitle.end > start) {
                        subtitle.end = end;
                    }
                    subtitle = new Subtitle(1, start, end, text, this.sync);
                    this.subtitleList.add(subtitle);
                }
            }
            text = matcher.group(2).trim();
        }
        return true;
    }

    private boolean loadingSubRip(String srt) {
        String[] split = patternSubRip.split(srt);
        if (split.length < 2) {
            return false;
        }
        this.inputType = FILE_EXTENTION_SUBRIP;
        this.subtitleList = new ArrayDeque();
        String[] arrstring = split;
        int n = split.length;
        for (int i = 0; i < n; ++i) {
            String str = arrstring[i];
            Matcher matcher = patternSubRipData.matcher(str);
            if (!matcher.find()) continue;
            String text = matcher.group(3);
            try {
                Matcher m = patternSpace.matcher(text);
                if (m.find()) {
                    text = m.replaceAll(" ");
                }
                if ((m = patternLeftTrim.matcher(text)).find()) {
                    text = m.replaceAll(LINE_DELIMITER_UNIX);
                }
                if ((m = patternRightTrim.matcher(text)).find()) {
                    text = m.replaceAll(LINE_DELIMITER_UNIX);
                }
                this.subtitleList.add(new Subtitle(2, this.formatSubRip.parse(matcher.group(1)), this.formatSubRip.parse(matcher.group(2)), text.trim(), this.sync));
                continue;
            }
            catch (ParseException e) {
                System.err.printf("File name: %s, line: %d, text: %s\n", this.inputFile.getName(), this.subtitleList.size() + 1, text);
                e.printStackTrace();
            }
        }
        return true;
    }

    private void writeFile(File file, String subtitle) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), this.outputCharset));
            writer.write(subtitle);
        }
        catch (Throwable throwable) {
            IO.close(writer);
            throw throwable;
        }
        IO.close(writer);
    }

    public File writeSAMI() throws IOException {
        String p;
        if (this.inputType.equals(FILE_EXTENTION_SAMI) && this.sync == 0L) {
            return null;
        }
        if (this.lineDelimiter == null) {
            this.lineDelimiter = LINE_DELIMITER_WINDOWS;
        }
        StringBuilder builder = new StringBuilder("<sami>\n<head>\n<title></title>\n<style><!--\np { font-family: sans-serif; text-align: center; }\n");
        if (this.inputCharset.equals(Charset.forName("x-windows-949")) || this.inputCharset.equals(Charset.forName("EUC-KR"))) {
            builder.append(".KRCC { Name: Korean; lang: ko-KR; }\n");
            p = "<p class=KRCC>";
        } else {
            p = "<p>";
        }
        builder.append("--></style>\n</head>\n<body>\n");
        long end = 0L;
        for (Subtitle item : this.subtitleList) {
            long e = item.getEnd();
            if (e < 0L) continue;
            long start = item.getStart();
            if (start != end && end != 0L) {
                builder.append(String.format("<sync start=%d>\n", end));
            }
            builder.append(String.format("<sync start=%d>\n%s%s\n", start, p, item.getSAMI()));
            end = e;
        }
        builder.append(String.format("<sync start=%d>\n</body>\n</sami>", end));
        String text = builder.toString().trim();
        if (this.lineDelimiter.equals(LINE_DELIMITER_WINDOWS)) {
            text = text.replace(LINE_DELIMITER_UNIX, LINE_DELIMITER_WINDOWS);
        }
        String name = patternExtention.split(this.inputFile.getName(), 0)[0];
        File file = new File(this.inputFile.getParent(), name + FILE_EXTENTION_SAMI);
        this.writeFile(file, text);
        return file;
    }

    public File writeSubRip() throws IOException {
        if (this.inputType.equals(FILE_EXTENTION_SUBRIP) && this.sync == 0L) {
            return null;
        }
        if (this.lineDelimiter == null) {
            this.lineDelimiter = LINE_DELIMITER_WINDOWS;
        }
        int i = 0;
        StringBuilder builder = new StringBuilder();
        for (Subtitle item : this.subtitleList) {
            String text;
            long end = item.getEnd();
            if (end < 0L || (text = item.getPlain()).length() < 1) continue;
            builder.append(String.format("%d\n%s --> %s\n%s\n\n", ++i, this.formatSubRip.format(new Date(item.getStart())), this.formatSubRip.format(new Date(end)), text));
        }
        String text = builder.toString().trim();
        if (this.lineDelimiter.equals(LINE_DELIMITER_WINDOWS)) {
            text = text.replace(LINE_DELIMITER_UNIX, LINE_DELIMITER_WINDOWS);
        }
        String name = patternExtention.split(this.inputFile.getName(), 0)[0];
        File file = new File(this.inputFile.getParent(), name + FILE_EXTENTION_SUBRIP);
        this.writeFile(file, text);
        return file;
    }

    public void write(int outputType) {
        File file = null;
        try {
            switch (outputType) {
                case 1: {
                    file = this.writeSAMI();
                    break;
                }
                case 2: {
                    file = this.writeSubRip();
                    break;
                }
                default: {
                    if (this.inputType.equals(FILE_EXTENTION_SAMI)) {
                        file = this.writeSubRip();
                        break;
                    }
                    if (!this.inputType.equals(FILE_EXTENTION_SUBRIP)) break;
                    file = this.writeSAMI();
                }
            }
            if (file != null) {
                System.out.printf("%s (%s) -> %s (%s)\n", this.inputFile.getName(), this.inputCharset.name(), file.getName(), this.outputCharset.name());
            } else {
                System.err.printf("Unsupported Type: %s\n", this.inputFile.getName());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (this.subtitleList == null) {
            this.fileOpen(this.inputFile);
        }
        this.write(this.outputType);
    }

    private class Subtitle {
        private static final int TYPE_SAMI = 1;
        private static final int TYPE_SUBRIP = 2;
        private int type = 0;
        private long start = 0L;
        private long end = 0L;
        private String text = null;
        private long sync = 0L;

        private Subtitle(int type) {
            this.type = type;
        }

        private Subtitle(int type, long sync) {
            this(type);
            this.sync = sync;
        }

        private Subtitle(int type, long start, long end, String text, long sync) {
            this(type, sync);
            this.start = start;
            this.end = end;
            this.text = text;
        }

        private Subtitle(int type, Date start, Date end, String text, long sync) {
            this(type, start.getTime(), end.getTime(), text, sync);
        }

        private long getStart() {
            long l = this.start + this.sync;
            if (l < 0L) {
                l = 0L;
            }
            return l;
        }

        private long getEnd() {
            long l = this.end + this.sync;
            if (l < 0L) {
                l = 0L;
            }
            return l;
        }

        private String getSAMI() {
            switch (this.type) {
                case 1: {
                    return this.text;
                }
            }
            String sami = this.text;
            Matcher matcher = patternNewLine.matcher(sami);
            if (matcher.find()) {
                sami = matcher.replaceAll("<br>\n");
            }
            return sami;
        }

        private String getPlain() {
            switch (this.type) {
                case 1: {
                    String str = this.text.replaceAll("&nbsp;", " ");
                    Matcher matcher = patternSAMINewLineTag.matcher(str);
                    if (matcher.find()) {
                        str = matcher.replaceAll(SubtitleConverter.LINE_DELIMITER_UNIX);
                    }
                    if ((matcher = patternSAMITag.matcher(str)).find()) {
                        str = matcher.replaceAll("");
                    }
                    if ((matcher = patternSpace.matcher(str)).find()) {
                        str = matcher.replaceAll(" ");
                    }
                    if ((matcher = patternLeftTrim.matcher(str)).find()) {
                        str = matcher.replaceAll(SubtitleConverter.LINE_DELIMITER_UNIX);
                    }
                    if ((matcher = patternRightTrim.matcher(str)).find()) {
                        str = matcher.replaceAll(SubtitleConverter.LINE_DELIMITER_UNIX);
                    }
                    return str.trim();
                }
            }
            return this.text;
        }
    }
}

