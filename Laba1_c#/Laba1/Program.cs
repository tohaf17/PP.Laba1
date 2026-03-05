using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading;

namespace Laba1
{
    public class Program
    {
        public static void Main()
        {
            Console.WriteLine("Input thread quantity:");
            int quantity = int.Parse(Console.ReadLine());
            List<TaskThread> threads = CreateThreads(quantity);

            foreach (TaskThread thread in threads)
            {
                new Thread(thread.Run).Start();
            }

            Thread stopper = new Thread(() => Run(threads));
            stopper.Start();
        }
        public static void Run(List<TaskThread> threads)
        {
            var stopwatch = Stopwatch.StartNew();

            while (threads.Any(t => t.IsRunning))
            {
                long elapsed = stopwatch.ElapsedMilliseconds;
                foreach (var thread in threads)
                {
                    if (thread.IsRunning && elapsed >= thread.Duration)
                    {
                        thread.IsRunning = false;
                    }
                }
                Thread.Sleep(10); 
            }
            Console.WriteLine("All threads have completed their work.");
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
        public int Step { get; set; }
        public int Duration { get; set; }
        public volatile bool IsRunning=true;
        public TaskThread(int step, int duration)
        {
            Step = step;
            Duration = duration;
        }

        public void Run()
        {
            int id = Thread.CurrentThread.ManagedThreadId;

            long sum = 0;
            long additions = 0;

            Stopwatch stopwatch = Stopwatch.StartNew();

            while (IsRunning)
            {
                sum += Step;
                additions++;
            }
            Console.WriteLine($"Thread №{id}: Sum = {sum:E2}, Additions = {additions}, Step = {Step:F2}, Time = {Duration}ms");
            
        }
    }
}