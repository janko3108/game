import javafx.application.Application;
import javafx.event.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.geometry.*;
import java.io.*;
import java.util.Random;

/**
 * 
 * This class represents a simple math game, Task1, where the user must solve a
 * random addition or subtraction problem.
 * The class extends the JavaFX Application class and overrides the start()
 * method to create the game's UI.
 * 
 * @author Janko Leskovac and Kristijan Nincevic
 * @version 1.0
 * @since 2023-04-20
 */
public class Task1 extends Application {
    private int input;
    protected boolean complete = false;

    /**
     * The main method of the application that launches the JavaFX application.
     * 
     * @param args - command line arguments
     * @throws Exception - any exception that might occur while launching the
     *                   application
     */
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    /**
     * Overrides the start method from Application and creates the game's UI.
     * 
     * @param stage - the main stage of the application
     * @throws Exception - any exception that might occur while creating the UI
     */
    @Override
    public void start(Stage stage) throws Exception {
        // set the window (stage) name
        stage.setTitle("Task1");

        // Layout - Border Pane
        VBox root = new VBox(10);
        root.setStyle("-fx-background-color: CYAN;");

        Random random = new Random();
        int operand1 = random.nextInt(10) + 1;
        int operand2 = random.nextInt(10) + 1;
        int operator = random.nextInt(2);

        String string = operator == 0 ? "+" : "-";
        input = operator == 0 ? operand1 + operand2 : operand1 - operand2;

        // question
        Label lblInput = new Label(operand1 + " " + string + " " + operand2 + " =");
        lblInput.setStyle("-fx-font-size: 150px; -fx-font-weight: bold;");

        // tf answer
        TextField tfAnswer = new TextField();
        tfAnswer.setStyle("-fx-font-size: 24px;");
        tfAnswer.setPrefWidth(tfAnswer.getPrefWidth());
        Button btnSubmit = new Button("Submit");
        btnSubmit.setStyle("-fx-font-size: 24px;");
        btnSubmit.setPrefWidth(btnSubmit.getPrefWidth());
        btnSubmit.setOnAction(event -> {
            if (tfAnswer.getText().equals(String.valueOf(input))) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Success!");
                alert.showAndWait();
                stage.close();
                complete = true;
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Wrong answer!");
                alert.showAndWait();

            }
        });

        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(tfAnswer, btnSubmit);
        hbox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(lblInput, hbox);
        root.setAlignment(Pos.CENTER);

        // Create a scene with a specific size, and assign layout
        Scene scene = new Scene(root, 750, 500);

        stage.setScene(scene);
        stage.show();

    }

}