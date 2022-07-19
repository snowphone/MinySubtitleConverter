/*
 * Decompiled with CFR 0.150.
 */

import com.myhyuny.MinySubtitleConverter.SubtitleConverter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MinySubtitleConverter {
    public static void main(String[] args) throws InterruptedException {
        if (args.length < 1) {
            try {
                Class<?> c = Class.forName("com.myhyuny.MinySubtitleConverter.MainFrame");
                c.getMethod("setVisible", Boolean.TYPE).invoke(c.getDeclaredConstructor().newInstance(), true);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
            return;
        }
        List<File> list = Arrays.stream(args)
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

