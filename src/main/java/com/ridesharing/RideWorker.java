package com.ridesharing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class RideWorker implements Runnable {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private final TaskQueue taskQueue;
    private final AtomicBoolean isRunning;
    private final int workerId;
    private final String outputFile;

    public RideWorker(int workerId, TaskQueue taskQueue, AtomicBoolean isRunning, String outputFile) {
        this.workerId = workerId;
        this.taskQueue = taskQueue;
        this.isRunning = isRunning;
        this.outputFile = outputFile;
    }

    @Override
    public void run() {
        String startMessage = String.format("%s Worker %d started%n", dateFormat.format(new Date()), workerId);
        writeToFile(startMessage);

        while (isRunning.get() || !taskQueue.isEmpty()) {
            try {
                RideTask task = taskQueue.getTask();
                if (task == null) continue;

                String processMessage = String.format("%s Worker %d processing ride: %s%n",
                        dateFormat.format(new Date()), workerId, task.toString());
                writeToFile(processMessage);

                // Simulate processing time
                Thread.sleep((long) (Math.random() * 1000));

            } catch (InterruptedException e) {
                String errorMessage = String.format("%s Worker %d interrupted: %s%n",
                        dateFormat.format(new Date()), workerId, e.getMessage());
                writeToFile(errorMessage);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                String errorMessage = String.format("%s Worker %d error: %s%n",
                        dateFormat.format(new Date()), workerId, e.getMessage());
                writeToFile(errorMessage);
            }
        }

        String endMessage = String.format("%s Worker %d finished%n", dateFormat.format(new Date()), workerId);
        writeToFile(endMessage);
    }

    private synchronized void writeToFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            writer.write(message);
        } catch (IOException e) {
            System.err.printf("Error writing to file: %s%n", e.getMessage());
        }
    }
}