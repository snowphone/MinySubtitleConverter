/*
 * Decompiled with CFR 0.150.
 */

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.myhyuny.MinySubtitleConverter.MainFrame;
import com.myhyuny.MinySubtitleConverter.SubtitleConverter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MinySubtitleConverter {
    @Parameter(names = {"--help", "-h"}, help = true)
    private boolean help = false;

    @Parameter(description = "Subtitles...")
    private List<String> uriList = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        var main = new MinySubtitleConverter();
        var jcmd = JCommander.newBuilder()
                .addObject(main)
                .build();

        jcmd.parse(args);
        if (main.help) {
            jcmd.setProgramName("MinySubtitleConverter");
            jcmd.usage();
            return;
        }
        main.run();
    }

    void run() throws InterruptedException {
        if (this.uriList.isEmpty()) {
            try {
                Class<?> c = MainFrame.class;
                c.getMethod("setVisible", Boolean.TYPE)
                        .invoke(c.getDeclaredConstructor().newInstance(), true);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
            return;
        }
        List<File> list = this.uriList
                .stream()
                .filter(uri -> SubtitleConverter.PATTERN_FILE_EXTENTION.matcher(uri).find())
                .map(File::new)
                .filter(File::isFile)
                .collect(Collectors.toList());

        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        list.stream().map(SubtitleConverter::new).forEach(service::execute);

        service.shutdown();
        service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        System.out.println();
    }
}

