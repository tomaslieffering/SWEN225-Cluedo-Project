package Board;

import Game.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Board {

    /**
     * Array containing all of the tiles on the board
     */
    private final BoardTile[][] board;

    /**
     * Constructs a new board with the given layout
     * @param boardLayout
     *   File containing the layout of the board
     * @throws BoardFormatException
     *   Thrown if the is an IO exception or if the file does not describe a valid board
     */
    public Board(File boardLayout) throws BoardFormatException{
        try{
            Scanner scan = new Scanner(boardLayout);
            String currentRow = scan.nextLine();
            int rowLength = currentRow.length() - 1;
            int rowNumber = 0;

            // scanning everything into list
            List<BoardTile> tileList = new ArrayList<>(Board.parseRow(currentRow, rowNumber));
            while(scan.hasNext()) {
                currentRow = scan.nextLine();
                rowNumber++;
                if(rowLength + 1 != currentRow.length())
                    throw new BoardFormatException("Exception during board parsing.\n\"Row Length mismatched\"");
                tileList.addAll(Board.parseRow(currentRow, rowNumber));
            }

            // compiling into array
            BoardTile[][] boardTiles = new BoardTile[tileList.size() / rowLength][rowLength];
            for(int i = 0; i < boardTiles.length; i++) {
                for(int j = 0; j < boardTiles[0].length; j++) {
                    boardTiles[i][j] = tileList.get(j + (i * rowLength));
                }
            }
            board = boardTiles;

        } catch (IOException e) {
            throw new BoardFormatException("Exception during board data file handling.");
        }
    }

    /**
     * Will parse a string into a list of board tiles
     */
    private static List<BoardTile> parseRow(String row, int rowNumber) throws BoardFormatException{
        if(!row.endsWith("/"))
            throw new BoardFormatException("Exception during board parsing.\n\"No '/' at end of row\"");
        row = row.substring(0, row.length() - 1);
        List<BoardTile> tiles = new ArrayList<>();
        int colNumber = 0;
        for(char c : row.toCharArray()) {
            switch (c) {
                case '&':
                    tiles.add(new WallTile());
                    break;
                case 'K':
                    tiles.add(new RoomTile(RoomTile.RoomType.KITCHEN));
                    break;
                case 'B':
                    tiles.add(new RoomTile(RoomTile.RoomType.BALL_ROOM));
                    break;
                case 'C':
                    tiles.add(new RoomTile(RoomTile.RoomType.CONSERVATORY));
                    break;
                case 'D':
                    tiles.add(new RoomTile(RoomTile.RoomType.DINING_ROOM));
                    break;
                case 'b':
                    tiles.add(new RoomTile(RoomTile.RoomType.BILLIARD_ROOM));
                    break;
                case 'L':
                    tiles.add(new RoomTile(RoomTile.RoomType.LIBRARY));
                    break;
                case 'l':
                    tiles.add(new RoomTile(RoomTile.RoomType.LOUNGE));
                    break;
                case 'H':
                    tiles.add(new RoomTile(RoomTile.RoomType.HALL));
                    break;
                case 'S':
                    tiles.add(new RoomTile(RoomTile.RoomType.STUDY));
                    break;
                case ' ':
                    tiles.add(new RoomTile(RoomTile.RoomType.HALLWAY));
                    break;
                case 'W':
                    tiles.add(new PlayerTile(PlayerTile.PersonType.MRS_WHITE, rowNumber, colNumber));
                    break;
                case 'G':
                    tiles.add(new PlayerTile(PlayerTile.PersonType.MR_GREEN, rowNumber, colNumber));
                    break;
                case 'P':
                    tiles.add(new PlayerTile(PlayerTile.PersonType.MRS_PEACOCK, rowNumber, colNumber));
                    break;
                case 'p':
                    tiles.add(new PlayerTile(PlayerTile.PersonType.PROFESSOR_PLUM, rowNumber, colNumber));
                    break;
                case 's':
                    tiles.add(new PlayerTile(PlayerTile.PersonType.MISS_SCARLETT, rowNumber, colNumber));
                    break;
                case 'M':
                    tiles.add(new PlayerTile(PlayerTile.PersonType.COLONEL_MUSTARD, rowNumber, colNumber));
                    break;
                default:
                    throw new BoardFormatException("Exception during board parsing.\n\"Unrecognized character '" + c + "'\"");
            }
            colNumber++;
        }
        return tiles;
    }

    /**
     * delete the player tiles from the board if there are less than six players
     * @param players the list of active players
     */
    public void deleteUnusedPlayers(List<Player> players){
        //go through the board
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[row].length; col++){
                //if it is a player tile, check whether they are a active [player
                if (board[row][col] instanceof PlayerTile){
                    boolean inGame = false;
                    for(Player p: players){
                        if (p.toString().equals(board[row][col].getName())){
                            inGame = true;
                            break;
                        }
                    }
                    //if not, replace with a hallway tile
                    if (!inGame){
                        board[row][col] = new RoomTile();
                    }
                }
            }
        }
    }

    /**
     * find a specific player's position on the board
     * @param personType the name of the person to be found
     * @return the position of found player
     */
    public Position findPlayer(String personType){
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[row].length; col++){
                if (board[row][col].getName().equals(personType)){
                    return new Position(row, col);
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[0].length; j++)
                str.append(board[i][j].toString());
            str.append("\n");
        }
        return str.toString();
    }

    //TODO remove this when not needed for testing anymore
    public static void main(String[] args) throws BoardFormatException {
        System.out.println(new Board(new File("board-layout.dat")));
    }

    /**
     * Checks whether a specific input is valid
     * @param input the input string to move the player
     * @param diceNumber the number the player rolled on the dice
     * @param start the player's starting position
     * @return true if the move is valid, false if not
     */
    public boolean checkMove(String input, int diceNumber, Position start) {
        if (input.length() == diceNumber){
            if(checkWall(input, start)){
                return true;
            }
            else{
                System.out.println("You can't move through a wall!");
            }
        }
        else{
            System.out.println("You must move the same amount of moves as dice rolls!");
            return false;
        }
        return false;
    }

    /**
     * Checks if a given input will go through a wall
     * @param input the input string
     * @param start the starting position
     * @return true if there are no walls in the way, false if there are
     */
    public boolean checkWall(String input, Position start){
        boolean noWalls = true;
        Position currentPosition = start;
        for (char c: input.toCharArray()){
            if (c == 'u'){
                currentPosition = currentPosition.move(-1, 0);
            }
            if (c == 'd'){
                currentPosition = currentPosition.move(1, 0);
            }
            if (c == 'r'){
                currentPosition = currentPosition.move(0, 1);
            }
            if (c == 'l'){
                currentPosition = currentPosition.move(0, -1);
            }
            if (board[currentPosition.getRow()][currentPosition.getCol()].getName().equals("WALL")){
                noWalls = false;
                break;
            }
        }
        return noWalls;
    }

    /**
     * Moves the playerTile on the board, after checking whether the move was valid or not
     * @param p the player to be moved
     * @param input the input string, valid
     */
    public void movePlayer(Player p, String input) {
        int rows = 0;
        int cols = 0;
        for (char c: input.toCharArray()){
            if (c == 'u'){
                rows--
                ;
            }
            if (c == 'd'){
                rows++;
            }
            if (c == 'r'){
                cols++;
            }
            if (c == 'l'){
                cols--;
            }
        }
        Position start = findPlayer(p.toString());
        Position end = start.move(rows, cols);
        board[start.getRow()][start.getCol()] = new RoomTile();
        board[end.getRow()][end.getCol()] = new PlayerTile(p.getPersonType(), end.getRow(), end.getCol());
    }
}
