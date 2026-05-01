package com.vis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/vis/fxml/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 500, 400);
        scene.getStylesheets().add(
                getClass().getResource("/com/vis/css/styles.css").toExternalForm());
        primaryStage.setTitle("Vehicle Identification System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
