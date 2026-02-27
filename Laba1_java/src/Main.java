import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть кількість потоків: ");
        int quantity = scanner.nextInt();

        Random random = new Random();
        List<TaskThread> tasks = new ArrayList<>();

        for (int i = 1; i <= quantity; i++) {
            // Випадковий час роботи від 1 до 10 секунд
            int time = 1000 + random.nextInt(9000);
            // Випадковий крок прогресії від 1 до 10
            int step = 1 + random.nextInt(10);

            tasks.add(new TaskThread(i, time, step));
        }

        System.out.println("Запуск потоків...");
        for (TaskThread task : tasks) {
            task.start();
        }
    }
}
   class TaskThread {
    private final int id;
    private final int delayMillis;
    private final int step;

    // volatile гарантує, що зміна значення в одному потоці
    // буде миттєво видна в іншому (обчислювальному)
    private volatile boolean canRun = true;

    public TaskThread(int id, int delayMillis, int step) {
        this.id = id;
        this.delayMillis = delayMillis;
        this.step = step;
    }

    public void start() {
        // Потік для обчислень
        Thread calculationThread = new Thread(this::calculate);

        // Потік для зупинки (керуючий потік для цього завдання)
        Thread stoperThread = new Thread(this::stoper);

        calculationThread.start();
        stoperThread.start();
    }

    private void calculate() {
        long sum = 0;
        long additions = 0;
        int currentElement = 0;

        while (canRun) {
            sum += currentElement;
            currentElement += step;
            additions++;
        }

        System.out.printf("Потік №%d | Сума: %d | Доданків: %d%n",
                id, sum, additions);
    }

    private void stoper() {
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        canRun = false;
    }
}