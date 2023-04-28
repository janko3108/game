import javafx.application.Application;
import javafx.event.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.geometry.*;
import java.io.*;

/**
 * 
 * A JavaFX application that generates a task where the user must click on
 * squares from left to right.
 * 
 * @author Janko Leskovac and Kristijan Nincevic
 * @version 1.0
 * @since 2023-04-20
 */
public class Task2 extends Application {
    private String[] squares = { "Red", "Blue", "Green", "Yellow", "Black" };
    private int n = squares.length;
    private boolean taskCompleted = false;
    private int currentIndex = 0;
    protected boolean complete = false;

    /**
     * Initializes and starts the JavaFX application.
     *
     * @param stage the primary stage for this application
     * @throws Exception if an exception occurs
     */
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    /**
     * Initializes and starts the JavaFX application.
     *
     * @param stage the primary stage for this application
     * @throws Exception if an exception occurs
     */
    @Override
    public void start(Stage stage) throws Exception {
        // set the window (stage) name
        stage.setTitle("Squares");

        Label lblInstructions = new Label("Click the squares from left to right!");
        lblInstructions.setStyle("-fx-font-size: 40px; -fx-font-weight: bold;");
        lblInstructions.setAlignment(Pos.CENTER);
        Pane pane = new Pane();

        // get random color squares
        for (int i = 0; i < n; i++) {
            Rectangle rec = new Rectangle(50, 50, Color.WHITE);
            rec.setStroke(Color.BLACK);
            rec.setX(100 + (i * 75));
            rec.setY(100);
            int index = i;
            rec.setOnMouseClicked(event -> {
                if (!taskCompleted && index == currentIndex) {
                    rec.setFill(Color.valueOf(squares[currentIndex]));
                    currentIndex++;
                    if (currentIndex == n) {
                        taskCompleted = true;
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Success!");
                        alert.showAndWait();
                        stage.close();
                        complete = true;
                    }
                }
            });
            pane.getChildren().addAll(rec);
        }

        VBox root = new VBox(10);
        root.setStyle("-fx-background-color:#DACF9C ;");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(lblInstructions, pane);

        // Create a scene with a specific size, and assign layout
        Scene scene = new Scene(root, 750, 500);

        stage.setScene(scene);
        stage.show();

    }

}
