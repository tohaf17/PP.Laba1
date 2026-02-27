import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input thread quantity");
        int quantity = scanner.nextInt();

        List<TaskThread> threads = Create(quantity);

        for (TaskThread thread : threads) {
            thread.startThread();
        }

        Thread controller = new Thread(() -> {
            for (TaskThread thread : threads) {
                try {
                    Thread.sleep(thread.getTimeUntilStop());
                    thread.stopThread();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        controller.start();

        try {
            controller.join();
            for (TaskThread thread : threads) {
                thread.joinThread();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("End");
    }
    public static List<TaskThread> Create(int quantity){
        Random random = new Random();
        List<TaskThread> threads = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            double step = random.nextDouble() * 10.0;
            int sleepTime = random.nextInt(15000) + 10000; // від 10 до 25 секунд
            threads.add(new TaskThread(i + 1, step, sleepTime));
        }
        return threads;
    }
}

class TaskThread implements Runnable {
    private Thread thread;
    private int id;
    private double step;
    private int timeUntilStop;
    private volatile boolean isRunning;

    public TaskThread(int id, double step, int timeUntilStop) {
        this.id = id;
        this.step = step;
        this.timeUntilStop = timeUntilStop;
        this.isRunning = true;
        this.thread = new Thread(this);
    }

    public void startThread() { thread.start(); }
    public void stopThread() { this.isRunning = false; }
    public void joinThread() throws InterruptedException { thread.join(); }
    public int getTimeUntilStop() { return timeUntilStop; }

    @Override
    public void run() {
        double sum = 0.0;
        double currentElement = 0.0;
        long additions = 0;

        while (isRunning) {
            sum += currentElement;
            currentElement += step;
            additions++;
        }

        System.out.printf("Thread №%d: Sum = %e, Additions = %d, Step = %.2f%n", id, sum, additions, step);
    }
}