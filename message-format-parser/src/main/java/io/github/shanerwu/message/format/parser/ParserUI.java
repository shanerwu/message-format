package io.github.shanerwu.message.format.parser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ParserUI extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = new FXMLLoader().load(
                Thread.currentThread().getContextClassLoader().getResourceAsStream("fxml/parser.fxml")
        );

        Scene scene = new Scene(root);

        stage.getIcons().add(new Image("/fxml/image/icon.png"));
        stage.setTitle("Message Parser");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
