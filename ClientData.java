package model;

public class ClientData {
    private String message;
    private boolean reconstruct;


    /**
     * Data sent from the model that is used to update the board
     * @param message message to display on the output
     * @param reconstruct boolean indicating whether the board and external stacks need to be rebuilt
     */
    public ClientData(String message, boolean reconstruct){
        this.message = message;
        this.reconstruct = reconstruct;
    }


    public String getMessage() {
        return this.message;
    }

    public boolean getReconstruct() {
        return this.reconstruct;
    }
}
