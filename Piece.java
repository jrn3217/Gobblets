package model;

/**
 * A piece in the Gobblets game
 */
public class Piece {

    /** Name associated with the Player that owns this piece*/
    private final String playerName;

    /** Integer representing the piece size **/
    private final int size;

    /**
     * Creates a new Piece
     * @param playerName name of the owner
     * @param size piece size
     */
    public Piece(String playerName, int size) {
        this.playerName = playerName;
        this.size = size;
    }

    /** returns the player name */
    public String getPlayerName(){
        return this.playerName;
    }

    /** returns the size of the piece*/
    public int getSize(){
        return this.size;
    }

    public boolean lessThan(Piece other){
        return this.size < other.getSize();
    }
}
