/* Course: COP 3809C
 * Author: Elisa Rexinger
 * Purpose: Create ClientChat class
 * Defines how the user will interact with the program as a client
 * Specifies how UTF messages from the server are displayed to the client in a JavaFX program. */

package chatroom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import chatroom.supplements.NameStage;
import chatroom.supplements.avatar.AvatarStage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/* ClientChat class Specifies how UTF messages from the server are displayed to the client in a JavaFX program.*/
public class ClientChat extends Application {
	  // IO streams
	  private static String username = null;
	  private static String avatar = null;
	  static DataOutputStream toServer = null;
	  static DataInputStream fromServer = null;

	  static int port = 8000;               // declares which port the client will connect to
	  static String host = "localhost";     // declare the host
      static TextArea ta = new TextArea();	// area where messages from other clients are presented
      static Socket socket;						// socket to connect to server

	@Override /* Override the start method in the Application class */
	public void start(Stage primaryStage) {
		try {
			// Create a socket to connect to the server
			socket = new Socket(host, port);

			// Create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());

			// Create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());

			// Create a thread to "listen" to messages coming from the server as long as the client is active
			new ReceiveMessagesThread().start();
		}
		catch (IOException ex) {
			ta.appendText(ex.toString() + '\n');
		}

		createLayout(primaryStage);
	}

	/* ReceiveMessagesThread is an inner class that allows
	the clients to accept and read messages from the server */
	class ReceiveMessagesThread extends Thread {
		public void run() {
			while(true) {
				try {
					// read the message from the input datastream
					String message = fromServer.readUTF();
					// print the message
					ta.appendText(message);
				}
				catch(IOException e) {
					break;
				}
			}
		}
	}

	/* method to let the user choose their username. */
	private void retrieveUsername() {
		username = NameStage.getName();			// retrieve username from the client
		try {
			toServer.writeUTF(username);		// write the username to the server
			toServer.flush();					// flush the output stream
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* method to let the user select what avatar they want to use */
	private ImageView retrieveAvatar(){
		avatar = AvatarStage.getAvatar();		// get the location for the picture of the desired avatar
		if(avatar == null) {
			closeClientStage();
		}
		Image avatarImage = new Image(avatar);		    // set this image to an image view
		ImageView avatarImageView = new ImageView(avatarImage);
		avatarImageView.setPreserveRatio(true);
		avatarImageView.setFitHeight(30);

		return avatarImageView;						    // return the imageview to be used as the avatar picture
	}

	/* method to create the mainPane of user's chat scene */
	private Pane createLayout(Stage primaryStage) {
		BorderPane mainPane = new BorderPane();

		// retrieve username from the client
		retrieveUsername();

		// set regions of the mainPane
		mainPane.setTop(createHBox());
		mainPane.setCenter(createScrollPane());
		mainPane.setBottom(createGridPane());

		// style mainPane
		mainPane.setStyle("-fx-background-color: white");
		mainPane.setPadding(new Insets (10,10,10,10));
		mainPane.getStyleClass().add("dotted-border");

		// create and style the scene
		Scene scene = new Scene(mainPane, 400, 400);
		scene.getStylesheets().add("chatroom/supplements/styling/chat.css");

		// and Action!
		primaryStage.setScene(scene);
		primaryStage.setTitle("ClientChat");
		primaryStage.show();

		// handle what happens when the user exits
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				closeClientStage();
				primaryStage.close();
			}
		});

		return mainPane;
	}

	/* method to create an HBox that goes at the top of the user's chat scene */
	private Pane createHBox() {
		HBox hBox = new HBox();

		// create elements
		ImageView avatarImageView = retrieveAvatar();	// retrieve the avatar the user wants to use

		Label nameLbl = new Label(username+ "'s Chat Room");
		nameLbl.getStyleClass().add("labels");

		// add elements to the hBox
		hBox.getChildren().addAll(avatarImageView, nameLbl);

		// style the hBox
		hBox.setAlignment(Pos.CENTER);
		hBox.setSpacing(15);
		hBox.setPadding(new Insets(6,6,6,6));

		return hBox;
	}

	/* method to create a ScrollPane that goes in the center of the user's chat scene */
	private ScrollPane createScrollPane() {
		ScrollPane scrollPane = new ScrollPane(ta);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);

		return scrollPane;
	}

	/* method to create a GridPane that goes at the bottom of the user's chat scene */
	private Pane createGridPane() {
		GridPane gridPane = new GridPane();

		// create elements for the gridPane
		// create TextField and its action
		TextField tf = new TextField();
		tf.setPromptText("Enter Message");
		tf.setAlignment(Pos.BOTTOM_LEFT);
		tf.setPrefWidth(270);
		tf.setOnAction(e -> {
			try {
				// Get the radius from the text field
				toServer.writeUTF(tf.getText().trim());
				toServer.flush();
				tf.setText("");
			}
			catch (IOException ex) {
				System.err.println(ex);
			}
		});

		// create an enter Button and its action
		Button btnEnter = new Button("Send");
		btnEnter.getStyleClass().add("message-button");
		btnEnter.setOnAction(e -> {
			try {
				// Get the message from the text field
				toServer.writeUTF(tf.getText().trim());
				toServer.flush();
				tf.setText("");
			}
			catch (IOException ex) {
				System.err.println(ex);
			}
		});

		// set regions of the gridPane
		gridPane.add(tf, 0,0);
		gridPane.add(btnEnter, 1,0);

		// set style of the gridPane
		gridPane.setPadding(new Insets(7, 7,7, 7));
		gridPane.setHgap(7);
		gridPane.setStyle("-fx-border-color: #ADD8E6");

		return gridPane;
	}

	/* method to close the client stage by closing the toServer and from server streams,
	   the socket, the platform, and the system*/
	public static void closeClientStage() {
		try {
			toServer.close();
			fromServer.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Platform.exit();
		System.exit(0);
	}

}
// END OF CLIENTCHAT
