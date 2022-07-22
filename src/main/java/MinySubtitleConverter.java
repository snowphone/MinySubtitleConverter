/*
 * Decompiled with CFR 0.150.
 */

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.myhyuny.MinySubtitleConverter.MainFrame;
import com.myhyuny.MinySubtitleConverter.OutputType;
import com.myhyuny.MinySubtitleConverter.SubtitleConverter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

	@Parameter(names = {"--sync", "-s"}, description = "Modify sync in msec")
	private long sync = 0L;

	@Parameter(names = {"--to", "-t"}, description = "Target extension. Allowed options: [smi|srt]")
	private String ext = "";

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

        var jobs = list.stream().map(SubtitleConverter::new)
			.peek(it -> it.setOutputCharset(StandardCharsets.UTF_8));

		if (this.sync != 0L) {
			jobs = jobs.peek(it -> it.setSync(this.sync));
		}

		switch (this.ext.toLowerCase()) {
			case "smi":
				jobs = jobs.peek(it -> it.setOutputType(OutputType.SAMI));
				break;
			case "srt":
				jobs = jobs.peek(it -> it.setOutputType(OutputType.SUBRIP));
				break;
			// Default: do nothing
		}

		jobs.forEach(service::execute);

        service.shutdown();
        service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        System.out.println();
    }
}

