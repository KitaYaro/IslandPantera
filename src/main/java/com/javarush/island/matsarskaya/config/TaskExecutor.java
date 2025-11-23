package com.javarush.island.matsarskaya.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TaskExecutor {
    private final ScheduledExecutorService scheduler;

    public TaskExecutor() {
        this.scheduler = Executors.newScheduledThreadPool(5);
    }

    public void executeTasks(List<Runnable> tasks) {
        List<Future<?>> futures = new ArrayList<>();

        for (Runnable task : tasks) {
            futures.add(scheduler.submit(task));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("\n" +
                        "The flow was interrupted: " + e.getMessage());
            } catch (ExecutionException e) {
                System.err.println("\n" +
                        "Task execution error: " + e.getMessage());
            }
        }
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return scheduler.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
