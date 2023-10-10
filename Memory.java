import java.util.*;
import java.io.*;
import java.lang.*;

public class Memory 
{
   public static void main(String args[])
   {
      
     //Receives file name from Parent CPU Process
     Scanner sc = new Scanner(System.in); 
     String fileName = null;
	  fileName = sc.nextLine();
     
     //Creates the Memory Array and initializes all values to -1
     int[] memoryArray = new int[2000];
     Arrays.fill(memoryArray, -1);
      
     try{               	
         //Sets up the Scanner and FileInputStream
         FileInputStream dataFile = null;
         Scanner inFS = null;
         dataFile = new FileInputStream(fileName);
         inFS = new Scanner(dataFile);
         
         
         int memIndex = 0;
         
         //Loop to parse the file and fill the array with memory instructions
         while (inFS.hasNextLine()) {
            String currentLine = inFS.nextLine();
            
            //Splits the current line into strings separated by spaces, so it can focus on only the first memory address
            String entries[] = currentLine.split(" ");
            char firstCharacter = ' ';
            
            //Skips lines that don't begin with digit or period
            if(entries[0].length() > 0)
               firstCharacter = entries[0].charAt(0);
            
            //if the first character of the line is a digit,
            //store the instruction in the current index of Memory
            if(Character.isDigit(firstCharacter)){
               memoryArray[memIndex] = Integer.parseInt(entries[0]);
               memIndex++;
           }
           
           //if the first character of the line is a digit,
           //jump to that index of memory and then store the instruction
           else if(firstCharacter == '.'){
               String jumpAddress = entries[0].substring(1);
               memIndex = Integer.parseInt(jumpAddress);
           }
         }
         
         boolean active = true;
         String instruction = null;
         
         //While memory process is active, read or write to the memory address
         //from the instruction passed from the CPU Process
         while(active) {
        	   instruction = sc.nextLine();
        	   String index[] = instruction.split(" ");
        	 
        	   //if terminate instruction is given, end the memory process
            if(index[0].equals("terminate")){
        		   active = false;
            }
        	 
            //if write instruction is given, store data in the given address
            else if(index[0].equals("write")){
               int address = Integer.parseInt(index[1]);
               int data = Integer.parseInt(index[2]);
               memoryArray[address] = data;
            }
          
            //if read instruction is given, pass the value at given memory address to the CPU
            else {
        		   int address = Integer.parseInt(index[1]);
        		   System.out.println(memoryArray[address]);
        	   }
          
         }//end of Memory loop
         
                    
      } 
      
	  
	  //Error Handling if file did not open successfully
     catch (IOException ex) {
         System.out.println("File cannot be opened");	 
     }
     
   }//end of main

}//end of class

