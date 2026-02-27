using System;
using System.Collections.Generic;
using System.Threading;

namespace Laba1
{
    public class Program
    {
        public static void Main()
        {
            Console.WriteLine("Input thread quantity:");
            int quantity=int.Parse(Console.ReadLine());
            List<TaskThread> threads=CreateWorkers(quantity);

            foreach (var thread in threads)
            {
                thread.Start();
            }

            Thread controller = new Thread(() => ControllerTask(threads));
            controller.Start();
            controller.Join();
            foreach (var worker in threads)
            {
                worker.Join();
            }

            Console.WriteLine("End");
        }
        public static List<TaskThread> CreateWorkers(int quantity)
        {
            Random random = new Random();
            List<TaskThread> workers = new List<TaskThread>();
            for (int i = 0; i < quantity; i++)
            {
                double step = random.NextDouble() * 10; 
                int sleepTime = random.Next(1000, 10000); 
                workers.Add(new TaskThread(i + 1, step, sleepTime));
            }
            return workers;
        }

        public static void ControllerTask(List<TaskThread> threads)
        {
            foreach (var thread in threads)
            {
                Thread.Sleep(thread.TimeUntilStop);
                thread.Stop();
            }
        }
    }

    public class TaskThread
    {
        private Thread thread;
        private int id;
        private double step;

        public int TimeUntilStop { get; private set; }

        private volatile bool isRunning;

        public TaskThread(int id, double step, int timeUntilStop)
        {
            this.id = id;
            this.step = step;
            this.TimeUntilStop = timeUntilStop;
            this.isRunning = true;
            this.thread= new Thread(()=>Calculate());
        }

        public void Start()
        {
            this.thread.Start();
        }

        public void Stop()
        {
            this.isRunning = false;
        }

        public void Join()
        {
            this.thread.Join();
        }

        private void Calculate()
        {
            double sum = 0.0;
            double currentElement = 0.0; 
            long additions = 0;

            while (isRunning)
            {
                sum += currentElement;
                currentElement += step; 
                additions++;
            }

            Console.WriteLine($"Thread №{id}: Sum = {sum:E2}, Additions = {additions}, Step = {step:F2}");
        }
    }
}