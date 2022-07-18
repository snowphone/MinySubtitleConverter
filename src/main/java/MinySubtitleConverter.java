/*
 * Decompiled with CFR 0.150.
 */
import com.myhyuny.MinySubtitleConverter.SubtitleConverter;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

public class MinySubtitleConverter {
    public static void main(String[] args) throws InterruptedException {
        if (args.length < 1) {
            try {
                Class<?> c = Class.forName("com.myhyuny.MinySubtitleConverter.MainFrame");
                c.getMethod("setVisible", Boolean.TYPE).invoke(c.newInstance(), true);
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            catch (InstantiationException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return;
        }
        LinkedList<File> list = new LinkedList<File>();
        String[] arrstring = args;
        int n = args.length;
        for (int i = 0; i < n; ++i) {
            Matcher matcher;
            String uri = arrstring[i];
            File file = new File(uri);
            if (!file.isFile() || !(matcher = SubtitleConverter.PATTERN_FILE_EXTENTION.matcher(uri)).find()) continue;
            list.add(file);
        }
        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (File file : list) {
            service.execute(new SubtitleConverter(file));
        }
        service.shutdown();
        service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        System.out.println();
    }
}

