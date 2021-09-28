package gui;

import javafx.application.Application;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import common.Observer;
import model.*;

import java.awt.event.ActionEvent;


public class GobbletsGUI extends Application implements Observer<GobbletsModel> {

    private static final int BOARD_BUTTON_SIZE = 180;
    private static final int STACK_BUTTON_SIZE = 80;

    // All screens

    /** Model that will represent an individual game*/
    private GobbletsModel model;

    /** Stage that the primary application will be running on*/
    private Stage mainStage;

    // Game screen

    /** Message displayed at the top of a game screen*/
    private Label message;

    /** Displays the name of the active player*/
    private Label player;

    /** Displays a timer if applicable */
    private Label timer;

    /** Displays the stack hovered upon if applicable */
    private Label stackDisplay;

    /** A grid of buttons acting as the game board*/
    private Button[][] boardGrid;

    /** An array of buttons acting as the active player's external stacks*/
    private Button[] externalStacks;

    /** Service that runs the game timer*/
    private TimerService timerService;

    /** Service that displays the board stack when hovered over*/
    private BoardStackService boardService;


    private void createGame(String timeSetting, boolean includeStackDisplay){
        this.model = new GobbletsModel(new Player("Player 1", timeSetting),
                new Player("Player 2", timeSetting));
        this.boardGrid = new Button[4][4];
        this.externalStacks = new Button[3];
        this.model.addObserver(this);

        boolean hasTimer = !timeSetting.equals("No time limit");

        if(hasTimer){
            this.timerService = new TimerService();
            updateScene(createGameDisplay(true, includeStackDisplay));
            this.timerService.start();
        } else {
            this.timerService = null;
            updateScene(createGameDisplay(false, includeStackDisplay));

        }
    }

    private void exitGame(){
        if(this.timerService != null) {
            this.timerService.cancel();
        }
        this.updateScene(createMenu());
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.mainStage = stage;
        stage.setTitle("Gobblets");
        stage.setFullScreen(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setScene(new Scene(createMenu()));
        stage.show();
    }

    /**
     * Loads a given scene onto the primary stage
     * @param value new root of the scene
     */
    private void updateScene(Parent value){
        this.mainStage.getScene().setRoot(value);
    }

    /**
     * Creates the initial Game scene
     * @return scene
     */
    private Parent createGameDisplay(Boolean hasTimer, Boolean showStackDisplay){
        BorderPane borderPane = new BorderPane();

        // Make the top (Exit button)
        Button exitButton = new Button("X");
        exitButton.setOnAction(ActionEvent -> this.exitGame());
        FlowPane exitPane = new FlowPane(exitButton);
        borderPane.setTop(exitPane);
        exitPane.setAlignment(Pos.TOP_RIGHT);

        // Makes the timer, message labels
        this.message = new Label("message go here");
        this.message.setFont(new Font("Helvetica", 30));

        this.player = new Label(this.model.getCurrentPlayer().getName());
        this.player.setFont(new Font("Helvetica", 36));

        this.stackDisplay = new Label();
        this.stackDisplay.setFont(new Font("Helvetica", 24));

        this.timer = new Label();
        this.timer.setFont(new Font("Helvetica", 36));
        if(hasTimer){
            updateClock();
        }

        VBox info = new VBox(40);
        HBox playerAndTimer = new HBox(20);
        playerAndTimer.getChildren().addAll(this.player, this.timer);
        info.getChildren().addAll(playerAndTimer, this.message, this.stackDisplay);
        BorderPane.setMargin(info, new Insets(12, 12, 12, 24));
        borderPane.setCenter(info);
        info.setBackground(new Background(new BackgroundFill(Color.AQUA, null, null)));

        // Makes the game board
        GridPane gridPane = new GridPane();
        BorderPane.setMargin(gridPane, new Insets(0, 12, 12, 12));
        borderPane.setLeft(gridPane);
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                BoardButton button = new BoardButton(row, col);
                button.setMinSize(BOARD_BUTTON_SIZE, BOARD_BUTTON_SIZE);
                button.setMaxSize(BOARD_BUTTON_SIZE, BOARD_BUTTON_SIZE);
                button.setStyle("-fx-focus-color: transparent;");
                button.setOnAction(ActionEvent -> model.selectBoardPiece(button.getRow(), button.getColumn()));

                if(showStackDisplay){
                    button.setOnMouseEntered(ActionEvent -> {
                        if(this.boardService != null){
                            this.boardService.cancel();
                            this.boardService = null;
                        }
                        this.boardService = new BoardStackService(button.getRow(), button.getColumn());
                        this.boardService.start();
                    });

                    button.setOnMouseExited(ActionEvent -> {
                        if(this.boardService != null){
                            this.boardService.cancel();
                            this.boardService = null;
                        }
                        this.stackDisplay.setText("");
                    });
                }

                gridPane.add(button, col, row);
                this.boardGrid[row][col] = button;

                //TODO set onMouseEnters / exits (only if the setting is selected)
            }
        }

        // Make the External stack display
        HBox stacks = new HBox(20);
        BorderPane.setMargin(stacks, new Insets(0, 12, 12, 12));

        Button stack0 = new StackButton(0);
        stack0.setMaxSize(STACK_BUTTON_SIZE, STACK_BUTTON_SIZE);
        stack0.setMinSize(STACK_BUTTON_SIZE, STACK_BUTTON_SIZE);
        stack0.setText("1");
        this.externalStacks[0] = stack0;
        stack0.setOnAction(ActionEvent -> model.selectStackPiece(0));

        Button stack1 = new StackButton(1);
        stack1.setMaxSize(STACK_BUTTON_SIZE, STACK_BUTTON_SIZE);
        stack1.setMinSize(STACK_BUTTON_SIZE, STACK_BUTTON_SIZE);
        stack1.setText("1");
        this.externalStacks[1] = stack1;
        stack1.setOnAction(ActionEvent -> model.selectStackPiece(1));

        Button stack2 = new StackButton(2);
        stack2.setMaxSize(STACK_BUTTON_SIZE, STACK_BUTTON_SIZE);
        stack2.setMinSize(STACK_BUTTON_SIZE, STACK_BUTTON_SIZE);
        this.externalStacks[2] = stack2;
        stack2.setText("1");
        stack2.setOnAction(ActionEvent -> model.selectStackPiece(2));

        stacks.getChildren().addAll(stack0, stack1, stack2);
        borderPane.setBottom(stacks);
        stacks.setAlignment(Pos.BASELINE_LEFT);

        return borderPane;
    }


    /**
     * Creates the scene that allows Local Game setup
     * @return scene
     */
    private Parent createLocalGameSetup(){
        BorderPane borderPane = new BorderPane();
        VBox settings = new VBox(20);

        String[] timerValues = {"No time limit", "1:00", "5:00", "10:00", "15:00", "20:00", "30:00"};

        ChoiceBox<String> timers = new ChoiceBox<>();
        timers.getItems().addAll(timerValues);
        timers.setValue("No time limit");

        CheckBox boardStackDisplay = new CheckBox();
        boardStackDisplay.setText("Display full board stack when hovered over?");
        boardStackDisplay.setSelected(true);
        settings.getChildren().addAll(timers, boardStackDisplay);

        borderPane.setCenter(settings);
        settings.setAlignment(Pos.CENTER);


        Button start = new Button("Start Game");
        start.setMinSize(150, 60);
        start.setMaxSize(150, 60);
        start.setFont(new Font("Helvetica", 18));
        start.setOnAction(ActionEvent -> this.createGame(timers.getValue(), boardStackDisplay.isSelected()));
        FlowPane startPane = new FlowPane(start);
        BorderPane.setMargin(startPane, new Insets(0, 0, 0, 20));
        borderPane.setBottom(startPane);
        startPane.setAlignment(Pos.CENTER);

        return borderPane;
    }


    /**
     * Creates the Main Menu Scene
     * @return scene
     */
    private Parent createMenu(){
        BorderPane borderPane = new BorderPane();

        Label title = new Label("Gobblets");
        title.setFont(new Font("Helvetica", 96));

        Button exitButton = new Button("X");
        exitButton.setOnAction(ActionEvent -> this.mainStage.close());

        BorderPane top = new BorderPane();
        top.setCenter(title);
        FlowPane exitPane = new FlowPane(exitButton);
        top.setTop(exitPane);
        exitPane.setAlignment(Pos.TOP_RIGHT);
        borderPane.setTop(top);


        VBox gameModes = new VBox();
        Button localGame = new Button("Local Game");
        localGame.setOnAction(ActionEvent -> this.updateScene(createLocalGameSetup()));
        gameModes.getChildren().addAll(localGame);
        borderPane.setCenter(gameModes);
        gameModes.setAlignment(Pos.CENTER);


        return borderPane;
    }

    /**
     * Updates the GUI during Gameplay
     * @param model current game model
     * @param clientData additional data that is used to update the gui
     */
    @Override
    public void update(GobbletsModel model, ClientData clientData) {

        this.stackDisplay.setText("");

        if(model.getWinner() != null){
            // Display end game screen (ignores other client data)
            if(this.timerService != null) {
                this.timerService.cancel();
            }
            if(this.boardService != null){
                this.boardService.cancel();
            }

            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 4; c++) {
                    this.boardGrid[r][c].disarm();
                }
            }

            for(int s = 0; s < 3; s++ ){
                this.externalStacks[s].disarm();
            }

            this.updateClock();
            this.message.setText(model.getWinner().getName() + " has won the game");
        }
        else {
            this.message.setText(clientData.getMessage());
            this.player.setText(model.getCurrentPlayer().getName());

            if (clientData.getReconstruct()) {
                // reconstructs the board and external stacks, swaps the displayed timers
                this.updateClock();
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        StringBuilder pieceText = new StringBuilder();
                        Piece piece = model.getBoardPiece(r, c);
                        if (piece != null) {
                            pieceText.append(piece.getSize()).append(" (").append(piece.getPlayerName()).append(")");
                        }
                        this.boardGrid[r][c].setText(String.valueOf(pieceText));
                    }
                }
                for (int s = 0; s < 3; s++) {
                    if (!model.getCurrentPlayer().emptyStack(s)) {
                        this.externalStacks[s].setText(String.valueOf(model.getCurrentPlayer().
                                peekStack(s).getSize()));
                    } else {
                        this.externalStacks[s].setText("");
                    }
                }
            }
            if(this.boardService != null){
                boardService.restart();
            }
        }
    }

    public void updateClock(){
        if(this.timerService != null){
            Player player = this.model.getCurrentPlayer();
            this.timer.setText(player.getTime() + " | " + this.model.getOtherPlayer(player).getTime());
        }
    }

    /**
     * A timer service used to manage the player timers
     */
    private class TimerService extends ScheduledService<Player> {

        @Override
        protected Task<Player> createTask() {
            return new Task<>() {
                @Override
                protected Player call() throws Exception {
                    do {
                        try{
                            Thread.sleep(1000);
                        } catch(InterruptedException e){
                            if(!isCancelled()){
                                System.out.println(e.getMessage());
                            }
                        }

                    } while(model.isSwapping());
                    Player player = model.getCurrentPlayer();
                    if(!isCancelled()){
                        player.updateTime();
                    }
                    return player;
                }

                @Override
                protected void succeeded(){
                    updateClock();
                    if(getValue().outOfTime()){
                        model.timedOut(getValue());
                        while(true){
                            if(!model.isUpdating()){
                                break;
                            }
                        }
                        model.alertObservers(new ClientData("Timeout", false));
                    }
                }

                @Override
                protected void failed(){
                    Throwable error = getException();
                    System.out.println("Error in the timerService: " + error.getMessage());
                }
            };
        }
    }

    private class BoardStackService extends Service<String>{
        private final int row;
        private final int column;

        public BoardStackService(int row, int column){
            this.row = row;
            this.column = column;
        }

        @Override
        protected Task<String> createTask() {
            return new Task<String>() {
                @Override
                protected String call() throws Exception {
                    String stackString = null;
                    try{
                        Thread.sleep(1250);
                    } catch(InterruptedException e){
                        if(!isCancelled()){
                            System.out.println(e.getMessage());
                        }
                    }
                    if(!isCancelled()){
                        PieceStack boardStack = model.getBoardStack(row, column);
                        for(Piece piece: boardStack){
                            String temp = stackString;
                            stackString = piece.getPlayerName() + "| Size = " + piece.getSize();
                            if(temp != null){
                                stackString = stackString + "\n" + temp;
                            }
                        }
                    }
                    return stackString;
                }
            };
        }

        @Override
        protected void succeeded(){
            stackDisplay.setText(getValue());
        }

        @Override
        protected void failed(){
            Throwable error = getException();
            System.out.println("Error: " + error.getMessage());
        }

        @Override
        protected void cancelled(){
            stackDisplay.setText("");
        }
    }



    public static void main(String[] args) {
        Application.launch(args);
    }
}