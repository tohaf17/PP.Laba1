import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Input thread quantity:");
        int quantity = Integer.parseInt(scanner.nextLine().trim());

        List<TaskThread> threads = createThreads(quantity);

        for (TaskThread thread : threads) {
            new Thread(thread).start();
        }

        new Thread(() -> Stop(threads)).start();
    }

    public static void Stop(List<TaskThread> threads) {
        long startTime = System.currentTimeMillis();

        while (threads.stream().anyMatch(TaskThread::isRunning)) {
            long elapsed = System.currentTimeMillis() - startTime;

            for (TaskThread t : threads) {
                if (t.isRunning() && elapsed >= t.getDuration()) {
                    t.setRunning(false);
                }
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Stopper thread has finished.");
    }

    public static List<TaskThread> createThreads(int quantity) {
        System.out.println("Input durations in seconds (space separated):");

        List<Integer> durations = Arrays.stream(scanner.nextLine().trim().split("\\s+"))
                .map(d -> Integer.parseInt(d) * 1000)
                .collect(Collectors.toList());

        System.out.println("Input steps (space separated):");
        List<Integer> steps = Arrays.stream(scanner.nextLine().trim().split("\\s+"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        return IntStream.range(0, quantity)
                .mapToObj(i -> new TaskThread(steps.get(i), durations.get(i)))
                .collect(Collectors.toList());
    }
}

class TaskThread implements Runnable {
    private final int step;
    private final int duration;
    private long sum = 0;

    private volatile boolean isRunning = true;

    public TaskThread(int step, int duration) {
        this.step = step;
        this.duration = duration;
    }

    public boolean isRunning() { return isRunning; }
    public void setRunning(boolean running) { this.isRunning = running; }
    public int getDuration() { return duration; }

    @Override
    public void run() {
        long id = Thread.currentThread().getId();
        long additions = 0;
        long startTime = System.currentTimeMillis();

        while (isRunning) {
            sum += step;
            additions++;
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.printf("Thread №%d: Sum = %.2E, Additions = %d, Step = %d.00, Time = %dms%n",
                id, (double) sum, additions, step, elapsedTime);


    }
}