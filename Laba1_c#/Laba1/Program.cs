using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading;

namespace Laba1
{
    public class Program
    {
        public static int ActiveThreads;
        public static void Main()
        {
            Console.WriteLine("Input thread quantity:");
            int quantity=int.Parse(Console.ReadLine());
            ActiveThreads=quantity;
            List<TaskThread> threads=CreateThreads(quantity);
            threads.Select(thread => new Thread(thread.Run)).ToList().ForEach(thread => thread.Start());
            
        }
        public static List<TaskThread> CreateThreads(int quantity)
        {
            Console.WriteLine("Input durations in seconds:");

            List<int> durations=Console.ReadLine()
                .Split(' ', StringSplitOptions.RemoveEmptyEntries)
                .Select(duration=>int.Parse(duration)*1000)
                .ToList();
            
            Console.WriteLine("Input steps:");
            
            List<int> steps=Array.ConvertAll(Console.ReadLine().Split(' '), int.Parse).ToList();

            List<TaskThread> threads = new List<TaskThread>();
            threads.AddRange(
                Enumerable.Range(0, quantity)
                .Select(i => new TaskThread(steps[i], durations[i]))
            );

            return threads;
        }

    }

    public class TaskThread
    {
        private int step;
        private int duration;
        private int id;
        private long sum;

        public bool IsRunning { get; set; } =true;

        public TaskThread(int step, int duration)
        {
            this.step = step;
            this.duration = duration;
        }

        public void Run()
        {
            this.id = Thread.CurrentThread.ManagedThreadId;

            long additions = 0;

            Stopwatch stopwatch = Stopwatch.StartNew();

            while (IsRunning && stopwatch.ElapsedMilliseconds < duration)
            {
                sum += step;
                additions++;
            }
            Thread.Sleep(10);
            Console.WriteLine($"Thread №{id}: Sum = {sum:E2}, Additions = {additions}, Step = {step:F2}, Time = {stopwatch.ElapsedMilliseconds}ms");
            if(Interlocked.Decrement(ref Program.ActiveThreads) == 0)
            {
                Console.WriteLine("All threads have completed their work.");
            }
        }
    }
}