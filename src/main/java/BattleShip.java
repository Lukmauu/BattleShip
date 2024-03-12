import java.util.*;

public class BattleShip {
    private final int totalShips = 17;
    private final Random random;
    private char[][] playerShipsBoard;
    private char[][] playerHitsBoard;
    private char[][] computerShipsBoard;
    private char[][] computerHitsBoard;
    private int playerHits = 0;
    private int playerMisses = 0;
    private int computerHits = 0;
    private int computerMisses = 0;
    private int[] computerCoordinates = new int[]{-2,-2};
    ComputerMove computerMove;
    private final String[] leadingLetters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

    public static void main(String[] args) {

        BattleShip battleShip = new BattleShip(10, 10);
        battleShip.computerMove = new ComputerMove(battleShip.getRows(), battleShip.getColumns());

        while (battleShip.playerHits < battleShip.totalShips && battleShip.computerHits < battleShip.totalShips) {

            /*
                TODO: YOU CAN UNCOMMENT THE LINES BELOW TO SEE THE COMPUTER'S SHIPS BOARD
             */
            battleShip.displayBoards(battleShip.playerHitsBoard, battleShip.playerShipsBoard, "Player");
            battleShip.displayBoards(battleShip.computerHitsBoard, battleShip.computerShipsBoard, "Computer");

            battleShip.displayStats();

            Print.spaceLine();
            Print.cyanLine("Player's turn:");
            boolean exit = battleShip.playerMove();

            if (!exit) {

                battleShip.endOfGame();
                battleShip.displayStats();
                break;
            }

            Print.cyanLine("Computer's turn:");
            battleShip.computerRandomMove();
        }
    }

    public BattleShip(int rows, int columns) {

        playerShipsBoard = new char[rows][columns];
        playerHitsBoard = new char[rows][columns];
        computerShipsBoard = new char[rows][columns];
        computerHitsBoard = new char[rows][columns];

        initializeBoard(playerShipsBoard);
        initializeBoard(playerHitsBoard);
        initializeBoard(computerShipsBoard);
        initializeBoard(computerHitsBoard);

        random = new Random();

        placeShips(computerShipsBoard, true, true);
        placeShips(playerShipsBoard, false, true);
    }

    private void initializeBoard(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = Status.EMPTY;
            }
        }
    }

    private void placeShips(char[][] board, boolean isComputer, boolean isTesting) {

        if (isComputer) {
            placeShipForComputer(board, 5, "Carrier");
            placeShipForComputer(board, 4, "Battleship");
            placeShipForComputer(board, 3, "Cruiser");
            placeShipForComputer(board, 3, "Submarine");
            placeShipForComputer(board, 2, "Destroyer");
        } else {
            placeShipsForPlayer(playerShipsBoard, 5, "Carrier", isTesting);
            placeShipsForPlayer(playerShipsBoard, 4, "Battleship", isTesting);
            placeShipsForPlayer(playerShipsBoard, 3, "Cruiser", isTesting);
            placeShipsForPlayer(playerShipsBoard, 3, "Submarine", isTesting);
            placeShipsForPlayer(playerShipsBoard, 2, "Destroyer", isTesting);
        }
    }

    private void placeShipsForPlayer(char[][] board, int length, String name, boolean isTesting) {

        int row;
        int col;
        String orientation = "h";
        boolean placed = false;
        boolean isHorizontal = false;

        Print.spaceLine();

        while (!placed) {
            if (!isTesting) {
                Scanner scanner = new Scanner(System.in);
                Print.purpleLine("Enter the starting row and column for the " + name + " (length " + length + "):");

                try {
                    Print.purpleLine("Row (1-" + board.length + "): ");
                    row = scanner.nextInt();
                    row--;

                    Print.purpleLine("Column (1-" + board[0].length + "): ");
                    col = scanner.nextInt();
                    col--;

                    Print.purpleLine("Enter 'h' for horizontal or 'v' for vertical placement: ");
                    orientation = scanner.next();
                } catch (Exception e) {
                    Print.redLine("Invalid input. Try again.");
                    Print.spaceLine();
                    continue;
                }

                isHorizontal = orientation.equalsIgnoreCase("h");
            } else {
                row = random.nextInt(board.length);
                col = random.nextInt(board[0].length);
                isHorizontal = random.nextBoolean();
            }


            if (isValidShipPlacement(board, row, col, length, isHorizontal)) {
                for (int i = 0; i < length; i++) {
                    if (isHorizontal) {
                        board[row][col + i] = Status.SHIP;
                    } else {
                        board[row + i][col] = Status.SHIP;
                    }
                }

                placed = true;
            } else {
                Print.redLine("Invalid placement: it might have ship already here or it went off the board. Try again.");
            }
        }
    }

    private void placeShipForComputer(char[][] board, int length, String name) {
        boolean placed = false;

        while (!placed) {
            int row = random.nextInt(board.length);
            int col = random.nextInt(board[0].length);
            boolean isHorizontal = random.nextBoolean();

            if (isValidShipPlacement(board, row, col, length, isHorizontal)) {

                ArrayList<int[]> finalPoints = new ArrayList<>();

                for (int h = 1; h <= length; h++) {

                    int[] centerPoint = isHorizontal ? new int[]{row, col + h} : new int[]{row + h, col};

                    if (canThisPointBeAShip(centerPoint, board)) {
                        finalPoints.add(centerPoint);
                    }
                }

                if (finalPoints.size() != length) {
                    continue;
                }

                for (int i = 0; i < length; i++) {
                    int[] point = finalPoints.get(i);
                    board[point[0]][point[1]] = Status.SHIP;
                }

                placed = true;
            }
        }
    }

    private boolean canThisPointBeAShip(int[] centerPoint, char[][] board) {
        int[][] points = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 0}, {0, 1},
                {1, -1}, {-1, 0}, {1, 1}
        };

        for (int i = 0; i < points.length; i++) {
            int[] point = points[i];

            int newRow = centerPoint[0] + point[0];
            int newCol = centerPoint[1] + point[1];

            if (newRow >= 0 && newRow < board.length && newCol >= 0 && newCol < board[0].length) {
                if (board[newRow][newCol] != Status.EMPTY) {
                    return false;
                }
            } else {
                if (newRow == centerPoint[0] && newCol == centerPoint[1]) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isValidShipPlacement(char[][] board, int row, int col, int length, boolean isHorizontal) {
        if (row < 0 || row >= board.length || col < 0 || col >= board[0].length) {
            return false;  // Invalid placement if outside the board
        }

        // Check if the ship can be placed without overlapping
        for (int i = 0; i < length; i++) {
            if (isHorizontal) {
                if (col + i >= board[0].length || board[row][col + i] == Status.SHIP) {
                    return false;  // Overlapping or outside the board
                }
            } else {
                if (row + i >= board.length || board[row + i][col] == Status.SHIP) {
                    return false;  // Overlapping or outside the board
                }
            }
        }

        return true;
    }

    private void displayBoards(char[][] shipBoard, char[][] hitBoard, String boardName) {

        produceHeader(shipBoard, hitBoard, boardName);

        for (int i = 0; i < shipBoard.length; i++) {

            produceWaterRows(shipBoard, i);

            purpleDivider();

            produceWaterRows(hitBoard, i);

            System.out.println();
        }

        System.out.println();
    }

    private void produceHeader(char[][] shipBoard, char[][] hitBoard, String boardName) {

        String emptySpace = "  ";

        Print.spaceLine();

        //String formatted = String.format("%4s %115s", (boardName + "'s Hit Board"), (boardName + "'s Ship Board"));
        String formatted = String.format("%4s %65s", (boardName + "'s Hit Board"), (boardName + "'s Ship Board"));

        Print.purpleLine(emptySpace + formatted);

        Print.spaceLine();

        produceFirstRow(shipBoard, emptySpace);

        purpleDivider();

        produceFirstRow(hitBoard, emptySpace);

        Print.spaceLine();
    }

    private void produceFirstRow(char[][] shipBoard, String emptySpace)
    {
        for (int i = 0; i < shipBoard[0].length; i++) {

            if (i == 0) {
                System.out.print(emptySpace + " " + emptySpace);
            }

            String spaceAfter = (i < 9 ? "  " : " ");

            System.out.print(emptySpace + (i + 1) + spaceAfter);
        }
    }

    private void purpleDivider() {
        String purpleColor = "\u001B[35m";
        String resetColor = "\u001B[0m";
        System.out.print(purpleColor + "    ||    " + resetColor);
    }

    private void produceWaterRows(char[][] board, int i) {
        String emptySpace = "  ";
        String redColor = "\u001B[31m";
        String resetColor = "\u001B[0m";
        String blueColor = "\u001B[34m";
        String brightGreenColor = "\u001B[92m";

        //String spaceAfter = (i < 9 ? "  " : "  ");
        String spaceAfter = "  ";

        //System.out.print(emptySpace + (i + 1) + spaceAfter);
        System.out.print(emptySpace + (leadingLetters[i]) + spaceAfter);

        for (int l = 0; l < board[0].length; l++) {

            char cell = board[i][l];

            String cellWithSpace = emptySpace + board[i][l] + emptySpace;

            if (cell == Status.HIT) {
                System.out.print(brightGreenColor + cellWithSpace + resetColor);
            } else if (cell == Status.MISS) {
                System.out.print(redColor + cellWithSpace + resetColor);
            } else if (cell == Status.SHIP) {
                System.out.print(resetColor + cellWithSpace);
            } else {
                System.out.print(blueColor + cellWithSpace + resetColor);
            }
        }
    }

    private void displayStats() {
        Print.spaceLine();
        Print.yellowLine("Player hits: " + playerHits);
        Print.yellowLine("Player misses: " + playerMisses);
        Print.yellowLine("Computer hits: " + computerHits);
        Print.yellowLine("Computer misses: " + computerMisses);

        // red color
        String redColor = "\u001B[31m";
        String resetColor = "\u001B[0m";

        if (computerCoordinates[0] != -2) {
            Print.purpleLine(redColor + "Computer's move: " +
                    (computerCoordinates[0] + 1) + ", " + (computerCoordinates[1] + 1));
        }
    }

    private boolean playerMove() {

        Scanner scanner = new Scanner(System.in);
        String firstLetter = leadingLetters[0];
        String lastLetter = leadingLetters[leadingLetters.length - 1];
        int row;
        int column;

        Print.cyanLine("Enter a letter for the row (" + firstLetter + "-" + lastLetter + ") or " +
                "(" + firstLetter.toLowerCase() + "-" + lastLetter.toLowerCase() + "): ");

        try {
            String letter = scanner.next();
            row = Arrays.asList(leadingLetters).indexOf(letter.toUpperCase());

            if (row == -1) {
                throw new Exception();
            }

            Print.cyanLine("Enter column (1-" + (playerHitsBoard[0].length) + "): ");
            column = scanner.nextInt();
            column--;
        } catch (Exception e) {
            row = -2;
            column = -2;
        }


        if (isValidMove(computerShipsBoard, row, column)
            && playerHitsBoard[row][column] == Status.EMPTY) {

            if (computerShipsBoard[row][column] == Status.SHIP) {

                Print.brightGreenLine("Player hit!");
                playerHitsBoard[row][column] = Status.HIT;

                //

                playerHits++;
            } else {

                Print.redLine("Player miss.");
                playerHitsBoard[row][column] = Status.MISS;
                playerMisses++;
            }
        } else {
            Print.redLine("Invalid move. Try again.");
            playerMove();
        }

        return true;
    }

    private boolean isValidMove(char[][] board, int row, int col) {
        return row >= 0 && row < board.length && col >= 0 && col < board[0].length &&
                board[row][col] != Status.HIT && board[row][col] != Status.MISS;
    }

    private void endOfGame() {
        Print.spaceLine();
        Print.cyanLine("END OF GAME");
        Print.spaceLine();
    }

    public int getRows() {
        return playerShipsBoard.length;
    }

    public int getColumns() {
        return playerShipsBoard[0].length;
    }

    private void computerRandomMove()
    {
        int[] spot = computerMove.getNextTarget();
        int row = spot[0];
        int column = spot[1];

        if (isValidMove(computerHitsBoard, row, column)) {

            checkIfHitOrMiss(row, column);
            computerMove.updateProbabilities(row, column, computerHitsBoard[row][column] == Status.HIT, computerHitsBoard);
        } else {

            computerRandomMove();
        }
    }

    private void checkIfHitOrMiss(int row, int column) {

        computerCoordinates[0] = row;
        computerCoordinates[1] = column;

        if (playerShipsBoard[row][column] == Status.SHIP) {
            Print.greenLine("Computer hit!");
            computerHitsBoard[row][column] = Status.HIT;
            computerHits++;
        } else {
            Print.redLine("Computer miss.");
            computerHitsBoard[row][column] = Status.MISS;
            computerMisses++;
        }
    }
}



class Status {
    public static final char SHIP = 'S';
    public static final char HIT = 'H';
    public static final char MISS = 'M';
    public static final char EMPTY = '~';
    public static final String NOT_FOUND = "NOT FOUND";
}



class Print {
    public static void spaceLine() {
        System.out.println();
    }

    public static void redLine(String text) {
        System.out.println("\u001B[31m" + text + "\u001B[0m");
    }

    public static void yellowLine(String text) {
        System.out.println("\u001B[33m" + text + "\u001B[0m");
    }

    public static void greenLine(String text) {
        System.out.println("\u001B[32m" + text + "\u001B[0m");
    }

    public static void blueLine(String text) {
        System.out.println("\u001B[34m" + text + "\u001B[0m");
    }

    public static void purpleLine(String text) {
        System.out.println("\u001B[35m" + text + "\u001B[0m");
    }

    public static void cyanLine(String text) {
        System.out.println("\u001B[36m" + text + "\u001B[0m");
    }

    public static void brightGreenLine(String text) { System.out.println("\u001B[92m" + text + "\u001B[0m"); }
}



class ComputerMove {
    private enum Mode {
        HUNTING,
        TARGETING
    }
    private Mode currentMode = Mode.HUNTING;
    private int initialHitRow = -1;
    private int initialHitCol = -1;
    private int lastHitRow = -1;
    private int lastHitCol = -1;
    private int direction = 0; // 0 = up, 1 = right, 2 = down, 3 = left
    private static final double INITIAL_PROBABILITY = 0.5;
    private static final double DECREASE_FACTOR = 0.5;
    private static final double INCREASE_FACTOR = 2.0;
    private double[][] probabilities;
    private boolean[][] targeted;
    private final Random random;

    public ComputerMove(int row, int col) {

        probabilities = new double[row][col];
        targeted = new boolean[row][col];
        random = new Random();
        initializeProbabilities();
    }

    private void initializeProbabilities() {

        for (int i = 0; i < probabilities.length; i++) {
            for (int j = 0; j < probabilities[i].length; j++) {
                probabilities[i][j] = random.nextDouble() * 0.5 + INITIAL_PROBABILITY;
            }
        }
    }

    public int[] getNextTarget() {

        if (currentMode == Mode.HUNTING) {

            normalizeProbabilities();
            int[] target = selectTarget();
            targeted[target[0]][target[1]] = true;

            return target;
        } else {

            normalizeProbabilities();
            return selectNextTargetInDirection(0);
        }
    }

    private void normalizeProbabilities() {

        /*
          In the context of the Monte Carlo method, normalization ensures that the probabilities
          are properly weighted for random selection. Without normalization, the random selection
          process may not be representative of the underlying probabilities, leading to biased or
          incorrect decisions.

          In summary, normalizing the probabilities ensures that they accurately represent a valid
          probability distribution, allowing for meaningful probabilistic reasoning and decision-making
          in the AI algorithm. It is a fundamental step in probabilistic modeling and inference.
         */

        // Calculate total probability
        double totalProbability = 0;
        for (double[] row : probabilities) {
            for (double prob : row) {
                totalProbability += prob;
            }
        }

        // Normalize probabilities
        for (int i = 0; i < probabilities.length; i++) {
            for (int j = 0; j < probabilities[i].length; j++) {
                probabilities[i][j] /= totalProbability;
            }
        }
    }

    private int[] selectTarget() {
        double maxProbability = -1;
        int[] maxProbCell = new int[]{-1, -1};

        for (int i = 0; i < probabilities.length; i++) {
            for (int j = 0; j < probabilities[i].length; j++) {
                if (!targeted[i][j] && probabilities[i][j] > maxProbability) {
                    maxProbability = probabilities[i][j];
                    maxProbCell[0] = i;
                    maxProbCell[1] = j;
                }
            }
        }

        if (maxProbCell[0] != -1 && maxProbCell[1] != -1) {
            targeted[maxProbCell[0]][maxProbCell[1]] = true;
            return maxProbCell;
        }

        // Fallback in case all cells are targeted (should not normally happen)
        return selectRandomUntargetedCell();
    }

    private int[] selectRandomUntargetedCell() {

        int i, j;
        do {
            i = random.nextInt(probabilities.length);
            j = random.nextInt(probabilities[0].length);
            // it will keep looping until it finds an untargeted cell
            // parity select every other cell
        } while (targeted[i][j] && (i+j) % 2 == 0);

        return new int[]{i, j};
    }

    private int[] selectNextTargetInDirection(int attempts) {

        if (attempts >= 4) {
            return selectRandomUntargetedCell();
        }

        int newRow = lastHitRow;
        int newCol = lastHitCol;

        switch (direction) {
            case 0: newRow--; break; // Up
            case 1: newCol++; break; // Right
            case 2: newRow++; break; // Down
            case 3: newCol--; break; // Left
        }

        if (newRow < 0 || newRow >= probabilities.length || newCol < 0
                || newCol >= probabilities[0].length || targeted[newRow][newCol]) {

            direction = (direction + 2) % 4;

            return selectNextTargetInDirection(attempts + 1);
        }

        return new int[]{newRow, newCol};
    }

    public void updateProbabilities(int row, int col, boolean hit, char[][] grid) {

        targeted[row][col] = true;

        if (hit) {
            if (currentMode == Mode.HUNTING) {
                initialHitRow = row;
                initialHitCol = col;
            }

            lastHitRow = row;
            lastHitCol = col;
            currentMode = Mode.TARGETING;
            decreaseAdjacentProbabilities(row, col);
        } else {
            if (currentMode == Mode.TARGETING) {
                direction = (direction + 1) % 4;
                if (direction == 2) {
                    lastHitRow = initialHitRow;
                    lastHitCol = initialHitCol;
                }
            }

            increaseProbabilitiesOnMiss(row, col);
        }

        normalizeProbabilities();
        updateProbabilityDensity(grid);
    }

    private void decreaseAdjacentProbabilities(int row, int col) {

        // Decrease probabilities of adjacent cells on hit
        adjustAdjacentProbabilities(row, col, DECREASE_FACTOR);
    }

    private void increaseProbabilitiesOnMiss(int row, int col) {

        // Increase probabilities of adjacent cells on miss
        adjustAdjacentProbabilities(row, col, INCREASE_FACTOR);
    }

    private void adjustAdjacentProbabilities(int row, int col, double factor) {

        for (int i = Math.max(0, row - 1); i <= Math.min(probabilities.length - 1, row + 1); i++) {
            for (int j = Math.max(0, col - 1); j <= Math.min(probabilities[0].length - 1, col + 1); j++) {
                probabilities[i][j] *= factor;
            }
        }
    }

    private void updateProbabilityDensity(char[][] board) {
        for (int i = 0; i < probabilities.length; i++) {
            for (int j = 0; j < probabilities[i].length; j++) {
                probabilities[i][j] = 0;
            }
        }

        // For each ship size
        for (int shipSize = 2; shipSize <= 5; shipSize++) {
            // For each cell on the board
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    // Check horizontal placement
                    if (j + shipSize <= board[i].length) {
                        boolean canPlace = true;
                        for (int k = 0; k < shipSize; k++) {
                            if (board[i][j + k] == Status.HIT || targeted[i][j + k]) {
                                canPlace = false;
                                break;
                            }
                        }
                        if (canPlace) {
                            for (int k = 0; k < shipSize; k++) {
                                probabilities[i][j + k]++;
                            }
                        }
                    }

                    // Check vertical placement
                    if (i + shipSize <= board.length) {
                        boolean canPlace = true;
                        for (int k = 0; k < shipSize; k++) {
                            if (board[i + k][j] == Status.HIT || targeted[i + k][j]) {
                                canPlace = false;
                                break;
                            }
                        }
                        if (canPlace) {
                            for (int k = 0; k < shipSize; k++) {
                                probabilities[i + k][j]++;
                            }
                        }
                    }
                }
            }
        }
    }
}




