import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import javafx.application.Application;
import javafx.css.CssMetaData;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * 
 * A JavaFX server application for multiplayer games. It handles multiple client
 * connections
 * and displays their activities in the server UI.
 * 
 * @author Janko Leskovac and Kristijan Nincevic
 * @version 1.0
 * @since 2023-04-20
 */

public class Server extends Application implements EventHandler<ActionEvent> {

    private ServerSocket sSocket = null;
    public static final int SERVER_PORT = 12345;
    int clientCount = 0;
    private ArrayList<ClientThread> listOfClients = new ArrayList<>();

    ServerThread sThread = null;

    private Button btnStart = null;
    private Stage stage;

    private FlowPane topPane;
    private FlowPane midPane;

    private Label lblServerIP = null;
    private TextField tfServerIP = null;

    private TextArea tArea;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    /**
     * Launches the JavaFX server application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the server and displays the server UI.
     */
    public void doStart() {
        System.out.println("Server started...");
        sThread = new ServerThread();
        sThread.start();
        btnStart.setText("Stop");
    }

    /**
     * Stops the server and updates the server UI.
     */
    public void doStop() {
        btnStart.setText("Start");
        System.out.println("Server stopping...");
        try {
            sSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Handles button events on the server UI.
     *
     * @param event button event
     */
    @Override
    public void handle(ActionEvent event) {
        Button btn = (Button) event.getSource();
        switch (btn.getText()) {
            case "Start":
                doStart();
                break;
            case "Stop":
                doStop();
                break;
        }
    }

    /**
     * Displays the server UI.
     *
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("Server for Multiplayer");

        VBox root = new VBox(10);
        btnStart = new Button("Start");
        btnStart.setOnAction(this);

        topPane = new FlowPane(10, 10);
        topPane.getChildren().addAll(btnStart);
        topPane.setAlignment(Pos.TOP_RIGHT);
        midPane = new FlowPane(10, 10);

        lblServerIP = new Label("Server IP: ");
        tfServerIP = new TextField();
        tfServerIP.setDisable(true);
        midPane.getChildren().addAll(lblServerIP, tfServerIP);

        tArea = new TextArea();
        tArea.setEditable(false);

        Scene scene = new Scene(root, 400, 400);
        root.getChildren().addAll(topPane, midPane, tArea);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * 
     * A class that extends Thread and represents a server thread. It listens on a
     * specified port for incoming client connections
     * 
     * and creates a new thread for each connected client.
     */
    class ServerThread extends Thread {

        /**
         * 
         * The main run method of the thread. It listens for incoming client connections
         * and creates a new thread for each connected client.
         */
        public void run() {
            try {
                sSocket = new ServerSocket(SERVER_PORT);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            while (true) {
                Socket cSocket = null;

                try {
                    System.out.println("Waiting for client to connect...");
                    cSocket = sSocket.accept();

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ClientThread ct = new ClientThread(cSocket, "Client " + clientCount);
                clientCount++;
                listOfClients.add(ct);
                ct.start();

            }
        }
    }

    /**
     * 
     * A class that represents a client thread.
     */
    class ClientThread extends Thread {
        private Socket cSocket = null;
        private String cName = "";
        private long connectionTime;

        /**
         * 
         * Creates a new instance of the ClientThread class.
         * 
         * @param _cSocket The socket connection to the client.
         * @param name     The name of the client.
         */
        public ClientThread(Socket _cSocket, String name) {
            this.cSocket = _cSocket;
            this.cName = name;
            this.connectionTime = System.currentTimeMillis();
        }

        /**
         * 
         * The method that runs when the thread is started.
         */
        public void run() {
            String clientIP = cSocket.getInetAddress().getHostAddress();
            System.out.println(this.cName + " connected...");
            tfServerIP.setText(clientIP);

            long connectTime = (System.currentTimeMillis() - connectionTime) / 1000;

            try {
                oos = new ObjectOutputStream(cSocket.getOutputStream());
                ois = new ObjectInputStream(cSocket.getInputStream());

                while (true) {
                    Object obj = ois.readObject();

                    if (obj instanceof ClientName) {
                        ClientName cName = (ClientName) obj;
                        String message = "[ " + cName.getName() + " connected from " + clientIP + " at " + new Date()
                                + " ]"
                                + "\n";

                        tArea.appendText(message);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                System.out.println(this.cName + " disconnected from " + clientIP + " after " + connectTime);
            }
        }
    }
}
