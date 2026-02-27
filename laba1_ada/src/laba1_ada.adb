with Ada.Text_IO; use Ada.Text_IO;
with Ada.Integer_Text_IO; use Ada.Integer_Text_IO;
with Ada.Float_Text_IO; use Ada.Float_Text_IO;
with Ada.Numerics.Float_Random;

procedure Laba1_Ada is
   Gen : Ada.Numerics.Float_Random.Generator;
   
   User_Num_Threads : Integer; 
begin
   Put ("Enter number of threads: ");
   Get (User_Num_Threads);

   declare
      protected Printer is
         procedure Print_Result (ID : Integer; Sum_Val : Float; Add_Val : Long_Long_Integer);
      end Printer;

      protected body Printer is
         procedure Print_Result (ID : Integer; Sum_Val : Float; Add_Val : Long_Long_Integer) is
         begin
            Put ("Thread #" & Integer'Image (ID) & " ");
            Put ("Sum: "); 
            Put (Sum_Val, Fore => 1, Aft => 2, Exp => 3);
            Put_Line (" Additions: " & Long_Long_Integer'Image (Add_Val));
         end Print_Result;
      end Printer;

      task type Thread_Task is
         entry Start (ID : Integer; Step : Float);
         entry Stop;
      end Thread_Task;

      task body Thread_Task is
         My_ID : Integer;
         My_Step : Float;
         Current_Val : Float := 0.0;
         Sum : Float := 0.0;
         Additions : Long_Long_Integer := 0; 
      begin
         accept Start (ID : Integer; Step : Float) do
            My_ID := ID;
            My_Step := Step;
         end Start;

         loop
            select
               accept Stop;
               exit;
            else
               Sum := Sum + Current_Val;
               Current_Val := Current_Val + My_Step;
               Additions := Additions + 1;
            end select;
         end loop;

         Printer.Print_Result (My_ID, Sum, Additions);
      end Thread_Task;

      type Thread_Array is array (1 .. User_Num_Threads) of Thread_Task;
      Threads : Thread_Array;

      task Controller;

      task body Controller is
         Step_Val : Float;
         Delay_Dur : Duration;
      begin
         Ada.Numerics.Float_Random.Reset (Gen);
         
         for I in 1 .. User_Num_Threads loop
            Step_Val := Ada.Numerics.Float_Random.Random (Gen) * 10.0;
            Threads (I).Start (I, Step_Val);
         end loop;

         for I in 1 .. User_Num_Threads loop
            Delay_Dur := Duration (Ada.Numerics.Float_Random.Random (Gen) * 2.0 + 1.0);
            delay Delay_Dur;
            Threads (I).Stop;
         end loop;
      end Controller;

   begin
      null;
   end; 
   
end Laba1_Ada;