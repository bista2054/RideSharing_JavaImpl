package com.ridesharing;

import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.logging.Logger;

public class TaskQueue {
    private static final Logger logger = Logger.getLogger(TaskQueue.class.getName());
    private final Queue<RideTask> queue = new LinkedList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private boolean isStopped = false;

    public void addTask(RideTask task) {
        lock.lock();
        try {
            if (isStopped) {
                logger.warning("Attempted to add task after queue stopped: " + task);
                return;
            }
            queue.add(task);
            logger.info("Task added: " + task + ". Queue size: " + queue.size());
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public RideTask getTask() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty() && !isStopped) {
                logger.fine("Queue is empty, waiting for tasks...");
                notEmpty.await();
            }
            if (isStopped) {
                logger.info("Queue stopped. Returning null from getTask.");
                return null;
            }
            RideTask task = queue.poll();
            logger.info("Task retrieved: " + task + ". Queue size: " + queue.size());
            return task;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            boolean empty = queue.isEmpty();
            logger.fine("Queue isEmpty check: " + empty);
            return empty;
        } finally {
            lock.unlock();
        }
    }

    public void stop() {
        lock.lock();
        try {
            isStopped = true;
            logger.info("Queue stopped. Notifying all waiting threads.");
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }
}