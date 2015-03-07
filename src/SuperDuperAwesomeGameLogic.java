import java.util.Arrays;
import java.util.Random;

enum Winner {
    PLAYER1,
    PLAYER2,
    TIE,
    NOT_FINISHED
}

class MiniMaxTree {
    public static final int GAME_NOT_WON = -1337;
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

    class Node {

        private int rootPlayerID;
        int depth;
        int[][] gameBoard;
        Node[] children;

        public Node(int[][] gameBoard, int depth, int rootPlayerID) {
            this.gameBoard = gameBoard;
            this.depth = depth;
            this.rootPlayerID = rootPlayerID;

        }

        public void updateState(int column, int playerID){
            int r = gameBoard[column].length - 1; // number of rows
            //SuperDuperAwesomeGameLogic.printGameboard(gameBoard);
            System.out.println();

            while (gameBoard[column][r] != 0 && r > 0)
                r--;
            if (!full(gameBoard)) {
                gameBoard[column][r] = playerID;
            }

        }

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
                            newGameBoard[c] = Arrays.copyOf(gameBoard[c],nRows);
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
                        System.out.println("Depth " + depth + "column " +i);
                        SuperDuperAwesomeGameLogic.printGameboard(child.gameBoard);
                        this.children[i] = child;
                        this.children[i].createChildren();
                    }
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

    public Node buildTree(int[][] gameBoard, int rootPlayerID){
        Node newRoot = new Node(gameBoard,0, rootPlayerID);
        newRoot.createChildren();
        return newRoot;
    }


	public int miniMax() {
		int low = Integer.MAX_VALUE;
		int high = -Integer.MAX_VALUE;
		int result;
		int choice = -1;
		if (playerID == 1) {
			for (int i = 0; i < root.getChildren().length; i++) {
				result = mini(root.getChildren()[i], i);
				if (result > high) {
					high = result;
					choice = i;
				}
			}

		} else {
			for (int i = 0; i < root.getChildren().length; i++) {
				result = max(root.getChildren()[i], i);
				if (result < low) {
					low = result;
					choice = i;
				}
			}
		}
		return choice;
	}

	private int mini(Node node, int column) {
		int value = this.gameWon(node, column, 1);
		if (value != MiniMaxTree.GAME_NOT_WON)
			return value;
		if (node.getChildren() == null) {
			return heuristic(node.gameBoard);
		}
		int lowest = Integer.MAX_VALUE;
		int heuristic;
		for (int i = 0; i < node.getChildren().length; i++) {
			heuristic = max(node.getChildren()[i], i);
			if (heuristic < lowest)
				lowest = heuristic;
		}
		return lowest;
	}

	private int max(Node node, int column) {
		int value = this.gameWon(node, column, 2);
		if (value != MiniMaxTree.GAME_NOT_WON)
			return value;
		if (node.getChildren() == null) {
			return heuristic(node.gameBoard);
		}
		int highest = -Integer.MAX_VALUE;
		int heuristic;
		for (int i = 0; i < node.getChildren().length; i++) {
			heuristic = mini(node.getChildren()[i], i);
			if (heuristic > highest)
				highest = heuristic;
		}
		return highest;
	}


    /**
     * Checks if the board is full by looking at the top row
     * @param gb gameboard
     * @return true or false
     */
	private boolean full(int[][] gb) {
		for (int i = 0; i < gb[0].length; i++) {
			if (gb[0][i] == 0)
				return false;
		}
		return true;
	}

    public int gameWon(Node node, int column, int currentPlayer) {
		Winner win = winner(node, column, currentPlayer);
		if (win != Winner.NOT_FINISHED) {
			if (win == Winner.TIE) {
				return 0;
			} else {
				if (win == Winner.PLAYER1) {
					return 200;
				} else
					return -200;
			}
		}
		return MiniMaxTree.GAME_NOT_WON;
	}

	// inplement tie == full
	// Winner???
	// current player = give it at mini and max, they know

	private Winner winner(Node node, int column, int currentPlayer) {
		int[][] gameboard = node.gameBoard;
		int row = -17; // if not changed = error
		for (int i = 0; i < gameboard[column].length; i++) {
			if (gameboard[column][i] != 0) {
				row = i; // row = row where last token was dropped
				break;
			}
		}
		if (full(gameboard))
			return Winner.TIE;
		Winner winner = horizontalWinner(row, column, gameboard, currentPlayer);
		if (winner != Winner.NOT_FINISHED)
			return winner;
		winner = verticalWinner(row, column, gameboard, currentPlayer);
		if (winner != Winner.NOT_FINISHED)
			return winner;
		winner = diagonalWinner(row, column, gameboard, currentPlayer);
		if (winner != Winner.NOT_FINISHED)
			return winner;
		return Winner.NOT_FINISHED;
	}

	private Winner diagonalWinner(int row, int column, int[][] gb,
			int currentPlayer) {
		int east = gb.length;
		int north = gb[column].length;

		int succes = 1;
		if (row - 1 >= 0 && column + 1 < east) {
			if (gb[column + 1][row - 1] == currentPlayer) {
				succes += 1;
				if (row - 2 >= 0 && column + 2 < east) {
					if (gb[column + 2][row - 2] == currentPlayer) {
						succes += 1;
						if (row - 3 >= 0 && column + 3 < east) {
							if (gb[column + 3][row - 3] == currentPlayer) {
								succes += 1;
							}
						}
					}
				}
			}
		}
		if (row + 1 < north && column - 1 >= 0) {
			if (gb[column - 1][row + 1] == currentPlayer) {
				succes += 1;
				if (row + 2 < north && column - 2 >= 0) {
					if (gb[column - 2][row + 2] == currentPlayer) {
						succes += 1;
						if (row + 3 < north && column - 3 >= 0) {
							if (gb[column - 3][row + 3] == currentPlayer) {
								succes += 1;
							}
						}
					}
				}
			}
		}

		if (succes >= 4) {
			if (currentPlayer == 1) {
				return Winner.PLAYER1;
			} else {
				return Winner.PLAYER2;
			}
		}

		succes = 1;

		if (row - 1 >= 0 && column - 1 >= 0) {
			if (gb[column - 1][row - 1] == currentPlayer) {
				succes += 1;
				if (row - 2 >= 0 && column - 2 >= 0) {
					if (gb[column - 2][row - 2] == currentPlayer) {
						succes += 1;
						if (row - 3 >= 0 && column - 3 >= 0) {
							if (gb[column - 3][row - 3] == currentPlayer) {
								succes += 1;
							}
						}
					}
				}
			}
		}
		if (row + 1 < north && column + 1 < north) {
			if (gb[column + 1][row + 1] == currentPlayer) {
				succes += 1;
				if (row + 2 < north && column + 2 < north) {
					if (gb[column + 2][row + 2] == currentPlayer) {
						succes += 1;
						if (row + 3 < north && column + 3 < north) {
							if (gb[column + 3][row + 3] == currentPlayer) {
								succes += 1;
							}
						}
					}
				}
			}
		}

		if (succes >= 4) {
			if (currentPlayer == 1) {
				return Winner.PLAYER1;
			} else {
				return Winner.PLAYER2;
			}
		}
		return Winner.NOT_FINISHED;
	}

	private Winner horizontalWinner(int row, int column, int[][] gb,
			int currentPlayer) {
		int sum = 0;
		int previousValue = 0;
		for (int c = 0; c < gb.length; c++) {
			int fieldValue = gb[c][row];
			if (fieldValue != previousValue) {
				sum = 0;
			}
			previousValue = fieldValue;
			if (fieldValue == 1)
				sum += 1;
			else if (fieldValue == 2)
				sum -= 1;
			if (sum == 4) {
				return Winner.PLAYER1;

			} else if (sum == -4) {
				return Winner.PLAYER2;

			}
		}
		return Winner.NOT_FINISHED;

	}

	private Winner verticalWinner(int row, int column, int[][] gb,
			int currentPlayer) {
		if (row > gb[column].length - 4)
			return Winner.NOT_FINISHED; // vertical win not possible

		for (int i = row; i < gb[column].length && i < 4 + row; i++) {
			if (gb[column][i] != currentPlayer) {
				return Winner.NOT_FINISHED;
			}
		}
		if (currentPlayer == 1)
			return Winner.PLAYER1;
		else
			return Winner.PLAYER2;
	}


	public int heuristic(int[][] gameBoard) {

		int valueV = valueVertically(gameBoard);
		int valueH = valueHorizontally(gameBoard);
		int valueD = valueDigonally(gameBoard);

		return valueV + valueH + valueD;
	}

	private int valueVertically(int[][] gameBoard) {
		int sum;
		int columns = gameBoard.length;
		int rows = gameBoard[0].length - 1; // -1 since we want the array
											// indices from 0 to 5
		sum = 0;
		for (int c = 0; c < columns; c++) {
			// sum = 0;
			int previousValue = 1000;
			int value = 1000;
			for (int r = rows; r >= 0; r--) {
				int fieldValue = gameBoard[c][r];
				if (fieldValue == 0) {
					break;
				}
				if (fieldValue == 1) {
					value = 1;
				}
				if (fieldValue == 2) {
					value = -1;
				}
				sum = +value;
				if (previousValue != value) {
					sum = value;
					if (r < 3) {
						sum = 0;
						break;
					}
				}
				previousValue = fieldValue;

			}
			if (playerID == 1 && sum == -3) {
				return -100;
			}
			if (playerID == 2 && sum == 3) {
				return 100;
			}
			sum = +sum;
		}
		return sum;
	}

	private int valueHorizontally(int[][] gameBoard) {

		int sum = 0;
		int columns = gameBoard.length;
		int rows = gameBoard[0].length;
		for (int r = rows - 1; r >= 0; r--) { // want to start from array index
												// 0
			int value = 1000;
			for (int c = 3; c < columns; c++) {
				int goodFor = -15;
				boolean first = true;
				for (int f = c - 3; f <= c; f++) { // check 4 continuous array
													// indices, if both blue and
													// red present skip
					int fieldValue = gameBoard[f][r];
					if (fieldValue == 0) {
						continue;
					}
					if (first) {
						goodFor = fieldValue;
						first = false;
					} else {
						if (goodFor != fieldValue) {
							break;
						}
						if (fieldValue == 1) {
							value += fieldValue;
						} else if (fieldValue == 2) {
							value += -1;
						}
					}
					sum += value;
				}
				sum += sum;
			}
		}
		return sum;
	}

	private int valueDigonally(int[][] gameBoard) {
		int sum = 0;
		int columns = gameBoard.length;
		int rows = gameBoard[0].length;

		for (int r = 0; r < rows - 3; r++) {
			int value = 1000;
			for (int c = 0; c < columns - 3; c++) {
				int goodFor = -15;
				boolean first = true;
				for (int i = 0; i < 4; i++) {
					int fieldValue = gameBoard[c + i][r + i]; // add i to both
																// to move
																// diagonally
					if (fieldValue == 0) {
						continue;
					}
					if (first) {
						goodFor = fieldValue;
						first = false;
					} else {
						if (goodFor != fieldValue) {
							break;
						}
						if (fieldValue == 1) {
							value += fieldValue;
						} else if (fieldValue == 2) {
							value += -1;
						}
					}
					sum += value;
				}
				sum += sum;
			}
		}

		for (int r = 0; r < rows - 3; r++) {
			int value = 1000;
			for (int c = 6; c >= columns - 4; c--) {
				int goodFor = -15;
				boolean first = true;
				for (int i = 0; i < 4; i++) {
					int fieldValue = gameBoard[c - i][r + i]; // add i to both
																// to move
																// diagonally
					if (fieldValue == 0) {
						continue;
					}
					if (first) {
						goodFor = fieldValue;
						first = false;
					} else {
						if (goodFor != fieldValue) {
							break;
						}
						if (fieldValue == 1) {
							value += fieldValue;
						} else if (fieldValue == 2) {
							value += -1;
						}
					}
					sum += value;
				}
				sum += sum;
			}
		}

		return sum;
	}
}

public class SuperDuperAwesomeGameLogic implements IGameLogic {
	private int x = 0;
	private int y = 0;
	private int playerID;
	private int currentPlayer = 1;
	private int lastInsertedColumn = -1337;
	private Winner winner = Winner.NOT_FINISHED;

	private int[][] gameBoard;
	private MiniMaxTree mm;

	static public void printGameboard(int[][] gb) {
		for (int i = 0; i < gb[0].length; i++) {
			for (int j = 0; j < gb.length; j++) {
				System.out.print(gb[j][i]);
			}
			System.out.println();
		}
	}

	public SuperDuperAwesomeGameLogic() {
		// We initialize the instantiated object using the initializeGame method
	}

	// Random r = new Random();
	// return r.nextInt(20) - 10;

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
		return this.winner;
		/*
		 * We use the following codes for game status: NOT_FINISHED = -1 TIE = 0
		 * PLAYER1 WIN = 1 PLAYER2 WIN = 2
		 */

		/*
		 * int winner = -1; // initialized with -1 so that we skip the //
		 * if-statements and return NOT_FINISHED
		 * 
		 * winner = checkBoardFull(gameBoard); if (winner == 0) { return
		 * Winner.TIE; }
		 * 
		 * winner = checkHorizontally(gameBoard); if (winner == 1) { return
		 * Winner.PLAYER1; } if (winner == 2) { return Winner.PLAYER2; } winner
		 * = checkDiagonally(gameBoard); if (winner == 1) { return
		 * Winner.PLAYER1; } if (winner == 2) { return Winner.PLAYER2; }
		 * 
		 * winner = checkVertically(gameBoard);
		 * 
		 * if (winner == 1) { return Winner.PLAYER1; } if (winner == 2) { return
		 * Winner.PLAYER2; }
		 * 
		 * return Winner.NOT_FINISHED;
		 */
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
		currentPlayer = playerID;
		lastInsertedColumn = column;
		System.out.println("first");
		printGameboard(gameBoard);
		
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