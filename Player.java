package model;

import java.util.Arrays;

/**
 * Represents a player in the Gobblets game
 * Stores their external piece stacks
 * (Will additionally track time, etc.)
 */
public class Player {

    /** Player name (unique from other players) */
    private String name;

    /** Stores the initial stack of pieces */
    private PieceStack[] externalStacks;

    /** A player's personal timer, may be null if there is an infinite time limit **/
    private PlayerTimer timer = null;

    /**
     * Creates a new player and initializes the external stacks
     * @param name Player username
     * @param initTime initial time on the player timer, may be no timer
     */
    public Player(String name, String initTime){
        this.name= name;
        this.externalStacks = initExternalStacks();

        if(!initTime.equals("No time limit")){
            String[] t = initTime.split(":");
            this.timer = new PlayerTimer(Integer.parseInt(t[0]));
        }
    }


    /**
     * Method used to initialize the external stacks
     * @return an array of 3 external stacks
     */
    private PieceStack[] initExternalStacks(){
        PieceStack[] stacks = new PieceStack[3];
        for(int s = 0; s < 3; s++){
            stacks[s] = new PieceStack("player");
            for(int p = 4; p > 0; p--){
                stacks[s].push(new Piece(this.name, p));
            }
        }
        return stacks;
    }

    public String getName(){
        return this.name;
    }

    /**
     * Determines if the given piece is owned by this player
     * @param piece provided piece
     * @return true if this is the player's piece, false otherwise
     */
    public boolean myPiece(Piece piece){
        return piece.getPlayerName().equals(this.name);
    }

    /**
     * Determines if one of the external stacks is empty
     * @param stackNum determines which stack to check
     * @return true if the stack is empty, false otherwise
     */
    public boolean emptyStack(int stackNum){
        return externalStacks[stackNum].empty();
    }

    /** Returns the top Piece of the stack */
    public Piece peekStack(int stackNum){
        return externalStacks[stackNum].peek();
    }

    /** Removes and returns the top piece from the stack */
    public Piece popStack(int stackNum){
        return externalStacks[stackNum].pop();
    }

    /** Gets a specific stack **/
    public PieceStack getStack(int stackNum){
        return externalStacks[stackNum];
    }

    /** Decreases the players timer by one second */
    public void updateTime(){
        this.timer.updateTime();
    }

    /** Gets the string value of the player's time */
    public String getTime(){
        return this.timer.getTime();
    }

    public boolean outOfTime(){
        return this.timer.outOfTime();
    }
}
