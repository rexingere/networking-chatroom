/* Course: COP 3809C
 * Author: Elisa Rexinger
 * Purpose: Create NameStage class
 * This is a stage that lets the user choose their username*/

package chatroom.supplements;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

/* NameStage class shows the user a stage that prompts them to enter their username*/
public class NameStage {
    static Stage stage = new Stage();
    static TextField userNameField = new TextField();

    /* method to retrieve the clients user name by entering the name into the NameStage */
    public static String getName() {
        createNameStage(stage);
        return userNameField.getText().trim();
    }

    /* method to create the stage for the name stage */
    private static Scene createNameStage(Stage stage) {
        // create and style the scene
        Scene nameScene = new Scene(createVBox(), 320, 320);
        nameScene.getStylesheets().add("chatroom/supplements/styling/chat.css");

        // define settings for the stage
        stage.initModality(Modality.APPLICATION_MODAL);    // blocks user interaction with other windows
        stage.setTitle("Username");
        stage.setMinWidth(250);

        // set scene of the stage to be the name scene
        stage.setScene(nameScene);
        stage.showAndWait();
        stage.setOnCloseRequest(e-> {
            stage.close();
        });

        return nameScene;
    }

    /* method to create the vBox that holds the elements for the name stage */
    private static VBox createVBox() {
        VBox vBox = new VBox();

        // create elements
        ImageView imageViewHello = createHelloImage();
        Label lblEnterName = createEnterLabel();
        TextField tfUserName = createUserNameTextField();
        Button btnEnter = createEnterButton();

        // add elements
        vBox.getChildren().addAll(imageViewHello, lblEnterName,tfUserName,btnEnter);

        // style the vBox
        vBox.getStyleClass().add("dotted-border");
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(8,8,8,8));
        vBox.setSpacing(9);
        vBox.setStyle("-fx-background-color: white");

        return vBox;
    }

    /* method to make the image view of a "hello" gif */
    private static ImageView createHelloImage() {
        Image helloPic = new Image("chatroom/supplements/styling/yelloHello.gif");
        ImageView imageView = new ImageView(helloPic);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(160);

        return imageView;
    }

    /* method to make the label that prompts the user to enter their name */
    private static Label createEnterLabel() {
        Label enterNameLbl = new Label("Enter your username:");
        enterNameLbl.getStyleClass().add("labels");

        return enterNameLbl;
    }

    /* method to make the text field where the user enters their name */
    private static TextField createUserNameTextField() {
        userNameField.setAlignment(Pos.CENTER);
        userNameField.getStyleClass().addAll("username-text", "text-field");
        userNameField.setPromptText("Enter Username:");

        userNameField.setOnAction(e -> {
            stage.close();
        });

        return userNameField;
    }

    /* method to make the button that the user pushes to enter their name */
    private static Button createEnterButton() {
        Button enterBtn = new Button("Enter");
        enterBtn.getStyleClass().add("message-button");
        enterBtn.setOnAction(e -> {
            stage.close();
        });

        return enterBtn;
    }
}
