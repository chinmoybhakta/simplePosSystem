package com.example.shopmanagement007;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class changeScene {

    @SuppressWarnings("CallToPrintStackTrace")
    public changeScene(ActionEvent event, String fxmlFile){
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(changeScene.class.getResource(fxmlFile));
            root = loader.load();
        } 
        catch(IOException e){
            e.printStackTrace();
            System.out.println("IO EXCEPTION");
        }

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        //change scene method
    }

}
