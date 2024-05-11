package com.internshala.connectfour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
      FXMLLoader loader=new FXMLLoader(getClass().getResource("game.fxml"));
      GridPane rootGridPane=loader.load();

      MenuBar menuBar= createMenu();

      Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
      menuPane.getChildren().add(menuBar);
      menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

      controller =loader.getController();
      controller.CreatePlayground();

      Scene scene=new Scene(rootGridPane);

      primaryStage.setScene(scene);
      primaryStage.setTitle("ConnectFour");
      primaryStage.setResizable(false);
      primaryStage.show();
    }
    private MenuBar createMenu(){

    	Menu filemenu =new Menu("File");

	    MenuItem newGame =new MenuItem("New Game");
	    newGame.setOnAction(event -> {
	    	controller.resetGame();
	    });
	    MenuItem resetGame =new MenuItem("Reset Game");
	    resetGame.setOnAction(event -> {
	    	controller.resetGame();
	    });
	    SeparatorMenuItem separatorMenuItem=new SeparatorMenuItem();
	    MenuItem exitGame =new MenuItem("Exit Game");
	    exitGame.setOnAction(event -> {
		    Platform.exit();
		    System.exit(0);
	    });

	    filemenu.getItems().addAll(newGame,resetGame,separatorMenuItem,exitGame);

	    Menu helpmenu =new Menu("Help");
	    MenuItem aboutgame=new MenuItem("About Game");
	    aboutgame.setOnAction(event -> {
	    	aboutGame();
	    });
	    SeparatorMenuItem separator=new SeparatorMenuItem();
	    MenuItem aboutme=new MenuItem("About Me");
	    aboutme.setOnAction(event -> {
	    	aboutMe();
	    });
	    helpmenu.getItems().addAll(aboutgame,separator,aboutme);

	    MenuBar menuBar=new MenuBar();
	    menuBar.getMenus().addAll(filemenu,helpmenu);

	    return menuBar;
    }

	private void aboutMe() {
		Alert alert=new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About The Developer");
		alert.setHeaderText("Kusuma Vishwesh");
		alert.setContentText("I love to play around with code and create games. Connect four game "+
				"is one of them. In the free time i love to play with code "+
				"And i like to spend time with near and dear");
		alert.show();
	}

	private void aboutGame() {
		Alert alert=new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About ConnectFour Game");
		alert.setHeaderText("How To Play?");
		alert.setContentText("Connect Four is a two-player connection game in which "+
				"the players first choose a color and then take turns dropping colored"+
				" discs from the top into a seven-column, six-row vertically suspended"+
				" grid. The pieces fall straight down, occupying the next available "+
				"space within the column. The objective of the game is to be the first "+
				"to form a horizontal, vertical, or diagonal line of four of one's own discs."+
				" Connect Four is a solved game. The first player can always win by playing the right moves.");
		alert.show();
	}

	public static void main(String[] args) {
        launch(args);
    }
}
