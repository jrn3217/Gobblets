package model;

import java.util.Stack;


public class PieceStack extends Stack<Piece> {
    private final String locationTag;

    public PieceStack(String locationTag){
        this.locationTag = locationTag;
    }

    public String getLocationTag(){
        return this.locationTag;
    }

}
