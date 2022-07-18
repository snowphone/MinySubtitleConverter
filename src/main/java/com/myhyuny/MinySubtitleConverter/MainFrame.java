/*
 * Decompiled with CFR 0.150.
 */
package com.myhyuny.MinySubtitleConverter;

import com.myhyuny.MinySubtitleConverter.SubtitleConverter;
import com.myhyuny.application.AppleApplication;
import com.myhyuny.application.information.Author;
import com.myhyuny.application.information.EMail;
import com.myhyuny.application.information.Homepage;
import com.myhyuny.application.information.Twitter;
import com.myhyuny.application.information.Version;
import com.myhyuny.window.About;
import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Panel;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

@Version(value="0.6.0")
@Author(value="Hyunmin Kang")
@EMail(value="myhyuny@live.com")
@Homepage(value="http://blog.myhyuny.com/")
@Twitter(value="MyHyuny")
public class MainFrame
extends Frame
implements WindowListener,
ActionListener,
FocusListener,
DropTargetListener,
FilenameFilter {
    private static final long serialVersionUID = -4911202395284319506L;
    private static final String charsetAuto = "Auto (Unicode Or System Default)";
    private static final String outputTypeSAMI = "SAMI (smi)";
    private static final String outputTypeSubRip = "SubRip (srt)";
    private static int LABLE_ALIGNMENT = AppleApplication.isSupport() ? 4 : 2;
    private static final int menuLine = 5;
    private static final Pattern syncPattern = Pattern.compile("-?\\d+\\.?\\d*");
    private Preferences preferences = Preferences.userNodeForPackage(this.getClass());
    private Charset outputCharset = Charset.forName("UTF-8");
    private Charset inputCharset = null;
    private String lineDelimiter = null;
    private int outputType = 0;
    private long sync = 0L;
    private About about = new About(this);
    private MenuItem menuItemOpenFile = new MenuItem("Open File...", new MenuShortcut(79));
    private MenuItem menuItemClose = new MenuItem("Close", new MenuShortcut(87));
    private MenuItem menuItemExit = new MenuItem("Exit");
    private MenuItem menuItemAbout = new MenuItem("About MinySubtitleConverter");
    private JLabel outputTypeLabel = new JLabel("Output Type", LABLE_ALIGNMENT);
    private JLabel inputCharsetLabel = new JLabel("Input Charset", LABLE_ALIGNMENT);
    private JLabel outputCharsetLabel = new JLabel("Output Charset", LABLE_ALIGNMENT);
    private JLabel lineDelimiterLabel = new JLabel("Line Delimiter", LABLE_ALIGNMENT);
    private JLabel syncLabel = new JLabel("Sync", LABLE_ALIGNMENT);
    private Choice outTypeChoice = new Choice();
    private Choice inputCharsetChoice = new Choice();
    private Choice outputCharsetChoice = new Choice();
    private Choice lineDelimiterChoice = new Choice();
    private JTextField syncTextField = new JTextField("0.0 sec");
    private JProgressBar progressBar = new JProgressBar();
    private JLabel statusLabel = new JLabel();
    private DropTarget dropTarget = new DropTarget(this, 0x40000000, this);
    private AppleApplication app;
    private boolean run = false;
    private final Runnable startConvert = new Runnable(){

        @Override
        public void run() {
            MainFrame.this.setEnabled(false);
            MainFrame.this.progressBar.setValue(0);
            MainFrame.this.statusLabel.setText("Converting");
        }
    };
    private final Runnable endConvert = new Runnable(){

        @Override
        public void run() {
            MainFrame.this.progressBar.setValue(0);
            MainFrame.this.statusLabel.setText("Complete");
            MainFrame.this.setEnabled(true);
            MainFrame.this.run = false;
        }
    };
    private final Runnable updateProgressBar = new Runnable(){

        @Override
        public void run() {
            MainFrame.this.progressBar.setValue(MainFrame.this.progressBar.getValue() + 1);
        }
    };

    public MainFrame() {
        this.setTitle("MinySubtitleConverter");
        this.setSize(320, 200);
        this.setResizable(false);
        MenuBar mb = new MenuBar();
        Menu m = new Menu("File");
        mb.add(m);
        m.add(this.menuItemOpenFile);
        this.menuItemOpenFile.addActionListener(this);
        m.addSeparator();
        m.add(this.menuItemClose);
        this.menuItemClose.addActionListener(this);
        m.addSeparator();
        m.add(this.menuItemExit);
        this.menuItemExit.addActionListener(this);
        if (!AppleApplication.isSupport()) {
            m = new Menu("Help");
            mb.add(m);
            m.add(this.menuItemAbout);
            this.menuItemAbout.addActionListener(this);
        }
        this.setMenuBar(mb);
        this.setLayout(new BorderLayout());
        Panel borderPanel = new Panel(new BorderLayout(10, 0));
        this.add((Component)new Panel(), "North");
        this.add((Component)new Panel(), "East");
        this.add((Component)new Panel(), "West");
        Panel panel = new Panel(new GridLayout(5, 1));
        panel.add(this.outputTypeLabel);
        panel.add(this.inputCharsetLabel);
        panel.add(this.outputCharsetLabel);
        panel.add(this.lineDelimiterLabel);
        panel.add(this.syncLabel);
        borderPanel.add((Component)panel, "West");
        panel = new Panel(new GridLayout(5, 1));
        panel.add(this.outTypeChoice);
        this.outTypeChoice.add(outputTypeSAMI);
        this.outTypeChoice.add(outputTypeSubRip);
        panel.add(this.inputCharsetChoice);
        panel.add(this.outputCharsetChoice);
        this.inputCharsetChoice.add(charsetAuto);
        for (Map.Entry<String, Charset> entry : Charset.availableCharsets().entrySet()) {
            this.inputCharsetChoice.add(entry.getKey());
            this.outputCharsetChoice.add(entry.getKey());
        }
        this.outputCharsetChoice.select("UTF-8");
        panel.add(this.lineDelimiterChoice);
        this.lineDelimiterChoice.add("Default");
        this.lineDelimiterChoice.add("Uinx");
        this.lineDelimiterChoice.add("Windows");
        panel.add(this.syncTextField);
        borderPanel.add((Component)panel, "Center");
        this.add((Component)borderPanel, "Center");
        panel = new Panel(new BorderLayout(10, 0));
        panel.add((Component)this.progressBar, "West");
        panel.add((Component)this.statusLabel, "Center");
        this.add((Component)panel, "South");
        this.syncTextField.addFocusListener(this);
        this.addWindowListener(this);
        this.outTypeChoice.select(this.preferences.get("OutputType", outputTypeSubRip));
        this.inputCharsetChoice.select(this.preferences.get("InputCharset", charsetAuto));
        this.outputCharsetChoice.select(this.preferences.get("OutputCharset", outputTypeSubRip));
        this.lineDelimiterChoice.select(this.preferences.get("LineDelimiter", "Default"));
        Dimension dimension = this.getToolkit().getScreenSize();
        int x = this.preferences.getInt("WindowBoundsX", Math.round((float)dimension.width / 2.0f - (float)this.getWidth() / 2.0f));
        int y = this.preferences.getInt("WindowBoundsY", Math.round((float)dimension.height / 2.0f - (float)this.getHeight() / 2.0f));
        this.setBounds(x, y, this.getWidth(), this.getHeight());
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        this.menuItemOpenFile.setEnabled(b);
        this.outputTypeLabel.setEnabled(b);
        this.inputCharsetLabel.setEnabled(b);
        this.outputCharsetLabel.setEnabled(b);
        this.lineDelimiterLabel.setEnabled(b);
        this.outTypeChoice.setEnabled(b);
        this.inputCharsetChoice.setEnabled(b);
        this.outputCharsetChoice.setEnabled(b);
        this.lineDelimiterChoice.setEnabled(b);
    }

    private Charset charset(String charsetName) {
        if (charsetName.equals(charsetAuto)) {
            return null;
        }
        return Charset.forName(charsetName);
    }

    private String lineDelimiterType(String name) {
        if (name.equals(name.equals("Unix"))) {
            return "\n";
        }
        if (name.equals(name.equals("Windows"))) {
            return "\r\n";
        }
        return null;
    }

    private int outputType(String type) {
        if (type == outputTypeSAMI) {
            return 1;
        }
        if (type == outputTypeSubRip) {
            return 2;
        }
        return 0;
    }

    private float parseFloat(String str) {
        Matcher matcher = syncPattern.matcher(str);
        if (matcher.find()) {
            return Float.parseFloat(matcher.group());
        }
        return 0.0f;
    }

    private String parseFloatString(String str) {
        return String.valueOf(this.parseFloat(str));
    }

    private void ready() {
        this.syncTextField.transferFocus();
        this.inputCharset = this.charset(this.inputCharsetChoice.getSelectedItem());
        this.outputCharset = this.charset(this.outputCharsetChoice.getSelectedItem());
        this.lineDelimiter = this.lineDelimiterType(this.lineDelimiterChoice.getSelectedItem());
        this.sync = (long)(this.parseFloat(this.syncTextField.getText()) * 1000.0f);
    }

    public void run(File data) {
        SubtitleConverter sc = new SubtitleConverter(data, this.inputCharset);
        sc.setOutputCharset(this.outputCharset);
        sc.setLineDelimiter(this.lineDelimiter);
        sc.setSync(this.sync);
        sc.write(this.outputType);
    }

    public void convert(List<File> files, final int outputType) {
        final ArrayDeque<File> list = new ArrayDeque<File>();
        for (File file : files) {
            Matcher matcher = SubtitleConverter.PATTERN_FILE_EXTENTION.matcher(file.getName());
            if (!matcher.find()) continue;
            list.push(file);
        }
        this.run = true;
        this.ready();
        final ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        new Thread(new Runnable(){

            @Override
            public void run() {
                MainFrame.this.progressBar.setMaximum(list.size());
                EventQueue.invokeLater(MainFrame.this.startConvert);
                for (File file : list) {
                    service.execute(new SubtitleConverter(file, MainFrame.this.inputCharset, outputType){

                        @Override
                        public void run() {
                            this.setOutputCharset(MainFrame.this.outputCharset);
                            this.setLineDelimiter(MainFrame.this.lineDelimiter);
                            this.setSync(MainFrame.this.sync);
                            super.run();
                            EventQueue.invokeLater(MainFrame.this.updateProgressBar);
                        }
                    });
                }
                try {
                    service.shutdown();
                    service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                EventQueue.invokeLater(MainFrame.this.endConvert);
            }
        }).start();
    }

    @Override
    public void drop(DropTargetDropEvent e) {
        if (this.run || (e.getDropAction() & 2) == 0) {
            e.dropComplete(false);
            return;
        }
        e.acceptDrop(e.getDropAction());
        Transferable t = e.getTransferable();
        try {
            List list = (List)t.getTransferData(DataFlavor.javaFileListFlavor);
            this.convert(list, this.outputType(this.outTypeChoice.getSelectedItem()));
            e.dropComplete(true);
        }
        catch (UnsupportedFlavorException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void showAbout() {
        this.about.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.menuItemClose) {
            if (AppleApplication.isSupport()) {
                this.setVisible(false);
            } else {
                this.exit();
            }
        } else if (e.getSource() == this.menuItemExit) {
            this.exit();
        } else if (e.getSource() == this.menuItemOpenFile) {
            FileDialog dialog = new FileDialog(this);
            dialog.setMode(0);
            dialog.setFilenameFilter(this);
            dialog.setVisible(true);
            if (dialog.getFile() == null) {
                return;
            }
            this.run = true;
            this.setEnabled(false);
            this.ready();
            this.outputType = this.outputType(this.outTypeChoice.getSelectedItem());
            this.run(new File(dialog.getDirectory(), dialog.getFile()));
            this.setEnabled(true);
            this.run = false;
        } else if (e.getSource() == this.menuItemAbout) {
            this.showAbout();
        }
    }

    @Override
    public boolean accept(File dir, String name) {
        Matcher matcher = SubtitleConverter.PATTERN_FILE_EXTENTION.matcher(name);
        return matcher.find();
    }

    public void exit() {
        try {
            this.preferences.put("OutputType", this.outTypeChoice.getSelectedItem());
            this.preferences.put("InputCharset", this.inputCharsetChoice.getSelectedItem());
            this.preferences.put("OutputCharset", this.outputCharsetChoice.getSelectedItem());
            this.preferences.put("LineDelimiter", this.lineDelimiterChoice.getSelectedItem());
            this.preferences.putInt("WindowBoundsX", this.getX());
            this.preferences.putInt("WindowBoundsY", this.getY());
            this.preferences.flush();
        }
        catch (BackingStoreException e) {
            e.printStackTrace();
        }
        this.dispose();
        System.exit(0);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (AppleApplication.isSupport()) {
            this.setVisible(false);
        } else {
            this.exit();
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        this.app = AppleApplication.getApplication("com.myhyuny.MinySubtitleConverter.AppleApplication", this);
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void dragEnter(DropTargetDragEvent e) {
    }

    @Override
    public void dragOver(DropTargetDragEvent e) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent e) {
    }

    @Override
    public void dragExit(DropTargetEvent e) {
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (e.getSource() == this.syncTextField) {
            this.syncTextField.setText(this.parseFloatString(this.syncTextField.getText()));
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (e.getSource() == this.syncTextField) {
            this.syncTextField.setText(String.valueOf(this.parseFloatString(this.syncTextField.getText())) + " sec");
        }
    }
}

