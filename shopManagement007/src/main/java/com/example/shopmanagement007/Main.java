package com.example.shopmanagement007;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("marketPurchase.fxml"));
        //FXML LOAD
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("---Simple POS System---");
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
        primaryStage.setX(100);
        primaryStage.setY(0);
        primaryStage.getIcons().add(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chieneseAxe.jpg")))
        );
        primaryStage.setResizable(false);

    }


    public static void main(String[] args) {
        launch(args);
    }
}