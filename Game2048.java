import java.util.Scanner;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;
import java.util.ArrayList;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.geometry.HPos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.Scanner;
import java.io.*;
import java.util.Optional;
import java.util.Random;

/**
 * 
 * @author Chen Hsun Lee
 */
public class Game2048 extends Application {
	public static ArrayList<Label> one;
	public static ArrayList<Label> two;
	public static ArrayList<Label> three;
	public static ArrayList<Label> four;
	public static Board board;
	public static Game2048 game;
	public static StackPane[][] cell;
	public static Label bestScore2;
	public static Label score2;

	protected static enum Direction {
		UP, DOWN, LEFT, RIGHT
	};

	protected static class Board {
		private int[][] board = null;
		private int boardSize = 0;
		private Random rand;
		public static int score = 0;
		public static int bestScore = 0;

		/**
		 * Creates a default board of size [4][4]
		 */
		public Board() {
			this(4);
		}

		/**
		 * Creates the board of size [size][size]
		 *
		 * @param size
		 *            - the size of the board
		 */
		public Board(int size) {
			boardSize = size;
			board = new int[boardSize][boardSize];
			rand = new Random();

			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
					board[i][j] = 0;
				}
			}
		}

		public int getScore() {
			return this.score;
		}

		/**
		 * initialize board for testing purposes
		 *
		 * @param board
		 *            2D array representing the 2048 board
		 */
		public void setBoard(int[][] board) {
			this.board = board;
		}

		/**
		 * return an individual row translated based on direction, e.g. [1, 2, 3
		 * 4, 5, 6 7, 8, 9] getRow(LEFT, 0) => [1,2,3] getRow(UP, 0) => [1,4,7]
		 * getRow(RIGHT,0) => [3,2,1] getRow(DOWN, 1) => [8,5,2]
		 *
		 * @param dir
		 *            - the direction the row will be translated to
		 * @param rowNumber
		 *            - the row number
		 */
		public int[] getRow(Direction dir, int rowNumber) {
			int[] row = new int[board.length];
			int i = 0;

			switch (dir) {
			case LEFT:
				for (i = 0; i < boardSize; i++) {
					row[i] = board[rowNumber][i];
				}
				break;

			case RIGHT:
				for (i = 0; i < boardSize; i++) {
					row[i] = board[rowNumber][boardSize - 1 - i];
				}
				break;

			case UP:
				for (i = 0; i < boardSize; i++) {
					row[i] = board[i][rowNumber]; // Wrong order
				}
				break;

			case DOWN:
				for (i = 0; i < boardSize; i++) {
					row[i] = board[boardSize - 1 - i][rowNumber];
				}
				break;

			default:
				System.out.println("Invalid direction provided");
				break;
			}

			return row;
		}

		/**
		 * put an individual row translated based on direction into the board,
		 * e.g. [1, 2, 3 4, 5, 6 7, 8, 9] putRow(LEFT, [3,4,5], 0) => board =
		 * [3,4,5 4,5,6 7,8,9] putRow(Right, [3,4,5], 0)=> board = [5,4,3 4,5,6
		 * 7,8,9] putRow(UP, [3,4,5], 1) => board = [1,3,3 4,4,6 7,5,9]
		 * putRow(DOWN, [3,4,5], 1) => board = [1,5,3 4,4,6 7,3,9] getRow(UP, 0)
		 * => [1,4,7] getRow(RIGHT,0) => [3,2,1] getRow(DOWN, 1) => [8,5,2]
		 *
		 * @param dir
		 *            - the direction the row will be translated to
		 * @param row
		 *            - the row to be inserted into the board (untranslated)
		 * @param rowNumber
		 *            - the row number
		 * @return
		 */
		public void putRow(Direction dir, int[] row, int rowNumber) {
			int i;

			switch (dir) {
			case LEFT:
				for (i = 0; i < boardSize; i++) {
					board[rowNumber][i] = row[i];
				}
				break;

			case RIGHT:
				for (i = 0; i < boardSize; i++) {
					board[rowNumber][boardSize - 1 - i] = row[i];
				}
				break;

			case UP:
				for (i = 0; i < boardSize; i++) {
					board[i][rowNumber] = row[i];
				}
				break;

			case DOWN:
				for (i = 0; i < boardSize; i++) {
					board[boardSize - 1 - i][rowNumber] = row[i];
				}
				break;

			default:
				System.out.println("Invalid direction provided");
				break;
			}

			return;
		}

		public int[][] getBoard() {
			return board;
		}

		/**
		 * inserts a 2 into a random open space on the board
		 *
		 * @return false if no open slots, true otherwise
		 */
		public boolean insertNumber() {
			int numSlots = getNumSlots();
			int nextSlot = 0;
			int slots = 0;

			if (numSlots != 0) {
				nextSlot = rand.nextInt(numSlots);
				for (int i = 0; i < boardSize; i++) {
					for (int j = 0; j < boardSize; j++) {
						if (board[i][j] == 0 && nextSlot == slots) {
							board[i][j] = 2;
							return true;
						} else if (board[i][j] == 0) {
							slots++;
						}

					}
				}
			}

			return false; // unable to fill a slot
		}

		/**
		 *
		 * @return Number of slots with no number
		 */
		private int getNumSlots() {
			int slots = 0;
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
					if (board[i][j] == 0)
						slots++;
				}
			}
			return slots;
		}

		/**
		 * Move the board in the given direction
		 *
		 * @param dir
		 *            Direction to move
		 */
		public boolean move(Direction dir) {
			int[] row;
			boolean canMove = canMove(dir);

			if (canMove) {
				for (int i = 0; i < boardSize; i++) {
					row = getRow(dir, i);
					transform(row);
					putRow(dir, row, i);
				}
			}
			return canMove;
		}

		/**
		 * Can anything on the board move in the specified direction?
		 * 
		 * @param dir
		 *            Direction to check
		 * @return
		 */
		public boolean canMove(Direction dir) {
			boolean rowCanMove = false;
			int[] row;

			for (int i = 0; i < boardSize; i++) {
				row = getRow(dir, i);
				rowCanMove = checkRow(row);
				if (rowCanMove)
					return true;
			}

			return false;
		}

		/**
		 * Test that this row can be moved to the left
		 * 
		 * @param row
		 *            Row/column already translated by getRow
		 */
		public static boolean checkRow(int[] row) {
			boolean nonEmpty = false;

			// if row is empty, return false... we cant move this row
			for (int i = 0; i < row.length; i++) {
				if (row[i] != 0)
					nonEmpty = true;
			}
			if (!nonEmpty)
				return false;

			// Row isn't empty
			for (int i = 1; i < row.length; i++) {
				// && row[i] != 0 is missing, mistakenly discern 32 0 0 0 0 is
				// true
				if (row[i - 1] == 0 && row[i] != 0) {
					return true;
				} else if (row[i - 1] == row[i] && row[i] != 0) {
					return true;
				}

			}
			return false;
		}

		/**
		 * Operate on a single row, always shifting to left if possible this is
		 * where the magic actually happens and is the hardest part of the
		 * problem
		 *
		 * @param row
		 *            One row or column, already translated by getRow
		 */
		public static void transform(int[] row) {
			boolean nonEmpty = false;
			int index = 1;

			// if row is empty, return, nothing to do
			for (int i = 0; i < row.length; i++) {
				if (row[i] != 0)
					nonEmpty = true;
			}
			if (!nonEmpty)
				return;

			// There is at least one entry in the board
			for (int i = 0; i < row.length - 1; i++) {
				if (row[index - 1] == 0) {
					shift(row, index); // previous spot is blank, move
										// everything left
				} else if (row[index] == 0 && ((index + 1) < row.length)) {
					shift(row, index + 1); // current is blank, shift toward
											// current
				} else if (row[index - 1] == row[index]) {
					row[index - 1] *= 2; // Merge 2 numbers
					score += row[index - 1];
					if ((index + 1) < row.length) {
						shift(row, (index + 1));
					} else {
						row[row.length - 1] = 0; // Assign 0 to the last index
					}
					index++;
				} else {
					index++;
				}
			}
		}

		/**
		 * Shift everything in a row left, starting at a given index The element
		 * at index-1 will be destroyed
		 *
		 * e.g. 2 0 0 2, index=1 --> 0 0 2 0
		 *
		 *
		 *
		 * @param row
		 *            row to shift
		 * @param index
		 *            Shift all elements starting from here onwards to the left
		 *            by 1 spot Must have index > 0
		 */
		public static void shift(int[] row, int index) {
			for (int i = index; i < row.length; i++) {
				row[i - 1] = row[i];
				// If the index is the last one, assign zero.
				if (i == (row.length - 1)) {
					row[i] = 0;
				}
			}
		}
	}

	@Override
	public void start(Stage applicationStage) throws IOException {

		one = new ArrayList<Label>();
		two = new ArrayList<Label>();
		three = new ArrayList<Label>();
		four = new ArrayList<Label>();

		GridPane gridPane = new GridPane();
		gridPane.setId("GridPane");

		ColumnConstraints colC = new ColumnConstraints(100);
		colC.setHalignment(HPos.CENTER);

		gridPane.getColumnConstraints().add(colC);
		gridPane.getColumnConstraints().add(colC);
		gridPane.getColumnConstraints().add(colC);
		gridPane.getColumnConstraints().add(colC);

		gridPane.getRowConstraints().add(new RowConstraints(50));
		gridPane.getRowConstraints().add(new RowConstraints(100));
		gridPane.getRowConstraints().add(new RowConstraints(100));
		gridPane.getRowConstraints().add(new RowConstraints(100));
		gridPane.getRowConstraints().add(new RowConstraints(100));

		Insets gridPadding = new Insets(10, 10, 10, 10);
		Scene scene = new Scene(gridPane, 450, 520);
		scene.getStylesheets().add(
				this.getClass().getResource("Game2048.css").toExternalForm());

		cell = new StackPane[4][4];

		board = new Board(4);
		game = new Game2048(board);

		Label score1 = new Label("SCORE");
		score1.setId("Score1");
		StackPane score1Pane = new StackPane();
		score1Pane.setId("Score1Pane");
		score1Pane.getChildren().add(score1);

		score2 = new Label();
		score2.setId("Score2");
		StackPane score2Pane = new StackPane();
		score2Pane.setId("Score2Pane");
		score2Pane.getChildren().add(score2);

		Label bestScore1 = new Label("BEST");
		bestScore1.setId("BestScore1");
		StackPane bestScore1Pane = new StackPane();
		bestScore1Pane.setId("Score1Pane");
		bestScore1Pane.getChildren().add(bestScore1);

		bestScore2 = new Label();
		bestScore2.setId("Score2");
		StackPane bestScore2Pane = new StackPane();
		bestScore2Pane.setId("Score2Pane");
		bestScore2Pane.getChildren().add(bestScore2);

		for (int i = 0; i < 4; ++i) {
			one.add(new Label());
			two.add(new Label());
			three.add(new Label());
			four.add(new Label());
			for (int j = 0; j < 4; ++j) {
				cell[j][i] = new StackPane();
				cell[j][i].setId("Cell");
			}
			one.get(i).setId("Label");
			cell[0][i].getChildren().add(one.get(i));
			two.get(i).setId("Label");
			cell[1][i].getChildren().add(two.get(i));
			three.get(i).setId("Label");
			cell[2][i].getChildren().add(three.get(i));
			four.get(i).setId("Label");
			cell[3][i].getChildren().add(four.get(i));
		}

		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < 4; ++j) {
				gridPane.add(cell[i][j], j, i + 1);
			}
		}

		gridPane.add(score1Pane, 0, 0);
		gridPane.add(score2Pane, 1, 0);
		gridPane.add(bestScore1Pane, 2, 0);
		gridPane.add(bestScore2Pane, 3, 0);

		gridPane.setPadding(gridPadding);
		gridPane.setHgap(10);
		gridPane.setVgap(10);

		applicationStage.setTitle("2048 by Andrew Chen Hsun Lee");
		applicationStage.setScene(scene);

		applicationStage.show();

		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				boolean move_result = false;

				if (event.getCode() == KeyCode.UP) {
					move_result = board.move(Direction.UP);
				} else if (event.getCode() == KeyCode.DOWN) {
					move_result = board.move(Direction.DOWN);
				} else if (event.getCode() == KeyCode.LEFT) {
					move_result = board.move(Direction.LEFT);
				} else if (event.getCode() == KeyCode.RIGHT) {
					move_result = board.move(Direction.RIGHT);
				}

				if (move_result) {
					board.insertNumber();
				}
				printBoard();
				score2.setText(Integer.toString(board.getScore()));

				if (!board.canMove(Direction.UP)
						&& !board.canMove(Direction.DOWN)
						&& !board.canMove(Direction.LEFT)
						&& !board.canMove(Direction.RIGHT)) {
					try {
						updateBestScore();
					} catch (IOException e) {
					}

					Alert alertEnd = new Alert(AlertType.NONE);
					alertEnd.setTitle("Game Over!");
					alertEnd.setContentText("You've got " + board.getScore()
							+ " score.\nDo you want to restart?");

					ButtonType exitButton = new ButtonType("Exit");
					ButtonType restartButton = new ButtonType("Restart");

					alertEnd.getButtonTypes().setAll(exitButton, restartButton);
					Optional<ButtonType> result = alertEnd.showAndWait();
					if (result.isPresent() && result.get() == restartButton) {
						reInitialize();
					} else {
						System.exit(0);
					}
				}
			}
		});

		score2.setText(Integer.toString(board.getScore()));
		game.play();
		return;
	}

	public void reInitialize() {
		board = new Board(4);
		game = new Game2048(board);
		board.score = 0;
		board.insertNumber();
		printBoard();

	}

	/**
	 * Creates a new board
	 */
	public Game2048() {
		this(new Board());
	}

	/**
	 *
	 * @param b
	 *            already initialized board to use
	 */
	public Game2048(Board b) {
		this.board = b;
	}

	public static void main(String[] args) {
		launch(args);

	}

	/**
	 * Start the game Take input and dish out the moves
	 */
	public void play() throws IOException {
		char input;

		// printControls();
		board.insertNumber();
		board.insertNumber();
		printBoard();
		updateBestScore();

		Alert alertStart = new Alert(AlertType.NONE,
				"Controls:\nMove Up - Move Down - Move Left - Move Right",
				new ButtonType("Play"));
		alertStart.showAndWait();

	}

	public void updateBestScore() throws IOException {
		try {
			int input = 0;
			FileInputStream f = new FileInputStream("SavingFile.txt");
			Scanner scnr = new Scanner(f);
			input = scnr.nextInt();
			scnr.close();
			if (input < board.score) {
				board.bestScore = board.score;
				PrintWriter pw = new PrintWriter("SavingFile.txt");
				pw.print(board.bestScore);
				pw.close();
			} else if (input > board.score) {
				board.bestScore = input;
			}

		} catch (IOException e) {
			PrintWriter pw = new PrintWriter("SavingFile.txt");
			pw.print("0");
			pw.close();
		}
		bestScore2.setText(Integer.toString(board.bestScore));
	}

	/**
	 * print the current state of the board
	 */
	private void printBoard() {
		int[][] a = board.getBoard();

		for (int i = 0; i < a.length; i++) {
			if (a[0][i] == 0) {
				one.get(i).setText("");
				cell[0][i].setId("Cell0");
			} else {
				one.get(i).setText(Integer.toString(a[0][i]));
				switch (a[0][i]) {
				case 2:
					one.get(i).setId("Label1");
					cell[0][i].setId("Cell1");
					break;
				case 4:
					one.get(i).setId("Label2");
					cell[0][i].setId("Cell2");
					break;
				case 8:
					one.get(i).setId("Label3");
					cell[0][i].setId("Cell3");
					break;
				case 16:
					one.get(i).setId("Label4");
					cell[0][i].setId("Cell4");
					break;
				case 32:
					one.get(i).setId("Label5");
					cell[0][i].setId("Cell5");
					break;
				case 64:
					one.get(i).setId("Label6");
					cell[0][i].setId("Cell6");
					break;
				case 128:
				case 256:
				case 512:
				case 1024:
				case 2048:
					one.get(i).setId("Label7");
					cell[0][i].setId("Cell7");
					break;
				}
			}
		}

		for (int i = 0; i < a.length; i++) {
			if (a[1][i] == 0) {
				two.get(i).setText("");
				cell[1][i].setId("Cell0");
			} else {
				two.get(i).setText(Integer.toString(a[1][i]));
				switch (a[1][i]) {
				case 2:
					two.get(i).setId("Label1");
					cell[1][i].setId("Cell1");
					break;
				case 4:
					two.get(i).setId("Label2");
					cell[1][i].setId("Cell2");
					break;
				case 8:
					two.get(i).setId("Label3");
					cell[1][i].setId("Cell3");
					break;
				case 16:
					two.get(i).setId("Label4");
					cell[1][i].setId("Cell4");
					break;
				case 32:
					two.get(i).setId("Label5");
					cell[1][i].setId("Cell5");
					break;
				case 64:
					two.get(i).setId("Label6");
					cell[1][i].setId("Cell6");
					break;
				case 128:
				case 256:
				case 512:
				case 1024:
				case 2048:
					two.get(i).setId("Label7");
					cell[1][i].setId("Cell7");
					break;
				}
			}
		}
		for (int i = 0; i < a.length; i++) {
			if (a[2][i] == 0) {
				three.get(i).setText("");
				cell[2][i].setId("Cell0");
			} else {
				three.get(i).setText(Integer.toString(a[2][i]));
				switch (a[2][i]) {
				case 2:
					three.get(i).setId("Label1");
					cell[2][i].setId("Cell1");
					break;
				case 4:
					three.get(i).setId("Label2");
					cell[2][i].setId("Cell2");
					break;
				case 8:
					three.get(i).setId("Label3");
					cell[2][i].setId("Cell3");
					break;
				case 16:
					three.get(i).setId("Label4");
					cell[2][i].setId("Cell4");
					break;
				case 32:
					three.get(i).setId("Label5");
					cell[2][i].setId("Cell5");
					break;
				case 64:
					three.get(i).setId("Label6");
					cell[2][i].setId("Cell6");
					break;
				case 128:
				case 256:
				case 512:
				case 1024:
				case 2048:
					three.get(i).setId("Label7");
					cell[2][i].setId("Cell7");
					break;
				}
			}
		}
		for (int i = 0; i < a.length; i++) {
			if (a[3][i] == 0) {
				four.get(i).setText("");
				cell[3][i].setId("Cell0");
			} else {
				four.get(i).setText(Integer.toString(a[3][i]));
				switch (a[3][i]) {
				case 2:
					four.get(i).setId("Label1");
					cell[3][i].setId("Cell1");
					break;
				case 4:
					four.get(i).setId("Label2");
					cell[3][i].setId("Cell2");
					break;
				case 8:
					four.get(i).setId("Label3");
					cell[3][i].setId("Cell3");
					break;
				case 16:
					four.get(i).setId("Label4");
					cell[3][i].setId("Cell4");
					break;
				case 32:
					four.get(i).setId("Label5");
					cell[3][i].setId("Cell5");
					break;
				case 64:
					four.get(i).setId("Label6");
					cell[3][i].setId("Cell6");
					break;
				case 128:
				case 256:
				case 512:
				case 1024:
				case 2048:
					four.get(i).setId("Label7");
					cell[3][i].setId("Cell7");
					break;
				}
			}
		}
	}
}
