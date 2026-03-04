import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static AtomicInteger activeThreads = new AtomicInteger(0);
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Input thread quantity:");
        int quantity = Integer.parseInt(scanner.nextLine().trim());

        activeThreads.set(quantity);

        List<TaskThread> threads = createThreads(quantity);

        threads.stream()
                .map(Thread::new)
                .forEach(Thread::start);
    }

    public static List<TaskThread> createThreads(int quantity) {
        System.out.println("Input durations in seconds:");

        List<Integer> durations = Arrays.stream(scanner.nextLine().trim().split("\\s+"))
                .filter(s -> !s.isEmpty())
                .map(duration -> Integer.parseInt(duration) * 1000)
                .collect(Collectors.toList());

        System.out.println("Input steps:");

        List<Integer> steps = Arrays.stream(scanner.nextLine().trim().split("\\s+"))
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<TaskThread> threads = new ArrayList<>();

        threads.addAll(
                IntStream.range(0, quantity)
                        .mapToObj(i -> new TaskThread(steps.get(i), durations.get(i)))
                        .collect(Collectors.toList())
        );

        return threads;
    }
}

class TaskThread implements Runnable {
    private int step;
    private int duration;
    private long id;
    private long sum;

    private boolean isRunning = true;

    public TaskThread(int step, int duration) {
        this.step = step;
        this.duration = duration;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    @Override
    public void run() {
        this.id = Thread.currentThread().getId();

        long additions = 0;
        long startTime = System.currentTimeMillis();

        while (isRunning && (System.currentTimeMillis() - startTime) < duration) {
            sum += step;
            additions++;
        }

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.printf("Thread №%d: Sum = %.2E, Additions = %d, Step = %.2f, Time = %dms%n",
                id, (double) sum, additions, (double) step, elapsedTime);

        if (Main.activeThreads.decrementAndGet() == 0) {
            System.out.println("All threads have completed their work.");
        }
    }
}