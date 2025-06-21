package com.ridesharing;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class RideSharingSystem {
    private static final Logger logger = Logger.getLogger(RideSharingSystem.class.getName());

    public static void main(String[] args) {
        int workerCount = 5;
        int taskCount = 20;
        String outputFile = "ride_results.txt";

        logger.info("Starting RideSharingSystem with " + workerCount + " workers and " + taskCount + " tasks.");

        // Clear previous output file
        try {
            new FileWriter(outputFile, false).close();
            logger.info("Cleared previous output file: " + outputFile);
        } catch (IOException e) {
            logger.severe("Error clearing output file: " + e.getMessage());
        }

        TaskQueue taskQueue = new TaskQueue();
        AtomicBoolean isRunning = new AtomicBoolean(true);
        ExecutorService executor = Executors.newFixedThreadPool(workerCount);

        // Start workers
        for (int i = 0; i < workerCount; i++) {
            logger.info("Starting worker " + i);
            executor.execute(new RideWorker(i, taskQueue, isRunning, outputFile));
        }

        // Add tasks
        for (int i = 0; i < taskCount; i++) {
            RideTask task = new RideTask(
                    "Ride-" + i,
                    "Location-" + i,
                    "Destination-" + i,
                    1 + (i % 4)
            );
            logger.info("Adding task: " + task);
            taskQueue.addTask(task);
        }

        // Shutdown after delay
        try {
            logger.info("System running. Sleeping for 5 seconds before shutdown.");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.warning("Main thread interrupted during sleep.");
            Thread.currentThread().interrupt();
        } finally {
            logger.info("Initiating shutdown sequence.");
            isRunning.set(false);
            taskQueue.stop();
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    logger.warning("Executor did not terminate in time. Forcing shutdown.");
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                logger.warning("Interrupted during executor termination. Forcing shutdown.");
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            logger.info("RideSharingSystem shutdown complete.");
        }
    }
}