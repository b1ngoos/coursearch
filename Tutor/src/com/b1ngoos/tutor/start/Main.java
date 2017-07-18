package com.b1ngoos.tutor.start;

import com.b1ngoos.tutor.controllers.MainController;
import com.b1ngoos.tutor.models.CollectionChapter;
import com.b1ngoos.tutor.models.CollectionCourse;
import com.b1ngoos.tutor.utils.MLogger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.log4j.xml.DOMConfigurator;

public class Main extends Application {

    public static String fileName;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //MLogger.initLogger();

        //DOMConfigurator.configure("/resources/log4j.xml");
        MLogger.info("Init application");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("../view/main.fxml"));
        Parent fxmlMain = fxmlLoader.load();
        MainController mainController = fxmlLoader.getController();
        mainController.setMainStage(primaryStage);
        primaryStage.setTitle("TUTOR");
        primaryStage.setMinHeight(337);
        primaryStage.setMinWidth(568);
        primaryStage.setScene(new Scene(fxmlMain, 568, 337));
        primaryStage.getIcons().add(new Image("/resources/images/icon.png"));
        primaryStage.show();
        mainController.txtTutorName.setText(fileName);
        MLogger.info("Start application");
    }


    public static void main(String[] args) {
        if(args.length > 0) {
            Main.fileName = args[0].trim();
            CollectionCourse.FILE_NAME = args[0].trim() + "_course.xml";
            CollectionChapter.FILE_NAME = args[0].trim() + "_chapter.xml";
        }
        launch(args);
    }
}
