import java.util.Arrays;
import java.util.Random;

public enum Winner {
	PLAYER1, PLAYER2, TIE, NOT_FINISHED
}

class Node {

	int[][] gb;
	private Node[] children;

	public Node(int i, int[][] gb, int playerID) {
		this.gb = Arrays.copyOf(gb, gb.length);
		this.insertCoin(i, playerID);
	}

	public Node(int[][] gb) {
		this.gb = Arrays.copyOf(gb, gb.length);
	}

	public void insertCoin(int i, int playerID) {
		if (i == -1) {
			// todo throw exception
		}
		if (gb[i][0] != 0) {
			// todo throw exception
		}
		int r = gb[i].length - 1;
		// SuperDuperAwesomeGameLogic.printGameboard(gb);

		while (r>0 && gb[i][r] != 0) {
			r--;
		}
		gb[i][r] = playerID;
	}

	public Node[] getChildren() {
		return children;
	}

	public void setChildren(Node[] children) {
		this.children = children;
	}

}

class MiniMaxTree {
	public static final int GAME_NOT_WON = -1337;
	Node root;
	int playerID;

	public MiniMaxTree(int[][] gameboard, int playerID) {
		root = new Node(gameboard);
		this.playerID = playerID;
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
			return heuristic(node.gb);
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
			return heuristic(node.gb);
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
		int[][] gameboard = node.gb;
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
				if (row - 2 >= 0 && column - 1 >= 0) {
					if (gb[column - 2][row - 2] == currentPlayer) {
						succes += 1;
						if (row - 3 >= 0 && column - 1 >= 0) {
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

	public void expandTree(int[][] gb, int playerID) {
		// GOGOGO CHRISTIAN!
		if (root.getChildren() == null) {
			root.setChildren(new Node[7]);
		}
		for (int i = 0; i < root.getChildren().length; i++) {
			root.getChildren()[i] = new Node(i, gb, playerID);

		}

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

	private int[][] gb;
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
	 * @param columns
	 *            The number of columns in the game board
	 * @param rows
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
		this.gb = new int[x][y]; // initialized with zeros by default
		this.mm = new MiniMaxTree(gb, playerID);
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
				if (fieldValue == 1)
					sum += 1;
				else if (fieldValue == 2)
					sum -= 1;
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
				if (fieldValue == 1)
					sum += 1;
				else if (fieldValue == 2)
					sum -= 1;
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
					if (fieldValue == 1)
						sum += 1;
					else if (fieldValue == 2)
						sum -= 1;
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
			for (int c = 4; c < columns - 3; c++) {
				sum = 0;
				int previousValue = 0;
				for (int i = 3; i < 4; i--) {
					int fieldValue = gameBoard[c - i][r - i]; // add i to both
																// to move
																// diagonally
					if (fieldValue != previousValue) {
						sum = 0;
					}
					previousValue = fieldValue;
					if (fieldValue == 1)
						sum += 1;
					else if (fieldValue == 2)
						sum -= 1;
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
		currentPlayer = playerID;
		lastInsertedColumn = column;
		System.out.println("first");
		printGameboard(gb);
		
		// Iterate through rows in column until we find empty spot and place
		// coin (code sakset fra FourConnectGUI)
		if (column == -1) {
			// todo throw exception
		}
		if (gb[column][0] != 0) {
			// todo throw exception
		}
		int r = gb[column].length - 1;
		while (gb[column][r] != 0)		//r>0 && 
			{
			System.out.println(r+": " +gb[column][r]);
			r--;
			}
		gb[column][r] = playerID;
		
		System.out.println("sec");
		printGameboard(gb);
		
		mm.expandTree(gb, currentPlayer);
		
		System.out.println("third");
		printGameboard(gb);
		
		mm.root = mm.root.getChildren()[column];
		
		System.out.println("fourth");
		printGameboard(gb);
		
		changeWinner();
		
		System.out.println("femte");
		printGameboard(gb);
	}

	private void changeWinner() {
		int column = lastInsertedColumn;
		int row = -1; // if not changed = error
		for (int i = 0; i < gb[column].length; i++) {
			if (this.gb[column][i] != 0) {
				row = i; // row = row where last token was dropped
				break;
			}
		}
		
		if (horizontalWinner(row, column))
			return;
		if (verticalWinner(row, column))
			return;
		diagonalWinner(row, column);
		/*
		 * int goodforPlayer = -44; for (int f = 0; f < gameBoard.length; f++) {
		 * if (gameBoard[f][0] == 0 ) continue; (goodforPlayer == 44) ?
		 * goodforPlayer=gameBoard[f][0] : (goodforPlayer==gameBoard[f][0] ?
		 * continue : break); }
		 */

	}

	private void diagonalWinner(int row, int column) {
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
				winner = Winner.PLAYER1;
			} else {
				winner = Winner.PLAYER2;
			}
			return;
		}

		succes = 1;

		if (row - 1 >= 0 && column - 1 >= 0) {
			if (gb[column - 1][row - 1] == currentPlayer) {
				succes += 1;
				if (row - 2 >= 0 && column - 1 >= 0) {
					if (gb[column - 2][row - 2] == currentPlayer) {
						succes += 1;
						if (row - 3 >= 0 && column - 1 >= 0) {
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
				winner = Winner.PLAYER1;
			} else {
				winner = Winner.PLAYER2;
			}
		}
	}

	private boolean horizontalWinner(int row, int column) {
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
				winner = Winner.PLAYER1;
				return true;
			} else if (sum == -4) {
				winner = Winner.PLAYER2;
				return true;
			}
		}
		return false;

	}

	private boolean verticalWinner(int row, int column) {
		if (row > gb[column].length - 4)
			return false; // vertical win not possible

		for (int i = row; i < gb[column].length && i < 4 + row; i++) {
			if (gb[column][i] != currentPlayer) {
				return false;
			}
		}
		if (currentPlayer == 1)
			winner = Winner.PLAYER1;
		else
			winner = Winner.PLAYER2;
		return true;
	}

	/**
	 * Calculates the next move This is where you should implement/call your
	 * heuristic evaluation functions etc.
	 */
	public int decideNextMove() {
		mm.expandTree(gb, currentPlayer);
		// return 0;
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