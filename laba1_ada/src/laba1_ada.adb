with Ada.Text_IO; use Ada.Text_IO;
with Ada.Integer_Text_IO; use Ada.Integer_Text_IO;
with Ada.Long_Float_Text_IO;
with Ada.Real_Time; use Ada.Real_Time;

procedure Laba1_Ada is
   package LLI_IO is new Ada.Text_IO.Integer_IO (Long_Long_Integer);

   protected Active_Counter is
      procedure Initialize (Count : Integer);
      procedure Decrement_And_Check;
   private
      Current_Count : Integer := 0;
   end Active_Counter;

   protected body Active_Counter is
      procedure Initialize (Count : Integer) is
      begin
         Current_Count := Count;
      end Initialize;

      procedure Decrement_And_Check is
      begin
         Current_Count := Current_Count - 1;
         if Current_Count = 0 then
            Put_Line ("All threads have completed their work.");
         end if;
      end Decrement_And_Check;
   end Active_Counter;

   protected Console is
      procedure Print_Result
        (Id         : Integer;
         Sum        : Long_Float;
         Additions  : Long_Long_Integer;
         Step       : Long_Float;
         Elapsed_Ms : Integer);
   end Console;

   protected body Console is
      procedure Print_Result
        (Id         : Integer;
         Sum        : Long_Float;
         Additions  : Long_Long_Integer;
         Step       : Long_Float;
         Elapsed_Ms : Integer)
      is
      begin
         Put ("Thread #");
         Put (Item => Id, Width => 0);
         Put (": Sum = ");
         Ada.Long_Float_Text_IO.Put
           (Item => Sum, Fore => 1, Aft => 2, Exp => 3);
         Put (", Additions = ");
         LLI_IO.Put (Item => Additions, Width => 0);
         Put (", Step = ");
         Ada.Long_Float_Text_IO.Put
           (Item => Step, Fore => 1, Aft => 2, Exp => 0);
         Put (", Time = ");
         Put (Item => Elapsed_Ms, Width => 0);
         Put_Line ("ms");
      end Print_Result;
   end Console;

   task type Worker_Task is
      entry Start
        (Init_Step     : Long_Long_Integer;
         Init_Duration : Integer;
         Init_Id       : Integer);
   end Worker_Task;

   task body Worker_Task is
      Step         : Long_Long_Integer;
      Duration_Ms  : Integer;
      Id           : Integer;
      Sum          : Long_Long_Integer := 0;
      Additions    : Long_Long_Integer := 0;
      Start_Time   : Time;
      Run_Duration : Time_Span;
      Elapsed_Ms   : Integer;
   begin
      accept Start
        (Init_Step     : Long_Long_Integer;
         Init_Duration : Integer;
         Init_Id       : Integer)
      do
         Step        := Init_Step;
         Duration_Ms := Init_Duration;
         Id          := Init_Id;
      end Start;

      Start_Time   := Clock;
      Run_Duration := Milliseconds (Duration_Ms);

      while Clock - Start_Time < Run_Duration loop
         Sum       := Sum + Step;
         Additions := Additions + 1;
      end loop;

      delay 0.01;

      Elapsed_Ms := Integer (To_Duration (Clock - Start_Time) * 1000.0);

      Console.Print_Result
        (Id,
         Long_Float (Sum),
         Additions,
         Long_Float (Step),
         Elapsed_Ms);

      Active_Counter.Decrement_And_Check;
   end Worker_Task;

   Quantity : Integer;

begin
   Put_Line ("Input thread quantity:");
   Get (Quantity);

   declare
      Durations : array (1 .. Quantity) of Integer;
      Steps     : array (1 .. Quantity) of Long_Long_Integer;
      Tasks     : array (1 .. Quantity) of Worker_Task;
   begin
      Put_Line ("Input durations in seconds:");
      for I in 1 .. Quantity loop
         Get (Durations (I));
         Durations (I) := Durations (I) * 1000;
      end loop;

      Put_Line ("Input steps:");
      for I in 1 .. Quantity loop
         LLI_IO.Get (Steps (I));
      end loop;

      Active_Counter.Initialize (Quantity);

      for I in 1 .. Quantity loop
         Tasks (I).Start (Steps (I), Durations (I), I);
      end loop;
   end;
end Laba1_Ada;