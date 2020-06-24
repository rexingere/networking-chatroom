/* Course: COP 3809C
 * Author: Elisa Rexinger
 * Purpose: Create AvatarStage class
 * This is a stage that presents the avatar options to the clients and allows them to select the
 * avatar they want to be displayed to the left of their username*/

package chatroom.supplements.avatar;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/* AvatarStage class creates a stage that prompts the user to select their avatar*/
public class AvatarStage {
    static Stage stage = new Stage();
    static String avatar = null;

    /* method to return the avatar selected */
    public static String getAvatar() {
        // create the scene for the avatar stage
        Scene avatarScene = new Scene(createBorderPane());
        stage.setScene(avatarScene);

        // style the scene/stage
        avatarScene.getStylesheets().add("chatroom/supplements/styling/chat.css");
        stage.setMinWidth(300);

        // set settings for stage
        stage.setTitle("Choose Avatar");
        stage.initModality(Modality.APPLICATION_MODAL);    // blocks user interaction with other windows
        stage.showAndWait();

        // handle what happens when user exits
        stage.setOnCloseRequest(e-> {
            stage.close();
        });

        return avatar;
    }

    /* method that creates main pane for the avatar scene */
    private static BorderPane createBorderPane() {
        BorderPane borderPane = new BorderPane();

        // create "Choose Avatar" label
        Label chooseAvatarLbl = new Label("Pick your Avatar!\nClick to Choose");
        chooseAvatarLbl.getStyleClass().add("labels");
        BorderPane.setAlignment(chooseAvatarLbl, Pos.CENTER);
        chooseAvatarLbl.setAlignment(Pos.CENTER);

        // set regions of borderPane
        borderPane.setTop(chooseAvatarLbl);
        borderPane.setCenter(createGridPane());

        // style borderPane
        borderPane.setPadding(new Insets(8,8,8,8));
        borderPane.getStyleClass().add("dotted-border");
        borderPane.setStyle("-fx-background-color: white");

        return borderPane;
    }

    /* method to create the grid pane (bottom of the main border pane) */
    private static GridPane createGridPane() {
        GridPane gridPane = new GridPane();

        // add elements to gridPane
        gridPane.add(createFoxAvatar(), 0, 1);
        gridPane.add(createPandaAvatar(), 1, 1);
        gridPane.add(createMonkeyAvatar(), 2, 1);
        gridPane.add(createCowAvatar(), 3, 1);

        // style gridPane
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(30);

        return gridPane;
    }

    /* method to create and return the fox avatar */
    private static ImageView createFoxAvatar() {
        String fox = "chatroom/supplements/avatar/fox.png";
        Image foxImage = new Image(fox);
        ImageView foxAvatar = new ImageView(foxImage);
        foxAvatar.setPreserveRatio(true);
        foxAvatar.setFitHeight(80);
        foxAvatar.setOnMouseClicked(e->{
            avatar = fox;
            stage.close();

        });

        return foxAvatar;
    }

    /* method to create and return the panda avatar */
    private static ImageView createPandaAvatar() {
        String panda = "chatroom/supplements/avatar/panda.png";
        Image pandaImage = new Image(panda);
        ImageView pandaAvatar = new ImageView(pandaImage);
        pandaAvatar.setPreserveRatio(true);
        pandaAvatar.setFitHeight(80);
        pandaAvatar.setOnMouseClicked(e->{
            avatar = panda;
            stage.close();

        });

        return pandaAvatar;
    }

    /* method to create and return the monkey avatar */
    private static ImageView createMonkeyAvatar() {
        String monkey = "chatroom/supplements/avatar/monkey.png";
        Image monkeyImage = new Image(monkey);
        ImageView monkeyAvatar = new ImageView(monkeyImage);
        monkeyAvatar.setPreserveRatio(true);
        monkeyAvatar.setFitHeight(80);
        monkeyAvatar.setOnMouseClicked(e->{
            avatar = monkey;
            stage.close();
        });

        return monkeyAvatar;
    }

    /* method to create and return the cow avatar */
    private static ImageView createCowAvatar() {
        String cow = "chatroom/supplements/avatar/cow.png";
        Image cowImage = new Image(cow);
        ImageView cowAvatar = new ImageView(cowImage);
        cowAvatar.setPreserveRatio(true);
        cowAvatar.setFitHeight(80);
        cowAvatar.setOnMouseClicked(e->{
            avatar = cow;
            stage.close();

        });

        return cowAvatar;
    }
}
