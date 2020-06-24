/* Course: COP 3809C
 * Author: Elisa Rexinger
 * Purpose: Create ServerChat class
 * Defines how the server operates and interacts with the connected clients*/

package chatroom;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import chatroom.supplements.ErrorPane;
import chatroom.supplements.InvalidServerExitException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/* ServerChat class defines how the server operates and interacts with the connected clients*/
public class ServerChat extends Application {
	ServerSocket serverSocket = null;		// Create a server socket
	private TextArea ta = new TextArea();	// Text area for displaying contents
	private int clientNo = 0;				// Number a client

	// Array list is iterated through when sending messages to all the clients.
	public ArrayList<ClientThread> clientList = new ArrayList<>();

	@Override /* Override the start method in the Application class */
	public void start(Stage primaryStage) {
		// Create and style the scene
		Scene scene = new Scene(createLayout(), 550, 230);
		scene.getStylesheets().add("chatroom/supplements/styling/chat.css");

		// set the stage settings
		primaryStage.setTitle("ServerChat"); // Set the stage title
		primaryStage.setScene(scene); // Place the scene in the stage
		primaryStage.show(); // Display the stage

		// handle what happens when user exits
		primaryStage.setOnCloseRequest(e -> {
			// checking if any clients are still connected to the server
			if(!clientList.isEmpty()) {
				try {
					throw new InvalidServerExitException("DENIED CLOSING SERVER:\n Clients are still using service.");
				} catch (InvalidServerExitException ex) {
					ErrorPane errorPane = new ErrorPane(ex.getMessage());
				}
				e.consume();
			}
			// if no clients are connected, then close
			else {
				CloseSocket();
				Platform.exit();
				System.exit(0);
			}
		});

		// create a thread that connects the server socket to port 8000 and listens for connections from clients
		new Thread(() -> {
			try {
				// Create a server socket
				serverSocket = new ServerSocket(8000);
				ta.appendText("ServerChat started at " + new Date() + '\n');

				// continuously listens for client connection
				while (true) {
					// Listen for a new connection request
					Socket socket = serverSocket.accept();
					DataOutputStream outputToAllClients = new DataOutputStream(socket.getOutputStream());

					// Increment clientNo
					clientNo++;

					// shows all the information to the server stage's text area
					Platform.runLater(() -> {
						// Display the client number
						ta.appendText("Connection initiated for Client " + clientNo + "\n\t\t- Time:  " + new Date() + '\n');

						// Find the client's host name, and IP address
						InetAddress inetAddress = socket.getInetAddress();
						ta.appendText("\t\t- Client " + clientNo + "'s host name: " + inetAddress.getHostName() + "\n");
						ta.appendText("\t\t- Client " + clientNo + "'s IP Address is " + inetAddress.getHostAddress() + "\n");
					});

					// Create and start a new thread for the connection
					ClientThread ct = new ClientThread(socket);
					clientList.add(ct);
				}
			} catch (IOException ex) {
			}
		}).start();
	}

	/* This inner class creates a ClientThread for each client that connects to the server */
	public class ClientThread extends Thread {
		String userName;
		Socket socket;
		DataInputStream inputFromClient;
		DataOutputStream outputToClient;
		String chatMessage;

		/* ClientThread constructor */
		public ClientThread(Socket socket) {
			this.socket = socket;
			this.start();

			// declare inputFromClient and outputToClient as streams from/to the client
			try {
				outputToClient = new DataOutputStream(socket.getOutputStream());
				inputFromClient  = new DataInputStream(socket.getInputStream());
			}
			catch (IOException e) {
				System.out.println("Exception in creating ClientThread output/input streams");
			}
		}

		/* method to specify how the ClientThread will run */
		public void run() {
			// read in the username from the client, document information in server's text area
			try {
				userName = inputFromClient.readUTF();
				ta.appendText("Client " + clientNo + " user name: " + userName + "\n");
			} catch (IOException e) {
				System.out.println("Exception in getting username");
			}

			try {
				// Continuously serve the client
				while (true) {
					 //Receive message from the client
					chatMessage = inputFromClient.readUTF();

					// adds information to the server's text area
					Platform.runLater(() -> {
						ta.appendText("Message from " + userName + " (Client " + clientNo + ")\n");
						ta.appendText("\t\t- " + userName + ": " + chatMessage + '\n');

						// send the message to all the other clients
						sendToAll(userName, chatMessage);
					});
				}
			} catch (IOException ex) {
			} finally {
				// When the ClientThread has stopped running, close it
				closeClientThread();
			}
		}

		/* method to fully close the ClientThread
		*  "close" = removes thread from the client list, closes the output stream, and closes the
		*  socket the thread was connected to. */
		public void closeClientThread() {
			try {
				clientList.remove(this);		// remove ClientThread from clientList
				outputToClient.close();			// close the output stream
				socket.close();					// close the socket

				// append the server and client's text areas with the notification that the client has left
				ta.appendText(userName + " (Client " + clientNo + ") has left the chat\n");
				sendToAll("::", userName + " has left the chat :::\n");

			} catch (IOException e) {
			}
		}

		/* method to write a string through the output stream (to a client) */
		public void writeMessage(String msg) {
			// write the message to the stream
			try {
				outputToClient.writeUTF(msg);
				outputToClient.flush();
			}
			catch(IOException e) {
				System.out.println("Exception in writeMessage()");
			}
		}
	}
	// End of ClientThread

	/* method to send messages to all the clients by iterating through the clientList (ArrayList<ClientThread> */
	private void sendToAll(String userName, String userMessage) {
		String message = userName + ": " + userMessage + "\n";
		for(int i = clientList.size(); --i >= 0;) {
			ClientThread ct = clientList.get(i);	// retrieve client at index i
			ct.writeMessage(message);				// send this client a message
		}
	}

	/* method to close the Client Thread and the server socket */
	private void CloseSocket() {
		if (serverSocket != null) {
			try {
				// iterates through the clientList and calls closeClientThread() for each
				for(int k = clientList.size(); --k >= 0;) {
					ClientThread ct = clientList.get(k);
					ct.closeClientThread();
				}

				// closing the server socket
				serverSocket.close();
			} catch (IOException e) {
				System.out.println("IO Exception in CloseSocket()");
			}
		}
	}

	/* method to create and style the main pane of the server scene */
	private Pane createLayout() {
		BorderPane pane = new BorderPane();

		// set regions of border pane
		pane.setLeft(createLeft());
		pane.setCenter(createCenter());

		// style the border pane
		pane.setStyle("-fx-background-color: white");

		return pane;
	}

	/* method to create and style the left side of the server scene */
	private VBox createLeft() {
		VBox vBox = new VBox();

		// create elements
		Image serverPic = new Image("chatroom/supplements/styling/server.gif");
		ImageView serverImageView = new ImageView(serverPic);
		serverImageView.setPreserveRatio(true);
		serverImageView.setFitHeight(80);

		Label serverLabel = new Label("server");
		serverLabel.getStyleClass().add("server-text");

		// add elements to vBox
		vBox.getChildren().addAll(serverImageView, serverLabel);

		// style the vBox
		vBox.setSpacing(20);
		vBox.setAlignment(Pos.CENTER);

		return vBox;
	}

	/* method to create and style the center portion of the server scene */
	private VBox createCenter() {
		VBox vBox = new VBox();

		// create elements
		// create scroll pane (top of vBox)
		ScrollPane scrollPane = new ScrollPane(ta);

		// create button (sends "hello" to all the clients from the server. Goes below scroll pane
		Button btnHello = new Button("Send Hello");
		btnHello.getStyleClass().add("round-black");
		btnHello.setOnAction(e -> {
			sendToAll("Server", "Hello Everyone!");
			Platform.runLater(() -> {
				ta.appendText("Server sent 'Hello' to clients." + "\n");
			});
		});

		// add elements to vBox
		vBox.getChildren().addAll(scrollPane, btnHello);

		// style the vBox
		vBox.setAlignment(Pos.CENTER);
		vBox.setPadding(new Insets(9, 9, 9, 9));
		vBox.setSpacing(10);

		return vBox;
	}
}
// End of ServerChat class