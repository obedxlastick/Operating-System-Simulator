import java.io.*;
import java.util.*;
import java.lang.Runtime;
import java.util.concurrent.*;

public class CPU 
{
   //Function to check if the memory access by the user is valid
   public static boolean checkMemoryAccess(String state, int address){
         if(state.equals("user mode") && address > 999){
            //returns false if the memory address is 1000 or greater as it's not acessible in user mode and prints a error message
            System.out.println("Memory Violation: Acessing System Address " + address + " in user mode.");
            
            return false;
         }
         
         //else it's a valid memory access
         else
            return true;
   }
   
   
   public static void main(String args[])
   {
   
      try
      {            
	      //Creating the child process for the Memory File
          Runtime rt = Runtime.getRuntime();

	      Process proc = rt.exec("java Memory");

	      //Setting up the process streams to act as pipes for communication
	      InputStream is = proc.getInputStream();
	      OutputStream os = proc.getOutputStream();

	      //Sending the name of the file to open to Memory Process
	      PrintWriter pw = new PrintWriter(os);
	      pw.printf(args[0] + '\n');
	      pw.flush();
	      
          //CPU Process waits 5 seconds for Memory Process to set up Array
          proc.waitFor(5, TimeUnit.SECONDS);
         
          //Creates scanner to read in from memory process
          Scanner sc = new Scanner(is);
          String command = null;
         
         //Initializes all the local register variables
         int PC = 0;
         int SP = 1000;
         int IR = 0;
         int AC = 0;
         int X = 0;
         int Y = 0;

         //Flag to know if CPU processs should continue
         boolean execute = true;
         
         //Variable to know if user or kernel mode
         String state = "user mode";
         
         //Timer value passed in from the parameters
         int timer = Integer.parseInt(args[1]);
         int instructionsCompleted = 0;
         
         boolean interruptsActive = true;
         boolean timerInterrupt = false;
          
         //CPU Process Loop
         while(execute){
            
            //if the number of instructions completed equals the system timer value
            //then a timer interrupt should occur and the instructions are reset to continue counting
            if(instructionsCompleted == timer){
               timerInterrupt = true;
               instructionsCompleted = 0;
            }
            
            //Execution of Timer Interrupt assuming interrupts are currently active
            if(timerInterrupt && interruptsActive){
               //Switches to kernel mode, saves SP and PC register values to the system stack
               state = "kernel mode";
                    
               int userStack = SP;
                    
               SP = 2000;
                    
               SP = SP - 1;
               command = "write " + Integer.toString(SP) + " " + Integer.toString(userStack);
               pw.printf(command + '\n');
               pw.flush();
                    
               SP = SP - 1;
               command = "write " + Integer.toString(SP) + " " + Integer.toString(PC);
               pw.printf(command + '\n');
               pw.flush();

               //Jumps to address 1000 for timer interrupt execution
               PC = 1000;
                        
               //No interrupts can occur during processing of an interrupt
               interruptsActive = false;
               timerInterrupt = false;
               
            }
            
            
            //reads the instruction stored at the given memory address
            command = "read " + Integer.toString(PC);
            pw.printf(command + '\n');
            pw.flush();

            //Stores the instruction into IR register
            IR = sc.nextInt();
            
            //Execution of logic for instruction based on tis value
            switch (IR) {
            	
            	//Load the given value into the AC Register
               case 1: {
                  PC = PC + 1;
                  
                  command = "read " + Integer.toString(PC);
                  pw.printf(command + '\n');
                  pw.flush();
                  
                  AC = sc.nextInt();
                  
                  break;
                }
            	
            	//Load the value at the given address into the AC register
               case 2: {
                    PC = PC + 1;
                    
                    command = "read " + Integer.toString(PC);
                    pw.printf(command + '\n');
                    pw.flush();
                    
                    int address = sc.nextInt();
                    
                    //Only carries out instruction if it's a valid memory address to accesss in user mode
                    if(checkMemoryAccess(state, address)){
                        command = "read " + Integer.toString(address);
                        pw.printf(command + '\n');
                        pw.flush();
                    
                        AC = sc.nextInt();
                    }
                    
                    else
                        execute = false;
                    
                    break;
                }
            	
            	//Load the value from the address found in the given address into the AC register
               case 3: {
                    PC = PC + 1;
                    
                    command = "read " + Integer.toString(PC);
                    pw.printf(command + '\n');
                    pw.flush();
                    
                    int address1 = sc.nextInt();
                    
                    command = "read " + Integer.toString(address1);
                    pw.printf(command + '\n');
                    pw.flush();
                    
                    int address2 = sc.nextInt();
                    
                    
                    if(checkMemoryAccess(state, address1) && checkMemoryAccess(state, address2)){
                        command = "read " + Integer.toString(address2);
                        pw.printf(command + '\n');
                        pw.flush();
                    
                        AC = sc.nextInt();
                    }
                    
                    else
                        execute = false;
                    
                    break;
                }
            	
            	//Load the value from the given address plus X into the AC register
               case 4: {
                    PC = PC + 1;
                    
                    command = "read " + Integer.toString(PC);
                    pw.printf(command + '\n');
                    pw.flush();
                    
                    int address = sc.nextInt() + X;
                    
                    if(checkMemoryAccess(state, address)){
                        command = "read " + Integer.toString(address);
                        pw.printf(command + '\n');
                        pw.flush();
                    
                        AC = sc.nextInt();
                    }
                    
                    else
                        execute = false;
                     
                    break;
                }
            	
            	//Load the value from the given address plus Y into the AC register
               case 5: {
                    PC = PC + 1;
                    
                    command = "read " + Integer.toString(PC);
                    pw.printf(command + '\n');
                    pw.flush();
                    
                    int address = sc.nextInt() + Y;
                    
                    if(checkMemoryAccess(state, address)){
                        command = "read " + Integer.toString(address);
                        pw.printf(command + '\n');
                        pw.flush();
                    
                        AC = sc.nextInt();
                    }
                    
                    else
                        execute = false;
                   
                    break;
                }
            	
            	//Load the value from the SP register plus X register into the AC register
               case 6: {                    
                    int address = SP + X;
                    
                    if(checkMemoryAccess(state, address)){
                        command = "read " + Integer.toString(address);
                        pw.printf(command + '\n');
                        pw.flush();
                    
                        AC = sc.nextInt();
                    }
                    
                    else
                        execute = false;
                   
                    break;
                }
            	
            	//Write the value from the AC into the given memory address
               case 7: {                    
                    PC = PC + 1;
                    
                    command = "read " + Integer.toString(PC);
                    pw.printf(command + '\n');
                    pw.flush();
                    
                    int address = sc.nextInt();
                    
                    if(checkMemoryAccess(state, address)){
                        command = "write " + Integer.toString(address) + " " + Integer.toString(AC);
                        pw.printf(command + '\n');
                        pw.flush();
                    }
                    
                    else
                        execute = false;
                   
                    break;
                }
            	
            	//Generates a random int from 1 to 100 and stores into the AC register
               case 8: {                    
                    Random rand = new Random();
                    int randomNum = rand.nextInt(100) + 1;
                    AC = randomNum;
                   
                    break;
                }
            	            	
               //Writes AC either as an int or char to screen based on port value 
            	case 9: {
                    PC = PC + 1;
                    
                    command = "read " + Integer.toString(PC);
                    pw.printf(command + '\n');
                    pw.flush();
                    
                    int port = sc.nextInt();
                    
                    if(port == 1)
                    	System.out.print(AC);
                    
                    else{
                     char letter = (char)AC;
                     System.out.print(letter); 
                    }
                    
                    break;
                }
                
            	//Take the sum of the AC and X and store it in the AC register
               case 10: {
            		AC = AC + X;
            		break;
            	}
            	
            	//Take the sum of the AC and Y and store it in the AC register
               case 11: {
            		AC = AC + Y;
            		break;
            	}
            	
            	//Take the difference of the AC and X and store it in the AC register
               case 12: {
            		AC = AC - X;
            		break;
            	}
            	
            	//Take the difference of the AC and Y and store it in the AC register
               case 13: {
            		AC = AC - Y;
            		break;
            	}
            	
            	//Set the X register equal to the AC register
               case 14: {
            		X = AC;
            		break;
            	}
            	
            	//Set the AC register equal to the X register
               case 15: {
            		AC = X;
            		break;
            	}
            	
            	//Set the Y register equal to the AC register
               case 16: {
            		Y = AC;
            		break;
            	}
            	
            	//Set the AC register equal to the Y register
               case 17: {
            		AC = Y;
            		break;
            	}
            	
            	//Set the SP register equal to the AC register
               case 18: {
            		SP = AC;
            		break;
            	}
            	
            	//Set the AC register equal to the SP register
               case 19: {
            		AC = SP;
            		break;
            	}
            	
            	//Jump to the given address
               case 20: {
                  PC = PC + 1;
                       
                  command = "read " + Integer.toString(PC);
                  pw.printf(command + '\n');
                  pw.flush();
                       
                  PC = sc.nextInt() - 1;

                  break;
              	}
               
            	//Jump to the given address if the value in the AC register is zero
               case 21: {
                  PC = PC + 1;
                  if(AC == 0){
                     
                     command = "read " + Integer.toString(PC);
                     pw.printf(command + '\n');
                     pw.flush();
                     
                     PC = sc.nextInt() - 1;

                  }
                  
                  break;
            	}
            	
            	//Jump to the given address if the value in the AC register is not zero
               case 22: {
                    PC = PC + 1;
                    if(AC != 0){
                       
                       command = "read " + Integer.toString(PC);
                       pw.printf(command + '\n');
                       pw.flush();
                       
                       PC = sc.nextInt() - 1;

                    }
                    
                    break;
              	}
              	
            	//Push return address onto the stack then jump to the address
               case 23: {
                    PC = PC + 1;
                    
                    command = "read " + Integer.toString(PC);
                    pw.printf(command + '\n');
                    pw.flush();
                    
                    int address = sc.nextInt();
                    
                    SP = SP - 1;
                    
                    //Pushes return address to stack
                    command = "write " + Integer.toString(SP) + " " + Integer.toString(PC + 1);
                    pw.printf(command + '\n');
                    pw.flush();
                    
                    
                    //Jumps to the address
                    PC = address - 1;
                    
                     
                    break;
              	}
            	
            	//Pops the return address from the stack and jumps back to that address
               case 24: {                    
                    command = "read " + Integer.toString(SP);
                    pw.printf(command + '\n');
                    pw.flush();
                    
                    //Pops the return address
                    int returnAddress = sc.nextInt();
                    
                    SP = SP + 1;
                    
                    //Jumps to that address
                    PC = returnAddress - 1;
                    break;
              	}
            	
            	//Increment the value in the X register
               case 25: {
                    X = X + 1;
                    break;
              	}
            	
            	//Decrement the value in the Y register
               case 26: {
                    X = X - 1;
                    break;
              	}
            	
            	//Push the AC register onto the Stack
               case 27: {   
                    SP = SP - 1;
                    command = "write " + Integer.toString(SP) + " " + Integer.toString(AC);
                    pw.printf(command + '\n');
                    pw.flush();
                    
                    break;              	
               }
            	
            	//Pop the top value from the stack and store it in the AC register
               case 28: {
                    command = "read " + Integer.toString(SP);
                    pw.printf(command + '\n');
                    pw.flush();
                    
                    AC = sc.nextInt();
                    
                    SP = SP + 1;
                    
                    break;
              	}
               
               //Performs system call interrupt
               case 29: {
                    
                    //if interrupts are enabled
                    if(interruptsActive){
                        
                        //switches to kernel mode
                        state = "kernel mode";
                    
                        int userStack = SP;
                    
                        SP = 2000;
                    
                        //Writes the SP and PC to the system stack
                        SP = SP - 1;
                        command = "write " + Integer.toString(SP) + " " + Integer.toString(userStack);
                        pw.printf(command + '\n');
                        pw.flush();
                    
                        SP = SP - 1;
                        command = "write " + Integer.toString(SP) + " " + Integer.toString(PC + 1);
                        pw.printf(command + '\n');
                        pw.flush();

                        PC = 1499;
                        //Jumps to address 1500 for processing of system interrupts
                        
                        interruptsActive = false;
                        //Interrupts are disabled to prevent nested execution
                    }
                    
                    break;
               }
               
               //Returns from an interrupt
               case 30: {
                    
                    //Switches back to user state
                    state = "user mode";
                    
                    //Pops the PC and SP values and reassigns them
                    command = "read " + Integer.toString(SP);
                    pw.printf(command + '\n');
                    pw.flush();
                    
                    PC = sc.nextInt() - 1;
                    
                    SP = SP + 1;
                    
                    command = "read " + Integer.toString(SP);
                    pw.printf(command + '\n');
                    pw.flush();
                    
                    SP = sc.nextInt();
                    
                    interruptsActive = true;
                    //Interrupts are enabled again
                        
                    break;
               }
            	
            	//End execution of program
               case 50: {
            		//terminates the while loop of CPU process
                  execute = false;
            		
            		//signal to memory to end process
                  command = "terminate " ;
            		pw.printf(command + '\n');
            		pw.flush();
                     
            		break;
            	}
             
            } //end of switch statement
            
            //Goes to the next address in memory
            PC = PC + 1;
            
            //Checks if current address is acessible by user
            //if memory access is false or invalid, ends the CPU process with an error message
            if(checkMemoryAccess(state, PC) == false){
               execute = false;
            }
            
            //Count of completed instructions for timer interrupt
            instructionsCompleted = instructionsCompleted + 1;

         
         }//end of while loop
         
         //if error terminates process, tells memory process to also terminate
         if(!execute){
            command = "terminate " ;
            pw.printf(command + '\n');
            pw.flush();
         }
         
      } //end of try loop
      
      //Error handling if child process was not created properly
      catch (Throwable t)
      {
	      t.printStackTrace();
      }
   
   }  //end of main 

}//end of class

