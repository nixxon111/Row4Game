
public class SuperDuperAwesomeGameLogic implements IGameLogic {
    private int x = 0;
    private int y = 0;
    private int playerID;

    private int[][] gameBoard;
    
    public SuperDuperAwesomeGameLogic() {
    	  // We initialize the instantiated object using the initializeGame method
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

        // Create game board in the correct dimensions where x = columns and y = rows
        // (why this has to be opposite to linear algebra beats me)
        this.gameBoard = new int[x][y]; // initialized with zeros by default

    }
	
    
    public Winner gameFinished() {
        //TODO Write your implementation for this method

        int winner = -1;

        if (checkVertically(gameBoard)) {}
        if (checkHorizontally(gameBoard)) {}
        if (checkDiagonally(gameBoard)) {}
        if (checkBoardFull(gameBoard)) {}

        if (winner == 0) {return Winner.TIE;}
        if (winner == 1) {return Winner.PLAYER1;}
        if (winner == 2) {return Winner.PLAYER2;}

        return Winner.NOT_FINISHED;
    }

    private boolean checkHorizontally(int[][] gameBoard) {
        int sum = 0;
        int col = gameBoard.length;
        int row = gameBoard[0].length;
        for (int c = 0; c < col; c++) {
            for (int r = 0; r < row; r++) {

            }
        }

        return false;
    }

    /**
     * Notifies that a token/coin is put in the specified column of 
     * the game board. 
     * @param column The column where the coin is inserted.      
     * @param playerID The ID of the current player.
     */
    public void insertCoin(int column, int playerID) {

        //Iterate through rows in column until we find empty spot and place coin (code sakset fra FourConnectGUI)
        if(column == -1) {
            //todo throw exception
        }
        if (gameBoard[column][0] != 0) {
            //todo throw exception
        }
        int r = gameBoard[column].length-1;
        while(gameBoard[column][r]!=0) r--;
        gameBoard[column][r]=playerID;
    }

    /**
     * Calculates the next move  This is where you should 
     * implement/call your heuristic evaluation functions etc. 
     */
    public int decideNextMove() {
        //TODO Write your implementation for this method
        return 0;
    }

}

//TODO: something
// Heuristic (count 2's and 3s, possibilites (Are they close to completion? ruin theirs)
// decision tree
// finished
// decide move (should create tree (from board) go through, call heuristic on node X, return highest value and translate to a colum
// constructor: initialize board + insert coin method