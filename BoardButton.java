package gui;

import javafx.scene.control.Button;

public class BoardButton extends Button {
    private int row;
    private int column;

    BoardButton(int row, int column){
        this.row = row;
        this.column = column;
    }

    public int getRow(){
        return this.row;
    }

    public int getColumn(){
        return this.column;
    }
}
