
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Font;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.geometry.*;
import java.io.*;
import javafx.util.Duration;

/**
 * 
 * This class is used to create a credit card swipe animation using JavaFX.
 * The user can drag the credit card image to the right, and when it reaches a
 * certain point,
 * an alert will pop up indicating that the swipe was successful.
 * If the user releases the card before it reaches the required point, the card
 * will be
 * translated back to its original position using an animation.
 * 
 * @author Janko Leskovac and Kristijan Nincevic
 * @version 1.0
 * @since 2023-04-20
 */
public class Task3 extends Application {
    private ImageView cardImageView;
    private double x;
    private double y;

    private static final int scene_w = 400;
    private static final int scene_h = 300;
    private static final int card_w = 100;
    private static final int card_h = 100;
    private static final int end = 400;
    protected boolean complete = false;

    /**
     * This method launches the application.
     *
     * @param args the command line arguments
     * @throws Exception if an exception occurs
     */
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    /**
     * This method is used to create the credit card swipe animation.
     *
     * @param stage the primary stage of the application
     * @throws Exception if an exception occurs
     */
    @Override
    public void start(Stage stage) throws Exception {
        // set the window (stage) name
        stage.setTitle("Task3");
        Image cardImage = new Image("credit.png");

        Label lblSwipe = new Label("Swipe the card!");
        lblSwipe.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblSwipe.setTextFill(Color.WHITE);

        Line line = new Line(end, 0, end, card_h);
        line.setStrokeWidth(6);
        line.setStroke(Color.GREEN);

        cardImageView = new ImageView(cardImage);
        cardImageView.setFitWidth(card_w);
        cardImageView.setFitHeight(card_h);

        cardImageView.setOnMousePressed((MouseEvent event) -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });

        cardImageView.setOnMouseDragged((MouseEvent event) -> {
            double distance = event.getSceneX() - x;
            if (distance > 0 && distance < end - card_w) {
                cardImageView.setTranslateX(distance);

            }
        });

        cardImageView.setOnMouseReleased((MouseEvent event) -> {
            if (cardImageView.getTranslateX() >= 390 - card_w) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText("Success!");
                alert.showAndWait();
                stage.close();
                complete = true;
            } else {

                TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), cardImageView);
                transition.setToX(0);
                transition.play();

            }
        });

        StackPane root = new StackPane(cardImageView, line, lblSwipe);
        root.setStyle("-fx-background-color:#747E8B;");
        StackPane.setMargin(lblSwipe, new Insets(30, 0, 0, 0));
        lblSwipe.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        StackPane.setAlignment(cardImageView, Pos.CENTER_LEFT);
        StackPane.setAlignment(line, Pos.CENTER_RIGHT);
        StackPane.setAlignment(lblSwipe, Pos.TOP_CENTER);

        // Create a scene with a specific size, and assign layout
        Scene scene = new Scene(root, scene_w, scene_h);

        stage.setScene(scene);
        stage.show();

    }

}
