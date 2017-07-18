package com.b1ngoos.student.start;

import com.b1ngoos.student.agent.StudentAgent;
import com.b1ngoos.student.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;

public class Main extends Application{

    public static final CountDownLatch latch = new CountDownLatch(1);
    public static Main startUpTest = null;
    public static StudentAgent agent = null;

    public static Main waitForStartUpTest() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return startUpTest;
    }

    public static void setStartUpTest(Main startUpTest0) {
        startUpTest = startUpTest0;
        latch.countDown();
    }

    public Main() {
        setStartUpTest(this);
    }

    public void setAgent(StudentAgent agent) {
        Main.agent = agent;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("../view/main.fxml"));
        Parent fxmlMain = fxmlLoader.load();
        MainController mainController = fxmlLoader.getController();
        mainController.setMainStage(primaryStage);

        primaryStage.setTitle("Student");
        primaryStage.setMinHeight(444);
        primaryStage.setMinWidth(421);
        primaryStage.setScene(new Scene(fxmlMain, 421, 444));
        primaryStage.getIcons().add(new Image("icon.png"));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
