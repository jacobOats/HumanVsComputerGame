package q3;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

//This program plays tic-tac game using min-max, depth limit,
//board evaluation, and alpha-beta pruning
public class AlphaBeta
{
    private final char EMPTY = ' ';                //empty slot
    private final char COMPUTER = 'X';             //computer
    private final char PLAYER = '0';               //player
    private final int MIN = 0;                     //min level
    private final int MAX = 1;                     //max level
    private final int LIMIT = 6;                   //depth limit
    private FileWriter outfile;

    //Board class (inner class)
    private class Board
    {
        private char[][] array;                    //board array

        //Constructor of Board class
        private Board(int size)
        {
            array = new char[size][size];          //create array
                                             
            for (int i = 0; i < size; i++)         //fill array with empty slots   
                for (int j = 0; j < size; j++)
                    array[i][j] = EMPTY;
        }
    }

    private Board board;                           //game board
    private int size;                              //size of board
    
    //Constructor of AlphaBeta class
    public AlphaBeta(int size, String file) throws IOException
    {
        this.board = new Board(size);              //create game board 
        this.size = size;                          //set board size
        this.outfile = new FileWriter(file);
    }

    //Method plays game
    public void play() throws IOException
    {
        for(int i=0;i<(size*size)/2;i++)           //computer and player take turns
        {
            board = playerMove(board);             //player makes a move
            displayPoints(board);				   //print player and computer points
            board = computerMove(board);           //computer makes a move
            displayPoints(board);				   //print player and computer points
        }
        if(!full(board)) {
        	board = playerMove(board);
        	displayPoints(board);
        }
        //determine winner, and print out corresponding message
        if(computerPoints(board) > playerPoints(board)) {
        	System.out.println("Computer wins.");
        	outfile.write("Computer wins.\n");
        }else if(playerPoints(board) > computerPoints(board)) {
        	System.out.println("Player wins.");
        	outfile.write("Player wins.\n");
        }else if(draw(board)) {
        	System.out.println("Draw.");
        	outfile.write("Draw.\n");
        }
        outfile.close();
    }

    //Method lets the player make a move
    private Board playerMove(Board board) throws IOException
    {
        System.out.print("Player move: ");         //prompt player
        
        Scanner scanner = new Scanner(System.in);  //read player's move
        int i = scanner.nextInt();
        int j = scanner.nextInt();
        outfile.write("Player move: " + i + " " + j + "\n");

        board.array[i][j] = PLAYER;                //place player symbol
        
        displayBoard(board);                       //display board
        
        return board;                              //return updated board
    }

    //Method determines computer's move
    private Board computerMove(Board board) throws IOException
    {                                              //generate children of board
        LinkedList<Board> children = generate(board, COMPUTER);

        int maxIndex = -1;
        int maxValue = Integer.MIN_VALUE;
                                                   //find the child with
        for (int i = 0; i < children.size(); i++)  //largest minmax value
        {
            int currentValue = minmax(children.get(i), MIN, 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (currentValue > maxValue)
            {
                maxIndex = i;
                maxValue = currentValue;
            }
        }

        Board result = children.get(maxIndex);     //choose the child as next move
        int[] array = new int[2];
        array = findIndex(board, result);                                     
        System.out.print("Computer move: " + array[0] + " " + array[1] + "\n");
        outfile.write("Computer move: " + array[0] + " " + array[1] + "\n");
        displayBoard(result);                      //print next move

        return result;                             //return updated board
    }

    //Method computes min-max value of a board
    private int minmax(Board board, int level, int depth, int alpha, int beta)
    {
        if (full(board) || depth >= LIMIT)
            return evaluate(board);                //if board is terminal or depth limit is reached
        else                                       //evaluate board
        {
            if (level == MAX)                      //if board is at max level     
            {
                 LinkedList<Board> children = generate(board, COMPUTER);
                                                   //generate children of board
                 int maxValue = Integer.MIN_VALUE;

                 for (int i = 0; i < children.size(); i++)
                 {                                 //find minmax values of children
                     int currentValue = minmax(children.get(i), MIN, depth+1, alpha, beta);
                                                   
                     if (currentValue > maxValue)  //find maximum of minmax values
                         maxValue = currentValue;
                                                   
                     if (maxValue >= beta)         //if maximum exceeds beta stop
                         return maxValue;
                                                   
                     if (maxValue > alpha)         //if maximum exceeds alpha update alpha
                         alpha = maxValue;
                 }

                 return maxValue;                  //return maximum value   
            }
            else                                   //if board is at min level
            {                     
                 LinkedList<Board> children = generate(board, PLAYER);
                                                   //generate children of board
                 int minValue = Integer.MAX_VALUE;

                 for (int i = 0; i < children.size(); i++)
                 {                                 //find minmax values of children
                     int currentValue = minmax(children.get(i), MAX, depth+1, alpha, beta);
                                     
                     if (currentValue < minValue)  //find minimum of minmax values
                         minValue = currentValue;
                                     
                     if (minValue <= alpha)        //if minimum is less than alpha stop
                         return minValue;
                                     
                     if (minValue < beta)          //if minimum is less than beta update beta
                         beta = minValue;
                 }

                 return minValue;                  //return minimum value 
            }
        }
    }

    //Method generates children of board using a symbol
    private LinkedList<Board> generate(Board board, char symbol)
    {
        LinkedList<Board> children = new LinkedList<Board>();
                                                   //empty list of children
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)         //go thru board
                if (board.array[i][j] == EMPTY)
                {                                  //if slot is empty
                    Board child = copy(board);     //put the symbol and
                    child.array[i][j] = symbol;    //create child board
                    children.addLast(child);
                }

        return children;                           //return list of children
    }  
    
    private int[] findIndex(Board board, Board result) {
    	int[] array = new int[2];
    	for(int i=0;i<size;i++) {
    		for(int j=0;j<size;j++) {
    			if(board.array[i][j] != result.array[i][j]) {
    				array[0] = i;
    				array[1] = j;
    			}
    		}
    	}
    	return array;
    }
    
    //Method to count 2 symbols next to each other on a row of the board
    private int count2Symbol(Board board, int i, char symbol) {
    	int count = 0;
    	
    	for(int j=0;j<size-1;j++) {
    		if(board.array[i][j] == symbol && board.array[i][j+1] == symbol) {
    			count++;
    		}
    	}
    	return count;
    }
    //Method to count 3 symbols next to each other on a row of the board
    private int count3Symbol(Board board, int i, char symbol) {
    	int count = 0;
    	for(int j=0;j<size-2;j++) {
    		if(board.array[i][j] == symbol && board.array[i][j+1] == symbol && board.array[i][j+2] == symbol) {
    			count++;
    		}
    	}
    	return count;
    }
    //Method to count 3 symbols next to each other on a column of the board
    private int count3SymbolColumn(Board board, int i, char symbol) {
    	int count = 0;
    	for(int j=0;j<size-2;j++) {
    		if(board.array[j][i] == symbol && board.array[j+1][i] == symbol && board.array[j+2][i] == symbol) {
    			count++;
    		}
    	}
    	return count;
    }
    //Method to count 2 symbols next to each other on a column of the board
    private int count2SymbolColumn(Board board, int i, char symbol) {
    	int count = 0;
    	for(int j=0;j<size-1;j++) {
    		if(board.array[j][i] == symbol && board.array[j+1][i] == symbol) {
    			count++;
    		}
    	}
    	return count;
    }

    //Method checks whether a board is full
    private boolean full(Board board)
    {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (board.array[i][j] == EMPTY)
                   return false;

        return true;
    }

    //Method makes copy of a board
    private Board copy(Board board)
    {
        Board result = new Board(size);      

        for (int i = 0; i < size; i++)       
            for (int j = 0; j < size; j++)
                result.array[i][j] = board.array[i][j];

        return result;                       
    }

    //Method displays a board
    private void displayBoard(Board board) throws IOException
    {
        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++) {
                System.out.print(board.array[i][j] + " | ");
                outfile.write(board.array[i][j] + " | ");
            }
            outfile.write("\n");
            System.out.println();
            for(int j=0;j<4*size;j++) {
            	System.out.print("-");
            	outfile.write("-");
            }
            outfile.write("\n");
            System.out.println();
        }
    }
    
    //method to print player and computer points
    private void displayPoints(Board board) throws IOException {
    		System.out.println("Computer Points: "+ computerPoints(board));
    		System.out.println("Player Points: " + playerPoints(board));
    		outfile.write("Computer Points: " + computerPoints(board) + "\n");
    		outfile.write("Player Points: " + playerPoints(board) + "\n");
    }

  //Method counts whether computer points
    private int computerPoints(Board board)
    {
    	int countTwo = 0;
    	int countThree = 0;
    	//check row for 2 and 3 symbols in a row
    	for(int i=0;i<size;i++) {
    		countTwo+= count2Symbol(board, i, COMPUTER);
    	}
    	for(int i=0;i<size;i++) {
    		countThree+= count3Symbol(board, i, COMPUTER);
    	}
    	//check columns for 2 and 3 symbols in a row
        for (int i = 0; i < size; i++)             
            countTwo += count2SymbolColumn(board, i, COMPUTER);
        
        for (int i = 0; i < size; i++)             
            countThree += count3SymbolColumn(board, i, COMPUTER);   
        return 2 * countTwo + 3 * countThree;
    }                                              

    //Method counts player points
    private int playerPoints(Board board)
    {
    	int countTwo = 0;
    	int countThree = 0;
    	//check row for 2 and 3 symbols in a row
    	for(int i=0;i<size;i++) {
    		countTwo+= count2Symbol(board, i, PLAYER);
    	}
    	for(int i=0;i<size;i++) {
    		countThree+= count3Symbol(board, i, PLAYER);
    	}
    	//check columns for 2 and 3 symbols in a row
        for (int i = 0; i < size; i++)             
            countTwo += count2SymbolColumn(board, i, PLAYER);
        
        for (int i = 0; i < size; i++)             
            countThree += count3SymbolColumn(board, i, PLAYER);   
        return 2 * countTwo + 3 * countThree;             
    }   
    //Method to determine if a board is a draw
    private boolean draw(Board board) {
    	if(playerPoints(board) == computerPoints(board) && full(board))
    		return true;
    	else
    		return false;
    }
    
    //Method evaluates a board
    private int evaluate(Board board)
    {                          
            return computerPoints(board) - playerPoints(board);
    }                                              //utility is difference between computer 
                                                   //and player points if depth limit
                                                   //is reached
}
