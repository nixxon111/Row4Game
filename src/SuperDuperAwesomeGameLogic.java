import java.util.Arrays;
import java.util.Random;

class MiniMaxTree {

	Node root;

	public MiniMaxTree(int[][] gameboard) {
		root = new Node(gameboard);
	}

	class Node {

		int[][] gb;
		private Node[] nodes;

		public Node(int[][] gb, int i, int playerID) {
			nodes = new Node[7];
			for (int j = 0; j < gb.length; j++) {
				insertCoin(gb, i, playerID);
			}
			
			this.gb = gb;
		}

		public Node(int[][] gameboard) {
			this.gb = Arrays.copyOf(gameboard, gameboard.length);
			nodes = new Node[gameboard.length];
			int playerId=1;
			for (int i = 0; i < gameboard.length; i++) {
				nodes[i] = new Node(this.gb, i, (playerId++%2)+1);
			}
		}

	}

	public int MiniMax(int[][] gb) {
		int alpha = -9999;
		int beta = 9999;
		int bestScore = -Integer.MAX_VALUE;
		int bestMove = -1;
		int depth = 10;
			
			alpha = Math.max(alpha,
					miniMaxMini(root, alpha, beta));
			if (alpha > bestScore) {
				//bestMove = i;
				bestScore = alpha;
			}
		return bestMove;

	}

	public void insertCoin(int[][] gb, int i, int playerID) {
		if (i == -1) {
			// todo throw exception
		}
		if (gb[i][0] != 0) {
			// todo throw exception
		}
		int r = gb[i].length - 1;
		while (gb[i][r] != 0)
			r--;
		gb[i][r] = playerID;
	}

	/**
	 * 
	 * @param i
	 *            the child node we go into
	 * @param depth
	 *            = remaining depth to travel
	 * @param alpha
	 * @return
	 */
	private int miniMaxMini(Node node, int alpha, int beta) {
		if (node.nodes[0] == null) {
			for (int i=0;i<node.nodes.length;i++) {
				node.nodes[i] = new Node(node.gb, i, 2);
			}
		}
		for (int i = 0; i < node.nodes.length; i++) {
			alpha = Math.max(alpha, miniMaxMax(root.nodes[i], alpha, beta));

			if (alpha >= beta) {
				return beta;
			}
		}
		return alpha;

	}

	private int miniMaxMax(Node node, int alpha, int beta) {
		if (node.nodes[0] == null) {
			for (int i=0;i<node.nodes.length;i++) {
				node.nodes[i] = new Node(node.gb, i, 1);
			}
		}
		if (node.nodes == null) {
			return SuperDuperAwesomeGameLogic.heuristic(node.gb);
		}
		for (int i = 0; i < node.nodes.length; i++) {

			beta = Math.min(beta, miniMaxMini(root.nodes[i], alpha, beta));

			if (alpha >= beta) {
				return alpha;
			}
		}
		return beta;
	}
}

public class SuperDuperAwesomeGameLogic implements IGameLogic {
	private int x = 0;
	private int y = 0;
	private int playerID;
	int count = 0;

	private int[][] gameBoard;
	MiniMaxTree mm;

	public SuperDuperAwesomeGameLogic() {
		this.mm = new MiniMaxTree(gameBoard);
		// We initialize the instantiated object using the initializeGame method
	}

	public static int heuristic(int[][] gb) {
		Random r = new Random();
		
		return r.nextInt(20)-10;
	}

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
		this.gameBoard = new int[x][y]; // initialized with zeros by default

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

		/*
		 * winner = checkVertically(gameBoard); if (winner == 0) {return
		 * Winner.TIE;} if (winner == 1) {return Winner.PLAYER1;} if (winner ==
		 * 2) {return Winner.PLAYER2;} winner = checkHorizontally(gameBoard); if
		 * (winner == 0) {return Winner.TIE;} if (winner == 1) {return
		 * Winner.PLAYER1;} if (winner == 2) {return Winner.PLAYER2;} winner =
		 * checkDiagonally(gameBoard); if (winner == 0) {return Winner.TIE;} if
		 * (winner == 1) {return Winner.PLAYER1;} if (winner == 2) {return
		 * Winner.PLAYER2;} winner = checkBoardFull(gameBoard);
		 */
		if (winner == 0) {
			return Winner.TIE;
		}
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
				sum += fieldValue;
				if (sum == 4) {
					return 1;
				} // sum == 4 since player1 tokens has a value of 1 and it takes
					// 4 to win.
				if (sum == 8) {
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
			System.out.println("sum:" + sum);
			for (int c = 0; c < columns; c++) {
				int fieldValue = gameBoard[c][r];
				if (fieldValue != previousValue) {
					sum = 0;
				}
				previousValue = fieldValue;
				sum += fieldValue;
				System.out.print(fieldValue);
				if (sum == 4) {
					return 1;
				} // sum == 4 since player1 tokens has a value of 1 and it takes
					// 4 to win.
				if (sum == 8) {
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
					sum += fieldValue;
					if (sum == 4) {
						return 1;
					} // sum == 4 since player1 tokens has a value of 1 and it
						// takes 4 to win.
					if (sum == 8) {
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
	}

	/**
	 * Calculates the next move This is where you should implement/call your
	 * heuristic evaluation functions etc.
	 */
	public int decideNextMove() {
		// Random ra = new Random();
		// int x = ra.nextInt(7);
		// return x;
		return mm.MiniMax(gameBoard);
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