package model;

import common.Observer;

import java.util.*;


/**
 * Operates the mechanics behind the Gobblets game
 */
public class GobbletsModel {

    /** List of Observers which the model calls to update*/
    private final List<Observer<GobbletsModel>> observers = new LinkedList<>();

    /** Represents the 4x4 game board*/
    private final PieceStack[][] board = new PieceStack[4][4];


    private final Player player1;

    private final Player player2;

    /** List of players in a queue; the first player in the queue is the active player */
    private final LinkedList<Player> playerQueue = new LinkedList<>();

    /** Location of the current selected piece, stored so that it may be called upon the next selection */
    private PieceStack selectedPieceLocation;

    /** Player that has won the game */
    private Player winner = null;

    private Boolean swapping = false;

    private Boolean updating = false;


    /**
     * Creates a new Game model with an empty board
     * @param player1 player that will go first
     * @param player2 player that will go second
     */
    public GobbletsModel(Player player1, Player player2){
        for(int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                this.board[row][col] = new PieceStack("board");
            }
        }
        this.playerQueue.add(player1);
        this.player1 = player1;
        this.playerQueue.add(player2);
        this.player2 = player2;
        this.selectedPieceLocation = null;
    }


    public boolean isSwapping(){
        return this.swapping;
    }

    public boolean isUpdating(){
        return this.updating;
    }


    /**
     * Actions taken when selecting a piece on the board
     * @param row board row selected
     * @param col board column selected
     */
    public void selectBoardPiece(int row, int col){

        // selects the board piece if it is the current player's piece, nothing otherwise
        this.updating = true;

        if(this.selectedPieceLocation == null){
            if(!this.board[row][col].empty() && this.playerQueue.peek().
                    myPiece(this.board[row][col].peek())){
                this.selectedPieceLocation = this.board[row][col];
                alertObservers(new ClientData("Selected board piece", false));
            }
            else{
                alertObservers((new ClientData("Invalid selection", false)));
            }
        }

        // moves the piece from the stack to the board if the move is valid, nothing otherwise
        else if (this.selectedPieceLocation.getLocationTag().equals("player")) {
            Piece piece = this.selectedPieceLocation.peek();

            // Place if tile is empty or if if opponent has a sequence of three pieces where one of those three can be
            // gobbled by the stack piece
            if(this.board[row][col].empty() || (piece.getPlayerName().equals(this.board[row][col].peek().
                    getPlayerName()) && piece.lessThan(board[row][col].peek()) && this.canGobbleFromStack
                    (row, col))){
                this.board[row][col].push(this.selectedPieceLocation.pop());
                this.selectedPieceLocation = null;

                // ends the round if there is a winner
                checkWinners();
                if(this.winner == null){
                    swapPlayers();
                }
                alertObservers(new ClientData("Played stack piece", true));
            }
            else{
                alertObservers(new ClientData("Invalid move", false));
            }
        }
        // moves the piece on the board if the move is valid, nothing otherwise
        else {
            if(this.board[row][col].empty() || this.selectedPieceLocation.peek().
                    lessThan(this.board[row][col].peek())){
                this.board[row][col].push(this.selectedPieceLocation.pop());
                this.selectedPieceLocation = null;
                // ends the round if there is a winner
                checkWinners();
                if(this.winner == null){
                    swapPlayers();
                }
                alertObservers(new ClientData("Played board piece", true));
            }
            else if(this.selectedPieceLocation.peek() == this.board[row][col].peek()){
                this.selectedPieceLocation = null;
                alertObservers(new ClientData("Deselected board piece", false));
            }
            else{
                alertObservers(new ClientData("Invalid move", false));
            }
        }
    }

    /**
     * Actions taken when a piece on the player's external stack is selected
     * @param stack external stack which should be taken from
     */
    public void selectStackPiece(int stack){
        // selects the piece from the stack, nothing if the stack is empty
        this.updating = true;

        if(this.selectedPieceLocation == null){
            if(!this.getCurrentPlayer().emptyStack(stack)){
                this.selectedPieceLocation = playerQueue.peek().getStack(stack);
                alertObservers(new ClientData("Selected stack piece", false));
            }
            else{
                alertObservers(new ClientData("Stack is empty", false));
            }
        }
        // deselects the piece if it's the same piece, otherwise do nothing
        else{
            if(this.selectedPieceLocation == playerQueue.peek().getStack(stack)){
                this.selectedPieceLocation = null;
                alertObservers(new ClientData("Deselected stack piece", false));
            }
            else{
                alertObservers(new ClientData("Invalid move", false));
            }
        }
    }






    /**
     * Swaps player turns
     */
    private void swapPlayers(){
        this.swapping = true;
        this.playerQueue.add(this.playerQueue.poll());
        this.swapping = false;
    }



    /**
     * Checks each player to see if they have won the game, priority goes to the player whose
     * turn it currently is not
     */
    private void checkWinners(){
        if(isWinner(this.playerQueue.get(1))){
            this.winner = this.playerQueue.get(1);
        }
        else if(isWinner(this.playerQueue.get(0))){
            this.winner = this.playerQueue.get(0);
        }
    }

    /**
     * Determines if a player has won by checking if any rows, columns, or diagonals contain 4 of
     * the given player's pieces
     * @param player Player whose pieces are being examined
     * @return True if this player has a winning condition, false otherwise
     */
    private boolean isWinner(Player player){
        for(int i = 0; i < 4; i++) {
            if (winningRow(player, i) || winningColumn(player, i)) {
                return true;
            }
        }
        return winningDiagonal(player);
    }

    /** Checks if a player has won in a given row */
    private boolean winningRow(Player player, int row){
        for(int col = 0; col < 4; col++){
            if(this.board[row][col].empty() || !player.myPiece(this.board[row][col].peek())){
                return false;
            }
        }
        return true;
    }

    /** Checks if a player has won in a given column */
    private boolean winningColumn(Player player, int col){
        for(int row = 0; row < 4; row++) {
            if (this.board[row][col].empty() || !player.myPiece(this.board[row][col].peek())) {
                return false;
            }
        }
        return true;
    }

    /** Checks if a player has won diagonally*/
    private boolean winningDiagonal(Player player){
        for(int i = 0; i < 4; i++) {
            if (this.board[i][i].empty() || !player.myPiece(this.board[i][i].peek())) {
                return false;
            }
        }
        for(int j = 0; j < 4; j++) {
            if (this.board[j][3 - j].empty() || !player.myPiece(this.board[j][3 - j].peek())) {
                return false;
            }
        }
        return true;
    }

    /** Returns the winner of the game, may be null */
    public Player getWinner(){
        return this.winner;
    }

    /** Given player has timed out so the opposing player wins */
    public void timedOut(Player player){
        this.winner = getOtherPlayer(player);
    }

    /** Returns the player whose turn it currently is */
    public Player getCurrentPlayer(){
        return this.playerQueue.peek();
    }

    /** returns the opposite player */
    public Player getOtherPlayer(Player current){
        if(current == this.player2){
            return player1;
        }
        return player2;
    }

    /** Returns the top piece on the board location or null if one does not exist */
    public Piece getBoardPiece(int row, int col){
        if(this.board[row][col].empty()){
            return null;
        }
        return this.board[row][col].peek();
    }

    public PieceStack getBoardStack(int row, int col){
        return this.board[row][col];
    }


    /** Pieces from the external stacks may only be played on empty tiles unless it is played on
     * an opponent's piece in a row, column, or diagonal where the opponent has three gobblets*/
    private boolean canGobbleFromStack(int row, int col){
        Player player = this.playerQueue.get(1);
        int numPieces = 0;

        // Three cards in the row
        for(int c = 0; c < 4; c++){
            if(this.board[row][c].peek().getPlayerName().equals(player.getName())){
                numPieces += 1;
            }
            if(numPieces == 3){
                return true;
            }
        }
        numPieces = 0;
        // Three cards in the column
        for(int r = 0; r < 4; r++){
            if(this.board[r][col].peek().getPlayerName().equals(player.getName())){
                numPieces += 1;
            }
            if(numPieces == 3){
                return true;
            }
        }
        numPieces = 0;

        //  Three cards in diagonal (if in a diagonal position)
        if(row == col || row + col == 3){
            for(int i = 0; i < 4; i++){
                if(this.board[i][i].peek().getPlayerName().equals(player.getName())){
                    numPieces += 1;
                }
                if(numPieces == 3){
                    return true;
                }
            }
            numPieces = 0;
            for(int j = 0; j < 4; j++){
                if(this.board[j][3-j].peek().getPlayerName().equals(player.getName())){
                    numPieces += 1;
                }
                if(numPieces == 3){
                    return true;
                }
            }
        }
        return false;
    }

    /** Adds an observer to the current list of observers so it can be updated*/
    public void addObserver(Observer<GobbletsModel> observer){
        this.observers.add(observer);
    }

    /** Updates each observer based on the model and the provided data*/
    public void alertObservers(ClientData data){
        for(var observer: this.observers){
            observer.update(this, data);
        }
        this.updating = false;
    }

}


