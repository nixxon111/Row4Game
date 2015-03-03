import java.util.Random;


public class SuperDuperAwesomeGameLogic implements IGameLogic {
    private int x = 0;
    private int y = 0;
    private int playerID;
    int count=0;
    
    public SuperDuperAwesomeGameLogic() {
    	
    }

	/**
     * Creates a new empty game board of the specified dimensions
     * and indicates the ID of the player. 
     * This method will be called from the main function. 
     * @param columns The number of columns in the game board
     * @param rows The number of rows in the game board
     * @param playerID 1 = blue (player1), 2 = red (player2)
     */
    public void initializeGame(int x, int y, int playerID) {
        this.x = x;
        this.y = y;
        this.playerID = playerID;
        //TODO Write your implementation for this method
    }
	
    
    public Winner gameFinished() {
    	if (count++>=20) {
    		return Winner.PLAYER1;
    	}
        return Winner.NOT_FINISHED;
    }

    /**
     * Notifies that a token/coin is put in the specified column of 
     * the game board. 
     * @param column The column where the coin is inserted.      
     * @param playerID The ID of the current player.
     */
    public void insertCoin(int column, int playerID) {
        //TODO Write your implementation for this method	
    }

    /**
     * Calculates the next move  This is where you should 
     * implement/call your heuristic evaluation functions etc. 
     */
    public int decideNextMove() {
        Random ra = new Random();
        int x = ra.nextInt(7);
        return x;
    }

}

//TODO: something
// Heuristic (count 2's and 3s, possibilites (Are they close to completion? ruin theirs)
// decision tree
// finished
// decide move (should create tree (from board) go through, call heuristic on node X, return highest value and translate to a colum
// constructor: initialize board + insert coin method