package com.internshala.connectfour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private static final int COLUMNS =7;
	private static final int ROWS =6;
	private static final int CIRCLE_DIAMETER=80;
	private static final String DiscColour1="#24303E"; //RGB value
	private static final String DiscColour2="#4CAA88";

	private static  String Player_One="Player One";
	private static  String Player_Two="Player Two";

	private boolean isPlayerOneTurn=true; // Flag to avoid the same color disc being added

	private Disc[][] insertedDiscArray=new Disc[ROWS][COLUMNS]; //For Structural Changes:For Developer

	@FXML
	public GridPane rootGridPane;
	@FXML
	public Pane GamePane;
	@FXML
	public Label PlayerName;
	@FXML
	public TextField playerOneTextField, playerTwoTextField;
	@FXML
	public Button setNamesButton;

	private boolean isAllowedToInsert=true;

	public void CreatePlayground(){
		Shape rectangleWithHoles=createGameStructuralGrid();
		rootGridPane.add(rectangleWithHoles,0,1);

		List<Rectangle> rectangleList=createClickableColumns();
		for (Rectangle rectangle:rectangleList) {
			rootGridPane.add(rectangle, 0, 1);
		}
		setNamesButton.setOnAction(event -> {
			Player_One=playerOneTextField.getText();
			Player_Two=playerTwoTextField.getText();
			PlayerName.setText(isPlayerOneTurn?Player_One:Player_Two);
		});
	}

	private Shape createGameStructuralGrid(){
		Shape rectangleWithHoles=new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);

		for (int row=0;row<ROWS;row++){
			for (int col=0;col<COLUMNS;col++){
				Circle circle=new Circle();
				circle.setRadius(CIRCLE_DIAMETER/2);
				circle.setCenterX(CIRCLE_DIAMETER/2);
				circle.setCenterY(CIRCLE_DIAMETER/2);
				circle.setSmooth(true);

				circle.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
				circle.setTranslateY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);

				rectangleWithHoles=Shape.subtract(rectangleWithHoles,circle);
			}
		}
		rectangleWithHoles.setFill(Color.WHITE);

		return rectangleWithHoles;
	}

	private List<Rectangle> createClickableColumns(){

		List<Rectangle>rectangleList=new ArrayList<>();

		for (int col=0;col<COLUMNS;col++){
			Rectangle rectangle=new Rectangle(CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);

			rectangle.setOnMouseEntered(event -> {
				rectangle.setFill(Color.valueOf("#eeeeee26"));
			});
			rectangle.setOnMouseExited(event -> {
				rectangle.setFill(Color.TRANSPARENT);
			});
			final int column=col;
			rectangle.setOnMouseClicked(event -> {
				if (isAllowedToInsert) {
					isAllowedToInsert=false; //when the disc being dropped then no more disc will be inserted
					InsertDisc(new Disc(isPlayerOneTurn), column);
				}
			});

			rectangleList.add(rectangle);
		}
		return rectangleList;
	}

	private void InsertDisc(Disc disc,int col){

		int row=ROWS-1;
		while (row>=0){
			if(getDiscIfPresent(row,col)==null){
				break;
			}
			row--;
		}
		if(row<0){
			return;
		}

		insertedDiscArray[row][col]=disc; //For Structural Changes:For Developer
		GamePane.getChildren().add(disc);

		disc.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);

		int currentrow=row;
		TranslateTransition translateTransition=new TranslateTransition(Duration.seconds(0.5),disc);
		translateTransition.setToY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
		translateTransition.setOnFinished(event -> {
			isAllowedToInsert=true; //finally ,when disc is dropped allowed next player to insert disc
			if(gameEnded(currentrow,col)){
				gameOver();
				return;
			}
			isPlayerOneTurn=!isPlayerOneTurn;
			PlayerName.setText(isPlayerOneTurn?Player_One:Player_Two);
		});
		translateTransition.play();
	}

	private boolean gameEnded(int row,int col){
		List<Point2D> verticalPoints=IntStream.rangeClosed(row-3,row+3)   //range of row values=0,1,2,53,4,5
				.mapToObj(r->new Point2D(r,col))//0,3 1,3 2,3 4,3 5,3 ->Point2D x,y
				.collect(Collectors.toList());
		List<Point2D> horizontalPoints=IntStream.rangeClosed(col-3,col+3)
				.mapToObj(c->new Point2D(row,c))
				.collect(Collectors.toList());
		Point2D startPoint1=new Point2D(row - 3,col + 3);
		List<Point2D> diagonal1points=IntStream.rangeClosed(0,6)
				.mapToObj(i-> startPoint1.add(i,-i))
				.collect(Collectors.toList());
		Point2D startPoint2=new Point2D(row - 3,col - 3);
		List<Point2D> diagonal2points=IntStream.rangeClosed(0,6)
				.mapToObj(i-> startPoint2.add(i,i))
				.collect(Collectors.toList());

		boolean isEnded=checkCombinations(verticalPoints) ||checkCombinations(horizontalPoints)
				||checkCombinations(diagonal1points) ||checkCombinations(diagonal2points) ;

		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {
		int chain =0;
		for (Point2D point:points) {
			int rowTndexForArray= (int) point.getX();
			int columnIndexForArray= (int) point.getY();

			Disc disc=getDiscIfPresent(rowTndexForArray,columnIndexForArray);

			if(disc!=null && disc.isPlayerOneMove==isPlayerOneTurn){

				chain++;
				if (chain==4){
					return true;
				}
			}else {
				chain=0;
			}
		}
		return false;
	}

	private Disc getDiscIfPresent(int row,int col){
		if (row>=ROWS || row<0 || col>=COLUMNS || col<0)
			return null;
		return insertedDiscArray[row][col];
	}

	private void gameOver(){
		String winner=isPlayerOneTurn? Player_One:Player_Two;
		System.out.println("Winner "+winner);

		Alert alert=new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four Game");
		alert.setHeaderText("The Winner is: "+winner);
		alert.setContentText("Want to Paly again?");

		ButtonType yesButton=new ButtonType("Yes");
		ButtonType noButton=new ButtonType("No");
		alert.getButtonTypes().setAll(yesButton,noButton);

		Platform.runLater(()->{
			Optional<ButtonType> btnclicked =alert.showAndWait();
			if (btnclicked.isPresent() && btnclicked.get()==yesButton){
				resetGame();
			}else{
				Platform.exit();
				System.exit(0);
			}
		});
	}

	public void resetGame() {
		GamePane.getChildren().clear();
		for (int row=0;row<insertedDiscArray.length;row++){
			for (int col=0;col<insertedDiscArray[row].length;col++){
				insertedDiscArray[row][col]=null;
			}
		}
		isPlayerOneTurn=true;
		PlayerName.setText(Player_One);
		CreatePlayground();
	}

	private static class Disc extends Circle{
		private final boolean isPlayerOneMove;

		public Disc(boolean isPlayerOneMove){
			this.isPlayerOneMove=isPlayerOneMove;
			setRadius(CIRCLE_DIAMETER/2);
			setFill(isPlayerOneMove?Color.valueOf(DiscColour1):Color.valueOf(DiscColour2));
			setCenterX(CIRCLE_DIAMETER/2);
			setCenterY(CIRCLE_DIAMETER/2);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
