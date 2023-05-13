package xyz.oribuin.eternalcrates.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SchedulerUtil {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
            4,
            (new ThreadFactoryBuilder().setNameFormat("EternalCrates-%d").build())
    );

    public static CompletableFuture<Void> async(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, scheduler);
    }

    public static ScheduledFuture<?> delayedTask(Runnable runnable, long delay) {
        return scheduler.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    public static ScheduledFuture<?> repeatingTask(Runnable runnable, long delay, long period, TimeUnit unit) {
        return scheduler.scheduleAtFixedRate(runnable, delay, period, unit);
    }

}
