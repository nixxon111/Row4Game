import java.util.Arrays;
import java.util.Random;

class MiniMaxTree {

    static final int maxDepth = 3;
	static int count = 300;

	Node root;
	int playerID;

	public MiniMaxTree(int[][] gameboard, int playerID) {
		root = new Node(gameboard, 0, playerID);
		this.playerID = playerID;
        initializeTree();
	}

    private void initializeTree(){
        root.createChildren();
    }

    private static void updateChildren(Node node, int column, int playerID){
        if (node.depth < maxDepth) {
            for (Node child : node.children) {
                updateChildren(child, column, playerID);
            }
        }
        node.updateState(column,playerID);
    }

    public Node buildTree(int[][] gameBoard, int rootPlayerID){
        Node newRoot = new Node(gameBoard,0, rootPlayerID);
        newRoot.createChildren();
        return newRoot;
    }


	public int miniMax() {
		int low=Integer.MAX_VALUE;
		int high = -Integer.MAX_VALUE;
		int result;
		int choice = -1;
		if (playerID==1) {
			for (int i = 0; i < root.getChildren().length; i++) {
				result = mini(root.getChildren()[i]);
				if (result>high) {
					high = result;
					choice = i;
				}
			}

		} else {
			for (int i = 0; i < root.getChildren().length; i++) {
				result = max(root.getChildren()[i]);
				if (result<low) {
					low = result;
					choice = i;
				}
			}
		}
		System.out.println("choice: "+ choice);
		return choice;
	}

	private int mini(Node node) {
		if (node.getChildren()== null ) {
			return SuperDuperAwesomeGameLogic.heuristic(node.gameBoard);
		}
		int lowest = Integer.MAX_VALUE;
		int heuristic;
		for (int i = 0; i < node.getChildren().length; i++) {
			heuristic = mini(node.getChildren()[i]);
			if (heuristic < lowest) lowest = heuristic;
		}

		return lowest;
	}

	private int max(Node node) {
		if (node.getChildren()== null ) {
			return SuperDuperAwesomeGameLogic.heuristic(node.gameBoard);
		}
		int highest = -Integer.MAX_VALUE;
		int heuristic;
		for (int i = 0; i < node.getChildren().length; i++) {
			heuristic = mini(node.getChildren()[i]);
			if (heuristic > highest) highest = heuristic;
		}

		return highest;
	}


    /**
     * Checks if the board is full by looking at the top row
     * @param gb
     * @return
     */
	private boolean full(int[][] gb) {

        for (int i = 0; i < gb.length; i++) {
            if (gb[0][i] == 0) {
                return false;
            }
        }
        return true;
    }

    public void expandTree(int column){
        root = root.children[column];
        root.decreaseDepthByOne();
        root.expandTree();
    }


    class Node {

        private int rootPlayerID;
        private int depth;
        int[][] gameBoard;
        Node[] children;


        public Node(int[][] gameBoard, int depth) {
            this.gameBoard = gameBoard;
            this.depth = depth;

        }

        public Node(int[][] gameBoard, int depth, int rootPlayerID) {
            this.gameBoard = gameBoard;
            this.depth = depth;
            this.rootPlayerID = rootPlayerID;

        }

        public void updateState(int column, int playerID){
            int r = gameBoard[column].length - 1; // number of rows
            SuperDuperAwesomeGameLogic.printGameboard(gameBoard);
            System.out.println();

            while (gameBoard[column][r] != 0 && r > 0)
                r--;
            if (!full(gameBoard)) {
                gameBoard[column][r] = playerID;
            }

        }

//        public void createChildren(){
//            int nColumns = gameBoard.length;
//            int nRows = gameBoard[0].length;
//            int depth = this.depth;
//            if (depth < maxDepth) {
//                if (this.children == null){
//                    this.children = new Node[nColumns];
//
//                    //Create children
//                    for (int i = 0; i < nColumns; i++) {
//                        //Carry over parent gameBoard state (remember to make new object)
//                        int[][] newGameBoard = new int[nColumns][nRows];
//                        for (int c = 0; c < nColumns; c++) {
//                            newGameBoard[c] = Arrays.copyOf(gameBoard[c],nColumns);
//                        }
//
//                        // Make child  and update its state
//                        Node child = new Node(newGameBoard, depth + 1);
//                        child.updateState(i,playerID);
//                        this.children[i] = child;
//                        this.children[i].createChildren();
//                    }
//                }
//            }
//        }

        public void createChildren(){
            int nColumns = gameBoard.length;
            int nRows = gameBoard[0].length;
            int depth = this.depth;
            if (depth < maxDepth) {
                if (this.children == null){
                    this.children = new Node[nColumns];

                    //Create children
                    for (int i = 0; i < nColumns; i++) {
                        //Carry over parent gameBoard state (remember to make new object)
                        int[][] newGameBoard = new int[nColumns][nRows];
                        for (int c = 0; c < nColumns; c++) {
                            newGameBoard[c] = Arrays.copyOf(gameBoard[c],nColumns);
                        }

                        // Find ID of player in next round (opposite of current)
                        int nextPlayerID = 0;
                        switch (rootPlayerID){
                            case 1: nextPlayerID = 2;
                                break;
                            case 2: nextPlayerID = 1;
                                break;
                        }

                        // Make child  and update its state
                        Node child = new Node(newGameBoard, depth + 1, nextPlayerID);
                        child.updateState(i, nextPlayerID);
                        this.children[i] = child;
                        this.children[i].createChildren();
                    }
                }
            }
        }

        public void decreaseDepthByOne(){
            int oldDepth = this.depth;
            if (oldDepth <= maxDepth) {
                int newDepth = this.depth - 1;
                if (oldDepth < maxDepth) { // because when depth=maxDepth there are no children
                    for (Node child : children) {
                        child.decreaseDepthByOne();
                    }
                }
                this.depth = newDepth;
            }
        }

        public void expandTree(){
            int nColumns = gameBoard.length;
            int nRows = gameBoard[0].length;
            int depth = this.depth;
            if (children != null) {
                for (Node child : children) {
                    child.expandTree();
                }
            } else {

                this.children = new Node[nColumns];

                //Create children
                for (int i = 0; i < nColumns; i++) {
                    //Carry over parent gameBoard state (remember to make new object)
                    int[][] newGameBoard = new int[nColumns][nRows];
                    for (int c = 0; c < nColumns; c++) {
                        newGameBoard[c] = Arrays.copyOf(gameBoard[c],nColumns);
                    }

                    // Make child  and update its state
                    Node child = new Node(newGameBoard, depth + 1);
                    child.updateState(i,playerID);
                    this.children[i] = child;
                }


            }

        }

        public Node[] getChildren() {
            return children;
        }

        public void setChildren(Node[] children) {
            this.children = children;
        }

    }

}

public class SuperDuperAwesomeGameLogic implements IGameLogic {
	private int x = 0;
	private int y = 0;
	private int playerID;
	int count = 0;

	private int[][] gameBoard;
	private MiniMaxTree mm;

	static public void printGameboard(int[][] gb) {
		for (int i = 0; i < gb.length; i++) {
			for (int j = 0; j < gb[0].length; j++) {
				System.out.print(gb[i][j]);
			}
			System.out.println();
		}
	}

	public SuperDuperAwesomeGameLogic() {
		// We initialize the instantiated object using the initializeGame method
	}

	public static int heuristic(int[][] gb) {
		Random r = new Random();

		return r.nextInt(20) - 10;
	}

	/**
	 * Creates a new empty game board of the specified dimensions and indicates
	 * the ID of the player. This method will be called from the main function.
	 * 
	 * @param x
	 *            The number of columns in the game board
	 * @param y
	 *            The number of rows in the game board
	 * @param playerID
	 *            1 = blue (player1), 2 = red (player2)
	 */
	public void initializeGame(int x, int y, int playerID) {
		this.x = x;
		this.y = y;
		this.playerID = playerID;

		// Create game board in the correct dimensions where x = columns and y =
		// rows
		// (why this has to be opposite to linear algebra beats me)
		this.gameBoard = new int[x][y]; // initialized with zeros by default

		this.mm = new MiniMaxTree(gameBoard, playerID);

	}

	/**
	 * Checks if the game is finished or not.
	 * 
	 * @return the winner of the game
	 */
	public Winner gameFinished() {
		/*
		 * We use the following codes for game status: NOT_FINISHED = -1 TIE = 0
		 * PLAYER1 WIN = 1 PLAYER2 WIN = 2
		 */

		int winner = -1; // initialized with -1 so that we skip the
							// if-statements and return NOT_FINISHED

		winner = checkVertically(gameBoard);
		if (winner == 0) {
			return Winner.TIE;
		}
		winner = checkHorizontally(gameBoard);
		if (winner == 1) {
			return Winner.PLAYER1;
		}
		if (winner == 2) {
			return Winner.PLAYER2;
		}
		winner = checkDiagonally(gameBoard);
		if (winner == 1) {
			return Winner.PLAYER1;
		}
		if (winner == 2) {
			return Winner.PLAYER2;
		}
		winner = checkBoardFull(gameBoard);

		if (winner == 1) {
			return Winner.PLAYER1;
		}
		if (winner == 2) {
			return Winner.PLAYER2;
		}

		return Winner.NOT_FINISHED;
	}

	private int checkBoardFull(int[][] gameBoard) {

		// Iterates through the entire game board looking for a field with value
		// zero
		int columns = gameBoard.length;
		int rows = gameBoard[0].length;
		for (int c = 0; c < columns; c++) {
			for (int r = 0; r < rows; r++) {
				int fieldValue = gameBoard[c][r];
				if (fieldValue == 0) {
					return -1;
				}
			}
		}
		return 0;
	}

	private int checkVertically(int[][] gameBoard) {

		int sum;
		int columns = gameBoard.length;
		int rows = gameBoard[0].length;
		for (int c = 0; c < columns; c++) {
			sum = 0;
			int previousValue = 0;
			for (int r = 0; r < rows; r++) {
				int fieldValue = gameBoard[c][r];
				if (fieldValue != previousValue) {
					sum = 0;
				}
				previousValue = fieldValue;
				if (fieldValue == 1) sum += 1;
				else if (fieldValue == 2) sum -= 1;
				if (sum == 4) {
					return 1;
				} // sum == 4 since player1 tokens has a value of 1 and it takes
					// 4 to win.
				if (sum == -4) {
					return 2;
				} // sum == 8 since player2 tokens has a value of 2 and it takes
					// 4 to win.
			}
		}

		return -1;
	}

	private int checkHorizontally(int[][] gameBoard) {
		int sum;
		int columns = gameBoard.length;
		int rows = gameBoard[0].length;
		for (int r = 0; r < rows; r++) {
			sum = 0;
			int previousValue = 0;
			for (int c = 0; c < columns; c++) {
				int fieldValue = gameBoard[c][r];
				if (fieldValue != previousValue) {
					sum = 0;
				}
				previousValue = fieldValue;
				if (fieldValue == 1) sum += 1;
				else if (fieldValue == 2) sum -= 1;
				if (sum == 4) {
					return 1;
				} // sum == 4 since player1 tokens has a value of 1 and it takes
					// 4 to win.
				if (sum == -4) {
					return 2;
				} // sum == 8 since player2 tokens has a value of 2 and it takes
					// 4 to win.
			}
		}

		return -1;
	}

	private int checkDiagonally(int[][] gameBoard) {
		int sum;
		int columns = gameBoard.length;
		int rows = gameBoard[0].length;

		for (int r = 0; r < rows - 3; r++) {
			for (int c = 0; c < columns - 3; c++) {
				sum = 0;
				int previousValue = 0;
				for (int i = 0; i < 4; i++) {
					int fieldValue = gameBoard[c + i][r + i]; // add i to both
																// to move
																// diagonally
					if (fieldValue != previousValue) {
						sum = 0;
					}
					previousValue = fieldValue;
					if (fieldValue == 1) sum += 1;
					else if (fieldValue == 2) sum -= 1;
					if (sum == 4) {
						return 1;
					} // sum == 4 since player1 tokens has a value of 1 and it
						// takes 4 to win.
					if (sum == -4) {
						return 2;
					} // sum == 8 since player2 tokens has a value of 2 and it
						// takes 4 to win.
				}
			}

		}

		return -1;
	}

	/**
	 * Notifies that a token/coin is put in the specified column of the game
	 * board.
	 * 
	 * @param column
	 *            The column where the coin is inserted.
	 * @param playerID
	 *            The ID of the current player.
	 */
	public void insertCoin(int column, int playerID) {

		// Iterate through rows in column until we find empty spot and place
		// coin (code sakset fra FourConnectGUI)
		if (column == -1) {
			// todo throw exception
		}
		if (gameBoard[column][0] != 0) {
			// todo throw exception
		}
		int r = gameBoard[column].length - 1;
		while (gameBoard[column][r] != 0)
			r--;
		gameBoard[column][r] = playerID;
        mm.root = mm.buildTree(gameBoard,playerID);

		//mm.expandTree(column);
		//mm.root = mm.root.getChildren()[column];
		
	}

	/**
	 * Calculates the next move This is where you should implement/call your
	 * heuristic evaluation functions etc.
	 */
	public int decideNextMove() {
		//mm.expandTree(0);
		 //return 0;
		return mm.miniMax();
		 
	}

}

// TODO: something
// Heuristic (count 2's and 3s, possibilites (Are they close to completion? ruin
// theirs)
// decision tree
// finished
// decide move (should create tree (from board) go through, call heuristic on
// node X, return highest value and translate to a colum
// constructor: initialize board + insert coin method