import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.geometry.*;
import javafx.animation.*;
import java.io.*;
import java.util.*;

// import Game2DClean.CrewmateRacer.movableBackground;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

/**
 * AmongUSStarter with JavaFX and Threads
 * Loading imposters
 * Loading background
 * Control actors and backgrounds
 * Create many number of imposters - random controlled
 * RGB based collision
 * Collsion between two imposters
 */

public class Sample extends Application {
   // Window attributes
   private Stage stage;
   private Scene scene;
   private StackPane root;

   private static String[] args;

   private final static String CREWMATE_IMAGE = "amongus.png"; // file with icon for a racer
   private final static String CREWMATE_RUNNERS = "amongusRunners.png"; // file with icon for a racer
   private final static String BACKGROUND_IMAGE = "background.jpg";

   // crewmates
   CrewmateRacer mastCrewmate = null;
   ArrayList<CrewmateRacer> robotCrewmates = new ArrayList<>();

   //movable background
   MovableBackground movableBackground=null;

   // Animation timer
   AnimationTimer timer = null;
   int counter = 0;
   boolean moveUP = false;
   boolean moveDOWN = false;
   boolean moveRIGHT = false;
   boolean moveLEFT = false;

   //background detection/collision
   Image backgroundCollision = null;
   // main program
   public static void main(String[] _args) {
      args = _args;
      launch(args);
   }

   // start() method, called via launch
   public void start(Stage _stage) {
      // stage seteup
      stage = _stage;
      stage.setTitle("Game2D Starter");
      stage.setOnCloseRequest(
            new EventHandler<WindowEvent>() {
               public void handle(WindowEvent evt) {
                  System.exit(0);
               }
            });

      // root pane
      root = new StackPane();

      initializeScene();

   }

   // start the game scene
   public void initializeScene() {

      mastCrewmate = new CrewmateRacer(true);
      for (int i = 0; i < 5; i++) {
         CrewmateRacer cR = new CrewmateRacer(false);
         robotCrewmates.add(cR);
      }
      //create background
      movableBackground = new MovableBackground();


         //add background
         this.root.getChildren().addAll(movableBackground);
      // add to the root
      this.root.getChildren().addAll(mastCrewmate);

      this.root.getChildren().addAll(robotCrewmates);

   

      // display the window
      scene = new Scene(root, 800, 500);
      // scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
      stage.setScene(scene);
      stage.show();

      // KEYBOARD CONTROL
      // -------------------------------------------------------------------------

      scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

         @Override
         public void handle(KeyEvent event) {
            switch (event.getCode()) {
               case UP:
                  moveUP = true;
                  break;
               case DOWN:
                  moveDOWN = true;
                  System.out.println("PRESSED");

                  break;
               case LEFT:
                  moveLEFT = true;
                  break;
               case RIGHT:
                  moveRIGHT = true;
                  break;
            }// end of switch

         }

      });// end of key pressed
      // -------------------------------------------------------------------------
      scene.setOnKeyReleased(new EventHandler<KeyEvent>() {

         @Override
         public void handle(KeyEvent event) {
            switch (event.getCode()) {
               case UP:
                  moveUP = false;
                  break;
               case DOWN:
                  moveDOWN = false;
                  System.out.println("RELEASED");
                  break;
               case LEFT:
                  moveLEFT = false;
                  break;
               case RIGHT:
                  moveRIGHT = false;
                  break;

            }// end of switch

         }

      });
      // -------------------------------------------------------------------------
      //background collision
      backgroundCollision = new Image(BACKGROUND_IMAGE);//MASK IMAGE 


      timer = new AnimationTimer() {

         @Override
         public void handle(long now) {
            // TODO Auto-generated method stub
            // System.out.println(counter++);
            mastCrewmate.update();
            for (int i = 0; i < robotCrewmates.size(); i++) {
               robotCrewmates.get(i).update();
            }

         }

      };
      timer.start();
   }

   // Inner class
   class CrewmateRacer extends Pane {
      private int racerPosX = 0;
      private int racerPosY = 0;
      private ImageView aPicView = null;
      private boolean isMaster = true;

      public CrewmateRacer(boolean isMaster) {
         this.isMaster = isMaster;
         if (isMaster) {
            aPicView = new ImageView(CREWMATE_IMAGE); // instanting a crewmate picture
            // racerPosX = (int)root.getWidth()/2;
            // racerPosY = (int)root.getHeight()/2;

         } else
            aPicView = new ImageView(CREWMATE_RUNNERS);
         this.getChildren().add(aPicView);// adding the picture to pane
      }

      public void update() {

         double speed = 10;

         if (isMaster) {// MASTER CONTROL

            //get pixel



          
            Color color = backgroundCollision.getPixelReader().getColor(racerPosX, racerPosY);//give me pixel of this location
            // System.out.println(color.getRed() + " " + color.getGreen() + " " + color.getBlue());//get colors 

            //get distance
            int targetX =0;
            int targetY = 0;

            double dist= Math.sqrt(Math.pow(racerPosX-targetX,2)
            +Math.pow(racerPosY-targetY, 2));

            System.out.println(dist);

            if(color.getRed()>0.6){
               speed = 10;
            }else{
               speed = 2;
            }
            if (moveDOWN) {
               racerPosY += speed;
            }
            if (moveUP) {
               racerPosY -= speed;
            }
            if (moveLEFT) {
               racerPosX -= speed;
            }
            if (moveRIGHT) {
               racerPosX += speed;
            }

         } else {
            racerPosX += Math.random() * speed;
            racerPosY += (Math.random() - 0.2) * speed;
         }

         aPicView.setTranslateX(racerPosX);
         aPicView.setTranslateY(racerPosY);

         if (racerPosX > root.getWidth())
            racerPosX = 0; // at the edge of root set x to 0 so it goes back
         if (racerPosY > root.getHeight())
            racerPosY = 0; // at the edge of root set y to 0 so it goes back
            if(racerPosX<0) racerPosX=0;
            if(racerPosY<0) racerPosY=0;

      }

      

   }

   // background
   class MovableBackground extends Pane {
      private int racerPosX = 0;
      private int racerPosY = 0;
      private ImageView aPicView = null;

      public MovableBackground() {

         aPicView = new ImageView(BACKGROUND_IMAGE); // instanting a crewmate picture

         this.getChildren().add(aPicView);// adding the picture to pane
      }
      public void update(){
         double speed = 10;

       
            // if (moveDOWN) {
            //    racerPosY -= speed;
            // }
            // if (moveUP) {
            //    racerPosY += speed;
            // }
            // if (moveLEFT) {
            //    racerPosX += speed;
            // }
            // if (moveRIGHT) {
            //    racerPosX -= speed;
            // }
            aPicView.setTranslateX(racerPosX);
            aPicView.setTranslateY(racerPosY);
         }

        
      
   }//end of inner class
} // end class Races