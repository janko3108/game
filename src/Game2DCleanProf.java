import javafx.application.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.text.*;
import javafx.scene.transform.Rotate;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.geometry.*;
import javafx.animation.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import javafx.scene.input.KeyEvent;

/**
 * AmongUSStarter with JavaFX and Threads
 * Loading imposters
 * Loading background
 * Control actors and backgrounds
 * Create many number of imposters - random controlled
 * RGB based collision
 * Collsion between two imposters
 * @author Janko Leskovac and Kristijan Nincevic
 * @version 1.0
 * @since 2023-04-20
 */

public class Game2DCleanProf extends Application {
   // Window attributes
   private Stage stage;
   private Scene scene;

   private static String[] args;

   private final static String CREWMATE_IMAGE = "Blue.png"; // file with icon for a racer
   private final static String CREWMATE_RUNNERS = "Yellow.png"; // file with icon for a racer
   private final static String BACKGROUND_IMAGE = "background3.0.png";
   private final static String CREWMATE_RUNNING = "running.gif";
   private final ImageView aPicView = new ImageView(CREWMATE_IMAGE);
   private int OldX = -1;
   private int OldY = -1;
   private String lastMove = "";

   private MediaPlayer mediaPlayer;
   String fileName = "music.mp3";

   private Menu menu;
   private MenuBar menuBar;
   private MenuItem miAudio;

   private Slider volumeSlider;

   // crewmates
   private CrewmateRacer masterCrewmate = null;
   private ArrayList<CrewmateRacer> robotCrewmates = new ArrayList<>();

   // movable background
   private MovableBackground movableBackground = null;

   private Image backgroundCollision = null;

   // Animation timer
   private AnimationTimer timer = null;
   private int counter = 0;
   private boolean moveUP = false, moveDown = false, moveRight = false, moveLeft = false;

   private StackPane root;
   private FlowPane topPane;
   private FlowPane midPane;
   private FlowPane bottomPane;
   private Button btnAudio;
   private Button btnPlay;

   private Button audioSettings;
   private Button colorPicker;
   private int sceneXx = 1200;
   private int sceneYy = 700;

   public String status = "Not completed yet.";
   public String status2 = "Not completed yet.";
   public String status3 = "Not completed yet.";

   public static final ObservableList<String> color = FXCollections.observableArrayList("Blue", "Yellow",
         "Purple", "Pink", "Orange", "Light Green", "Green", "Dark Green",
         "Cyan", "Beige", "Red", "Brown");

   private ComboBox<String> cmbColor = new ComboBox<>(color);

   private StackPane paneStack;
   private Text text;

   private String name;

   private String image = "Blue.png";

   private Socket socket;
   public static final int SERVER_PORT = 12345;

   private ObjectOutputStream oos;
   private ObjectInputStream ois;

   // main program
   /**
    * 
    * The main class of the JavaFX application.
    * 
    * This class launches the application and sets the command-line arguments.
    */
   public static void main(String[] _args) {
      args = _args;
      launch(args);
   }

   // start() method, called via launch
   public void start(Stage _stage) {
      // stage seteup
      stage = _stage;
      stage.setTitle("Among us - Final Project K.N. & J.L.");
      stage.setOnCloseRequest(
            new EventHandler<WindowEvent>() {
               public void handle(WindowEvent evt) {
                  System.exit(0);
               }
            });

      // root pane

      btnPlay = new Button("Play");
      btnAudio = new Button("Turn On Audio");
      btnAudio.setOnAction(new EventHandler<ActionEvent>() {

         @Override
         public void handle(ActionEvent arg0) {
            // TODO Auto-generated method stub
            if (btnAudio.getText().equals("Turn On Audio")) {
               doSound(fileName);
               btnAudio.setText("Turn Off Audio");
            } else if (btnAudio.getText().equals("Turn Off Audio")) {
               doSoundOff(fileName);
               btnAudio.setText("Turn On Audio");
            }

         }
      });

      btnPlay.setPrefWidth(150);
      btnPlay.setPrefHeight(50);

      btnPlay.setOnAction(new EventHandler<ActionEvent>() {

         @Override
         public void handle(ActionEvent arg0) {

            String[] validIPs = { "127.0.0.1", "localhost" };
            String ipAdDress = "";

            int clientCount = 0;
            while (!Arrays.asList(validIPs).contains(ipAdDress)) {
               TextInputDialog dialog = new TextInputDialog();

               dialog.setTitle("Connect to server");
               dialog.setHeaderText("Enter the server's IP address: ");
               dialog.setContentText("IP Address:");

               Optional<String> result = dialog.showAndWait();
               if (result.isPresent()) {
                  ipAdDress = result.get();
               }
            }

            try (Socket s = new Socket()) {
               s.connect(new InetSocketAddress(ipAdDress, SERVER_PORT), 1000);
               System.out.println("Server is running...");
               socket = new Socket(ipAdDress, SERVER_PORT);
               clientCount++;
               System.out.println("Client " + clientCount + " connected..");
            } catch (UnknownHostException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            } catch (IOException e) {
               Alert alert = new Alert(AlertType.WARNING);
               alert.setTitle("Server not running!");
               alert.setHeaderText(null);
               alert.setContentText("Server is currently not running! Shutting down the game.");
               alert.showAndWait();
               System.exit(0);
               e.printStackTrace();
            }

            TextInputDialog tid = new TextInputDialog();
            tid.setTitle("Enter your character name: ");
            tid.setHeaderText(null);
            tid.setContentText("Name: ");
            tid.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
               if (!newValue.matches("[a-zA-Z]+") && !newValue.isEmpty() || newValue.length() > 16) {
                  Alert alert = new Alert(AlertType.WARNING);
                  alert.setTitle("Invalid input!");
                  alert.setHeaderText(null);
                  alert.setContentText("Name must be from 1 to 16 characters, letters only.");
                  alert.showAndWait();
                  tid.getEditor().setText(oldValue);
               }
            });
            Optional<String> result = tid.showAndWait();
            if (result.isPresent()) {
               name = result.get();
            } else {
               Alert alert = new Alert(AlertType.WARNING);
               alert.setTitle("Cancelled");
               alert.setHeaderText("Warning");
               alert.setContentText("You must enter a name to continue.");
               alert.showAndWait();
            }
            if (name != null && !name.isEmpty()) {

               try {
                  ClientName cName = new ClientName(name);
                  oos = new ObjectOutputStream(socket.getOutputStream());
                  oos.writeObject(cName);
                  oos.flush();
               } catch (IOException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
               initializeScene();
            }

         }

      });

      /* Adjust volume */

      volumeSlider = new Slider(0, 100, 50);
      volumeSlider.setShowTickLabels(true);
      volumeSlider.setShowTickMarks(true);
      volumeSlider.setMajorTickUnit(50);
      volumeSlider.setMinorTickCount(5);
      volumeSlider.setBlockIncrement(10);

      volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

         @Override
         public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
            double volume = newValue.doubleValue() / 100.0;
            mediaPlayer.setVolume(volume);
         }

      });
      volumeSlider.setDisable(true);

      audioSettings = new Button("Audio settings");
      audioSettings.setPrefWidth(100);
      audioSettings.setPrefHeight(35);

      audioSettings.setOnAction(e -> {
         FlowPane audioSettingsPane = new FlowPane();
         FlowPane audioSettingsPane2 = new FlowPane();
         FlowPane audioSettingsPane3 = new FlowPane();
         FlowPane audioSettingsPane4 = new FlowPane();

         Button back = new Button("<-- Back to main screen");

         back.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
               // TODO Auto-generated method stub
               stage.setScene(scene);
            }
         });
         Label adjustAudio = new Label("Adjust music volume: ");

         audioSettingsPane3.getChildren().addAll(back);
         audioSettingsPane.getChildren().addAll(btnAudio);
         audioSettingsPane2.getChildren().addAll(volumeSlider);
         audioSettingsPane4.getChildren().addAll(adjustAudio);

         audioSettingsPane.setAlignment(Pos.CENTER);
         audioSettingsPane2.setAlignment(Pos.CENTER);
         audioSettingsPane4.setAlignment(Pos.CENTER);
         audioSettingsPane3.setAlignment(Pos.TOP_LEFT);

         GridPane audioPane = new GridPane();
         audioPane.getChildren().addAll(audioSettingsPane3, audioSettingsPane, audioSettingsPane4, audioSettingsPane2);
         audioPane.setRowIndex(audioSettingsPane3, 0);
         audioPane.setRowIndex(audioSettingsPane, 1);
         audioPane.setRowIndex(audioSettingsPane2, 3);
         audioPane.setRowIndex(audioSettingsPane4, 2);
         audioPane.setVgap(20);
         audioPane.setAlignment(Pos.CENTER);
         Scene audioSettingsScene = new Scene(audioPane, 1200, 700);
         Image backgroundImage = new Image("amongus.jpg");
         BackgroundImage background = new BackgroundImage(backgroundImage, null, null, null, null);
         audioPane.setBackground(new Background(background));
         stage.setScene(audioSettingsScene);
         stage.show();
      });
      colorPicker = new Button("Pick the color of your character");
      colorPicker.setPrefWidth(100);
      colorPicker.setPrefHeight(35);
      colorPicker.setOnAction(e -> {
         FlowPane pane1 = new FlowPane();
         FlowPane pane2 = new FlowPane();
         FlowPane pane3 = new FlowPane();

         Button back = new Button("<-- Back to main screen");

         back.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
               // TODO Auto-generated method stub
               stage.setScene(scene);
            }
         });
         Label lblCharColor = new Label("Pick the color of your character: ");

         pane1.getChildren().addAll(back);
         pane2.getChildren().addAll(cmbColor);
         pane1.setAlignment(Pos.TOP_LEFT);
         pane2.setAlignment(Pos.CENTER);
         pane3.getChildren().addAll(lblCharColor);
         pane3.setAlignment(Pos.CENTER);

         cmbColor.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
               String selectedColor = cmbColor.getValue();

               if (selectedColor == null) {
                  selectedColor = "Blue";
               } else {
                  image = selectedColor + ".png";
               }
            }
         });

         GridPane colorPane = new GridPane();
         colorPane.getChildren().addAll(pane1, pane3, pane2);
         colorPane.setRowIndex(pane1, 0);
         colorPane.setRowIndex(pane2, 2);
         colorPane.setRowIndex(pane3, 1);
         colorPane.setVgap(20);
         colorPane.setAlignment(Pos.CENTER);
         Scene colorPickerScene = new Scene(colorPane, 1200, 700);
         Image backgroundImage = new Image("amongus.jpg");
         BackgroundImage background = new BackgroundImage(backgroundImage, null, null, null, null);
         colorPane.setBackground(new Background(background));
         stage.setScene(colorPickerScene);
         stage.show();
      });

      topPane = new FlowPane(20, 20);
      topPane.getChildren().addAll(btnPlay);
      topPane.setAlignment(Pos.CENTER);
      midPane = new FlowPane(20, 20);
      midPane.getChildren().addAll(audioSettings);
      midPane.setAlignment(Pos.CENTER);
      bottomPane = new FlowPane(20, 20);
      bottomPane.getChildren().addAll(colorPicker);
      bottomPane.setAlignment(Pos.CENTER);

      GridPane gPane = new GridPane();
      gPane.getChildren().addAll(topPane, midPane, bottomPane);
      gPane.setRowIndex(topPane, 0);
      gPane.setRowIndex(midPane, 1);
      gPane.setRowIndex(bottomPane, 2);
      gPane.setVgap(10);
      gPane.setAlignment(Pos.CENTER);
      // midPane.getChildren().addAll(audioSettings);

      Image backgroundImage = new Image("amongus.jpg");
      BackgroundImage background = new BackgroundImage(backgroundImage, null, null, null, null);
      gPane.setBackground(new Background(background));

      root = new StackPane();

      scene = new Scene(gPane, sceneXx, sceneYy);

      stage.setScene(scene);
      stage.getIcons().add(new Image("amongus.jpg"));
      stage.show();
   }

   // start the game scene
   private void initializeScene() {

      masterCrewmate = new CrewmateRacer(name);
      for (int i = 0; i < 5; i++) {
         CrewmateRacer cR = new CrewmateRacer(name);
         robotCrewmates.add(cR);
      }
      // create background
      CrewmateRacer cr = new CrewmateRacer(name);
      movableBackground = new MovableBackground(cr);

      // add background
      this.root.getChildren().add(movableBackground);
      // add to the root
      this.root.getChildren().add(masterCrewmate);
      this.root.getChildren().addAll(robotCrewmates);
      status = "Not completed yet.";
      status2 = "Not completed yet.";
      status3 = "Not completed yet.";

      text = new Text(
            "Task 1: Math " + status + "\nTask 2: Squares " + status2 + "\nTask 3: Swipe the card " + status3);
      text.setFill(Color.WHITE);

      paneStack = new StackPane(text);
      paneStack.setAlignment(Pos.TOP_RIGHT);

      this.root.getChildren().add(paneStack);

      scene = new Scene(root, 1200, 700);

      stage.setScene(scene);
      stage.show();

      // KEYBOARD CONTROL

      scene.setOnKeyPressed(
            new EventHandler<KeyEvent>() {

               @Override
               public void handle(KeyEvent event) {
                  switch (event.getCode()) {
                     case UP:
                        moveUP = true;
                        // masterCrewmate.setImage(CREWMATE_RUNNING);
                        break;
                     case DOWN:
                        moveDown = true;
                        // masterCrewmate.setImage(CREWMATE_RUNNING);
                        break;
                     case LEFT:
                        moveLeft = true;
                        break;
                     case RIGHT:
                        moveRight = true;
                        break;
                     case A:
                        moveLeft = true;
                        break;
                     case W:
                        moveUP = true;
                        // masterCrewmate.setImage(CREWMATE_RUNNING);
                        break;
                     case D:
                        moveRight = true;
                        // masterCrewmate.setImage(CREWMATE_RUNNING);
                        break;
                     case S:
                        moveDown = true;
                        // masterCrewmate.setImage(CREWMATE_RUNNING);
                        break;
                  }

               }
            });

      scene.setOnKeyReleased(
            new EventHandler<KeyEvent>() {
               @Override
               public void handle(KeyEvent event) {
                  switch (event.getCode()) {
                     case UP:
                        moveUP = false;
                        // masterCrewmate.setImage(CREWMATE_IMAGE);
                        break;
                     case DOWN:
                        moveDown = false;
                        // masterCrewmate.setImage(CREWMATE_IMAGE);
                        break;
                     case LEFT:
                        moveLeft = false;
                        // masterCrewmate.setImage(CREWMATE_IMAGE);
                        break;
                     case RIGHT:
                        moveRight = false;
                        // masterCrewmate.setImage(CREWMATE_IMAGE);
                        break;

                     case A:
                        moveLeft = false;
                        break;
                     case W:
                        moveUP = false;
                        // masterCrewmate.setImage(CREWMATE_IMAGE);
                        break;
                     case D:
                        moveRight = false;
                        // masterCrewmate.setImage(CREWMATE_IMAGE);
                        break;
                     case S:
                        moveDown = false;
                        // masterCrewmate.setImage(CREWMATE_IMAGE);
                        break;
                  }

               }
            });

      backgroundCollision = new Image(BACKGROUND_IMAGE);

      timer = new AnimationTimer() {
         @Override
         public void handle(long now) {

            for (int i = 0; i < robotCrewmates.size(); i++) {
               robotCrewmates.get(i).update();
            }

            movableBackground.update();
         }
      };
      timer.start();
   }

   /**
    * 
    * Plays a sound file repeatedly.
    * 
    * @param fileName the name of the sound file to play
    */
   private void doSound(String fileName) {

      String path = getClass().getResource(fileName).getPath();
      Media media = new Media(new File("src/music.mp3").toURI().toString());
      mediaPlayer = new MediaPlayer(media);
      mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
      volumeSlider.setDisable(false);
      mediaPlayer.play();

   }

   /**
    * 
    * Stops the currently playing sound and disables the volume slider.
    * If no sound is playing, does nothing.
    * 
    * @param fileName the name of the sound file to be played
    */
   private void doSoundOff(String fileName) {
      if (mediaPlayer != null) {
         mediaPlayer.stop();
         volumeSlider.setDisable(true);
         mediaPlayer = null;
      }

   }

   // inner class
   /**
    * 
    * The CrewmateRacer class extends Pane and represents a racer object in the
    * game.
    */
   class CrewmateRacer extends Pane {
      private int racerPosX = 500;
      private int racerPosY = 300;
      private ImageView aPicView = null;

      private boolean isMaster = true;
      private double SceneX = sceneXx / 3000.0;
      private double SceneY = sceneYy / 1687.0;

      private Label nameLabel = null;

      /**
       * 
       * Constructor for a CrewmateRacer object with a given name.
       * 
       * @param name the name of the racer
       */
      public CrewmateRacer(String name) {
         this.isMaster = isMaster;

         aPicView = new ImageView(image);
         aPicView.setTranslateX(racerPosX);
         aPicView.setTranslateY(racerPosY);
         this.getChildren().add(aPicView);

         nameLabel = new Label(name);
         nameLabel.setTextFill(Color.BLACK);
         nameLabel.setFont(new Font("Arial", 14));
         nameLabel.setTranslateX(racerPosX);
         nameLabel.setTranslateY(racerPosY - 35);
         this.getChildren().add(nameLabel);
      }

      /**
       * 
       * Updates the racer's position based on its speed.
       */
      public void update() {

         double speed = 2.2;

      }

      /**
       * 
       * Sets the image of the racer to the given image file.
       * 
       * @param imageFileName the file path of the image
       */
      public void setImage(String imageFileName) {
         aPicView.setImage(new Image(imageFileName));
      }

      /**
       * 
       * Returns the current X position of the racer.
       * 
       * @return the current X position of the racer
       */
      public int getRacerPosX() {
         return racerPosX;
      }

      /**
       * 
       * Returns the current Y position of the racer.
       * 
       * @return the current Y position of the racer
       */
      public int getRacerPosY() {
         return racerPosY;
      }

      /**
       * 
       * Sets the X position of the racer to the given value.
       * 
       * @param value the new X position of the racer
       */
      public void setRacerPosX(int value) {
         if (value != OldX)
            System.out.println("Set pos X: " + value);
         racerPosX = value;
      }

      /**
       * 
       * Sets the Y position of the racer to the given value.
       * 
       * @param value the new Y position of the racer
       */
      public void setRacerPosY(int value) {
         if (value != OldY)
            System.out.println("Set pos Y: " + value);
         racerPosY = value;
      }

   }

   // background
   /**
    * 
    * The MovableBackground class extends Pane to create a movable background for
    * the CrewmateRacer game.
    */
   class MovableBackground extends Pane {

      private ImageView aPicView = null;
      private Image backgroundImage = null;
      private CrewmateRacer crewmateRacer;
      private double sceneX = sceneXx / 3000.0;
      private double sceneY = sceneYy / 1687.0;
      private boolean gameRunning = true;
      Task1 task1 = new Task1();
      Task2 task2 = new Task2();
      Task3 task3 = new Task3();

      private int BackgroundPosX = 0;
      private int BackgroundPosY = 0;

      /**
       * 
       * Creates a new MovableBackground object.
       * 
       * @param cr The CrewmateRacer object to which this background belongs.
       */
      public MovableBackground(CrewmateRacer cr) {
         this.crewmateRacer = cr;
         aPicView = new ImageView(BACKGROUND_IMAGE);
         aPicView.setTranslateX(BackgroundPosX);

         aPicView.setTranslateY(BackgroundPosY);
         this.getChildren().add(aPicView);

      }

      /**
       * 
       * Updates the state of the game and moves the background accordingly.
       */
      public void update() {
         double speed = 3;
         PixelReader pixelReader = backgroundCollision.getPixelReader();
         Color color = pixelReader.getColor((int) crewmateRacer.getRacerPosX(), (int) crewmateRacer.getRacerPosY());

         if ((int) crewmateRacer.getRacerPosX() >= 165 && (int) crewmateRacer.getRacerPosX() <= 250
               && (int) crewmateRacer.getRacerPosY() >= 471 && (int) crewmateRacer.getRacerPosY() <= 555) {
            if (!task1.complete) {
               startTask1();
               moveLeft = false;
               moveRight = false;
               moveUP = false;
               moveDown = false;
            }

         }
         if (color.equals(Color.RED)) {

            if (!task2.complete) {
               startTask2();
               moveLeft = false;
               moveRight = false;
               moveUP = false;
               moveDown = false;
            }

         }
         if ((int) crewmateRacer.getRacerPosX() >= 2230 && (int) crewmateRacer.getRacerPosX() <= 2410
               && (int) crewmateRacer.getRacerPosY() >= 1450 && (int) crewmateRacer.getRacerPosY() <= 1525) {

            if (!task3.complete) {
               startTask3();
               moveLeft = false;
               moveRight = false;
               moveUP = false;
               moveDown = false;
            }
         }

         if (task1.complete && task2.complete && task3.complete) {
            timer.stop();
            endGame();
         }

         if (task1.complete) {
            status = "Completed";
            text.setText(
                  "Task 1: Math " + status + "\nTask 2: Squares " + status2 + "\nTask 3: Swipe the card " + status3);
         }
         if (task2.complete) {
            status2 = "Completed";
            text.setText(
                  "Task 1: Math " + status + "\nTask 2: Squares " + status2 + "\nTask 3: Swipe the card " + status3);
         }
         if (task3.complete) {
            status3 = "Completed";
            text.setText(
                  "Task 1: Math " + status + "\nTask 2: Squares " + status2 + "\nTask 3: Swipe the card " + status3);
         }
         ArrayList<Color> colorList = new ArrayList<Color>();
         boolean allowedMove = false;

         if (moveDown) {
            for (int i = 0; i < (int) (speed + 1); i++) {
               colorList.add(pixelReader.getColor((int) crewmateRacer.getRacerPosX(),
                     (int) crewmateRacer.getRacerPosY() + i + 1));
            }

            allowedMove = colorList.contains(Color.BLACK) ? false : true;
         } else if (moveUP) {
            for (int i = 0; i < (int) (speed + 1); i++) {
               colorList.add(pixelReader.getColor((int) crewmateRacer.getRacerPosX(),
                     (int) crewmateRacer.getRacerPosY() - i - 1));
            }

            allowedMove = colorList.contains(Color.BLACK) ? false : true;
         }

         else if (moveLeft) {
            for (int i = 0; i < (int) (speed + 1); i++) {
               colorList.add(pixelReader.getColor((int) crewmateRacer.getRacerPosX() - i - 1,
                     (int) crewmateRacer.getRacerPosY()));
            }

            allowedMove = colorList.contains(Color.BLACK) ? false : true;
         } else if (moveRight) {
            for (int i = 0; i < (int) (speed + 1); i++) {
               colorList.add(pixelReader.getColor((int) crewmateRacer.getRacerPosX() + i + 1,
                     (int) crewmateRacer.getRacerPosY()));
            }

            allowedMove = colorList.contains(Color.BLACK) ? false : true;
         }
         // System.out.println(color);

         if (color.equals(Color.BLACK) && !allowedMove) {
            System.out.println(color);
            speed = 0.1;
         }

         if (moveDown && allowedMove) {
            BackgroundPosY -= speed;
            crewmateRacer.setRacerPosY((int) (crewmateRacer.getRacerPosY() + speed));
            lastMove = "moveDown";
         }
         if (moveUP && allowedMove) {
            BackgroundPosY += speed;

            crewmateRacer.setRacerPosY((int) (crewmateRacer.getRacerPosY() - speed));
            lastMove = "moveUp";

         }
         if (moveLeft && allowedMove) {
            BackgroundPosX += speed;

            crewmateRacer.setRacerPosX((int) (crewmateRacer.getRacerPosX() - speed));
            lastMove = "moveLeft";
         }
         if (moveRight && allowedMove) {
            BackgroundPosX -= speed;
            crewmateRacer.setRacerPosX((int) (crewmateRacer.getRacerPosX() + speed));
            lastMove = "moveRight";
         }

         aPicView.setTranslateX(BackgroundPosX);
         aPicView.setTranslateY(BackgroundPosY);

      }

      public boolean hasStarted = false;
      public boolean hasStarted2 = false;
      public boolean hasStarted3 = false;

      /**
       * Starts Task1 if it has not been started already.
       * 
       * @throws Exception if an exception occurs while starting Task1
       */
      private void startTask1() {
         if (!hasStarted) {

            try {
               task1.start(new Stage());
            } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
            hasStarted = true;
         }

      }

      /**
       * Starts Task2 if it has not been started already.
       * 
       * @throws Exception if an exception occurs while starting Task2
       */
      private void startTask2() {

         if (!hasStarted2) {

            try {
               task2.start(new Stage());
            } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
            hasStarted2 = true;
         }

      }

      /**
       * Starts Task3 if it has not been started already.
       * 
       * @throws Exception if an exception occurs while starting Task3
       */
      private void startTask3() {

         if (!hasStarted3) {
            try {
               task3.start(new Stage());
            } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
            hasStarted3 = true;
         }

      }

   }

   /**
    * 
    * Ends the game and displays a congratulatory message.
    * Runs the message in the JavaFX Application Thread using Platform.runLater().
    * Displays an Alert dialog box with the header text "Congratulations you won
    * the game!"
    * and the content text "You are victorious!".
    * Then restarts the game by calling the start() method with the previously
    * stored stage object.
    */
   private void endGame() {
      Platform.runLater(() -> {
         Alert alert = new Alert(AlertType.CONFIRMATION, "Congratulations");
         alert.setHeaderText("Congratulations you won the game!");
         alert.setContentText("You are victorious!");

         alert.showAndWait();
         this.start(stage);

      });

   }

   /**
    * 
    * Returns the height of the player's image.
    * 
    * @return a double representing the height of the player's image
    */
   public double getPlayerHeight() {
      return this.aPicView.getImage().getHeight();
   }

   /**
    * 
    * Returns the width of the player's image.
    * 
    * @return a double representing the width of the player's image
    */
   public double getPlayerWidth() {
      return this.aPicView.getImage().getWidth();
   }

}
// end class Races