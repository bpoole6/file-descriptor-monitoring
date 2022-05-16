package com.technologies.sunset.filedescriptor;


import com.sun.management.UnixOperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class GenerateFileDescriptors {
    private static Logger LOG = LoggerFactory.getLogger(GenerateFileDescriptors.class);
    static List<Path> paths = new ArrayList<>();
    static List<InputStream> files = new ArrayList<>();
    private Status status = Status.IDLE;
    private static ExecutorService mainThreadPool = Executors.newSingleThreadExecutor();
    public static void main(String[] args) throws InterruptedException, IOException {
        GenerateFileDescriptors t = new GenerateFileDescriptors();
        t.thread(11000, 4,1000);
    }

    public synchronized String thread(int count, int threadCount, int sleepInSeconds) throws InterruptedException, IOException {
        if (this.status != Status.IDLE) {
            return "Status:" + this.status.toString();
        }
        mainThreadPool.execute(()->{

        status = Status.WORKING;
        AtomicInteger ai = new AtomicInteger(count);

        Runnable run = () -> {
            int num = ai.decrementAndGet();
            while (num > 0) {
                try {
                    Path path = Files.createTempFile("", "");
                    paths.add(path);
                    files.add(Files.newInputStream(path));
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
                num = ai.decrementAndGet();

            }
        };
        try {
            ThreadPoolExecutor es = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
            for (int i = 0; i < threadCount; i++) {
                es.execute(run);
            }
            while (es.getActiveCount() > 0) {
                LOG.debug("Active threads: " + es.getActiveCount());

                Thread.sleep(1000);
            }
            Thread.sleep(sleepInSeconds*1000);
            listOpenFiles();

        }catch (Throwable t){
            t.printStackTrace();
        }
        finally {
            closeStreams();
            deleteTempFiles();
            files.clear();
            paths.clear();
            status = Status.IDLE;
        }
        });
        return "processing";
    }

    public void closeStreams() {
        this.status = Status.CLEARING;
        files.forEach((f) -> {
            try {
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void deleteTempFiles() {
        //Delete temp files
        try {
            for (Path path : paths) {
                if (Files.exists(path)) {
                    Files.delete(path);
                }
            }
        }catch (Throwable t){}
    }

    public Status getStatus() {
        return status;
    }

    public void listOpenFiles() {
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        if (os instanceof UnixOperatingSystemMXBean) {
            LOG.info("Number of open fd: " + ((UnixOperatingSystemMXBean) os).getOpenFileDescriptorCount());
        }
    }
}
