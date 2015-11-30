/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mobile.sdc.task;

import java.util.Vector;

/**
 *
 * @author ietservizi
 */
public class TaskEngine {
    private Vector taskQueue;
    private Runner runner;
    private boolean running;

    private class Runner extends Thread {
        boolean halt;

        Runnable currentTask = null;

        public void run() {
            System.out.println("Runner started");
            while (! halt) {
                synchronized (taskQueue) {
                    if (taskQueue.isEmpty()) {
                        try {
                            taskQueue.wait();
                        } catch (InterruptedException ex) {
                            //ex.printStackTrace();
                        }
                    } else {
                        currentTask = (Runnable) taskQueue.elementAt(0);
                        taskQueue.removeElementAt(0);
                    }
                }
                if (!halt && currentTask != null){
                    try {
                       currentTask.run();
                   } catch (Exception e) {
                    } finally {
                        currentTask = null;
                    }
                }
            }
            System.out.println("Runner dead");
        }

    }

    public TaskEngine() {
        init();
    }

    public void runTask(Runnable task) {
        if (runner.halt) {
            throw new RuntimeException("This TaskManager has been closed.");
        }
        synchronized (taskQueue) {
            taskQueue.addElement(task);
            taskQueue.notify();
        }
         
    }

    public void runTaskASAP(Runnable task) {
        synchronized (taskQueue) {
            taskQueue.insertElementAt(task, 0);
            taskQueue.notify();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void close() {
        runner.halt = true;
        synchronized (taskQueue) {
            taskQueue.removeAllElements();
            taskQueue.notify();
        }
        running = false;
    }

    public int getTaskCount() {
        synchronized (taskQueue) {
            return taskQueue.size();
        }
    }

    private void init() {
        taskQueue = new Vector();
        runner = new Runner();
        runner.start();

        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
    }

    public void killCurrentTask() {
        try {
            runner.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            runner = null;
            runner = new Runner();
        }

    }

}


