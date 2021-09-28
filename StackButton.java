package gui;

import javafx.scene.control.Button;

public class StackButton extends Button {
    private final int stackNum;

    StackButton(int stackNum){
        this.stackNum = stackNum;
    }

    public int getStackNum(){
        return this.stackNum;
    }

}
