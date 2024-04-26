package q3;
import java.io.IOException;
import java.util.Scanner;

//Tester program for tic-tac with min-max, depth limit, 
//board evaluation, and alpha-beta pruning
public class AlphaBetaTester
{
 //main program for tester
 public static void main(String[] args) throws IOException
 {
	 Scanner input = new Scanner(System.in);
	 System.out.print("Enter output file name: ");
	 String file = input.nextLine();    //output file name
	 boolean done = false;
	 while(!done) {		//loop until user enters even number
		 System.out.print("Enter an even board size: ");
		 int size = input.nextInt();	//get size from user
		 if(size % 2 == 0) {
			 done = true;
			 //create object and play game
			 AlphaBeta a = new AlphaBeta(size, file);
			 a.play();
		 }else {
			 System.out.println("You entered an odd number, try again.");
		 }
	 }
	 input.close();
 }
}