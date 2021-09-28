package model;

import javax.swing.*;

public class PlayerTimer {


    /** Remaining time in milliseconds */
    private int remainingTime;

    /** seconds on the clock*/
    private int seconds = 0;

    /** minutes on the clock*/
    private int minutes;

    /**
     * Creates a new Timer
     * @param minutes initial minutes on the timer
     */
    public PlayerTimer(int minutes){
        this.minutes = minutes;
        this.remainingTime = minutes * 60000;


    }

    /**
     * Updates the timer to represent a second passing
     * @return 0 if the clock has run out of time, 1 otherwise
     */
    public void updateTime(){
        if(!outOfTime()) {
            this.remainingTime -= 1000;
            this.minutes = this.remainingTime / 60000;
            this.seconds = (this.remainingTime / 1000) % 60;
        }
    }


    public String getTime(){
        return String.format("%02d", this.minutes) + ":" + String.format("%02d", this.seconds);

    }

    public boolean outOfTime(){
        return this.remainingTime == 0;
    }
}
