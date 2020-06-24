/* Course: COP 3809C
 * Author: Elisa Rexinger
 * Purpose: Create ErrorPane class (extends VBox
 * This is a pane that is displayed to the server operator when they try to close the server
 * when clients are still connected. (I didn't want that to be possible). When the user exits
 * this pane, the server stage is left unchanged*/

package chatroom.supplements;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/* ErrorPane class creates the error message displayed to the user when closing a server that still has clients connected*/
public class ErrorPane extends VBox {
    public ErrorPane(String errorMessage){
        Stage errorStage = new Stage();

        // create elements
        // create picture to the background of the error window
        Image redXPic = new Image("chatroom/supplements/styling/redX.png");
        ImageView redImageView = new ImageView(redXPic);
        redImageView.setPreserveRatio(true);
        redImageView.setFitHeight(120);

        // create error label
        Label errorLabel = new Label(errorMessage);
        errorLabel.setAlignment(Pos.CENTER);
        errorLabel.getStyleClass().add("error-label");

        // create enter button to close the stage
        Button btnOk = new Button("Enter");
        btnOk.getStyleClass().add("red-button");
        btnOk.setOnAction(e->{
            errorStage.close();
        });

        // add elements to the error pane
        getChildren().addAll(redImageView, errorLabel,btnOk);

        // format pane
        getStyleClass().add("red-border");
        setPadding(new Insets(15,40,20,40));
        setAlignment(Pos.CENTER);
        setSpacing(20);

        // create, style, and add the error pane to a scene
        Scene errorScene = new Scene(this,350,350);
        errorScene.getStylesheets().add("chatroom/supplements/styling/chat.css");

        // set the stage
        errorStage.setScene(errorScene);
        errorStage.setTitle("Error");
        errorStage.show();
    }
}
