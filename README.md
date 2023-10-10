# Operating-System-Simulator
Listing of Files:
CPU.java
Memory.java


CPU.java
- Contains the CPU process of the OS Project, is the parent process which executes the child Memory file, so only execution of this file is needed

Memory.java
- Contains the Memory process of the OS project, is run by executing CPU.java which uses an exec call to open this child process

How to compile and run your project:
1) Compile both CPU.java and Memory.java
	- javac CPU.java
	- javac Memory.java

2) Execute the CPU.java file with two command line arguments: file name and timer value
	For example..
	- java CPU sample1.txt 30
	- java CPU sample2.txt 30
	- java CPU sample3.txt 30
	- java CPU sample4.txt 30
	- java CPU sample5.txt 30

3) Output of that file should then be printed to the screen!

