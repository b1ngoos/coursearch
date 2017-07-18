package com.b1ngoos.student.controllers;

import com.b1ngoos.student.start.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class MainController {

    @FXML
    private ListView<String> listChapters;

    @FXML
    private TextField txtCourse;

    @FXML
    private TextField txtChapter;

    @FXML
    private TextField txtDuration;

    @FXML
    private TextField txtPrice;

    @FXML
    private CheckBox checkBeginner;

    @FXML
    private CheckBox checkMedium;

    @FXML
    private CheckBox checkAdvanced;

    ObservableList<String> chapters =FXCollections.observableArrayList ("java servlet", "corba", "XML");
    private Stage mainStage;
    private Parent fxmlResult;
    private FXMLLoader fxmlLoader = new FXMLLoader();
    private ResultController resultController;
    private Stage resultStage;

    public MainController() {

    }

    @FXML
    private void initialize(){
        listChapters.setItems(chapters);
        initLoad();
    }

    private void initLoad() {
        try {

            fxmlLoader.setLocation(getClass().getResource("../view/result.fxml"));
            fxmlResult = fxmlLoader.load();
            resultController = fxmlLoader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionButtonPressed(ActionEvent actionEvent) {

        Object source = actionEvent.getSource();

        if (!(source instanceof Button)) {
            return;
        }

        Button clickedButton = (Button) source;
        switch (clickedButton.getId()) {
            case "btnAddChapter": {
                chapters.add(txtChapter.getText());
                break;
            }
            case "btnDeleteChapter": {
                final int selectedIdx = listChapters.getSelectionModel().getSelectedIndex();
                if (selectedIdx != -1) {
                    String itemToRemove = listChapters.getSelectionModel().getSelectedItem();

                    final int newSelectedIdx =
                            (selectedIdx == listChapters.getItems().size() - 1)
                                    ? selectedIdx - 1
                                    : selectedIdx;

                    listChapters.getItems().remove(selectedIdx);
                    listChapters.getSelectionModel().select(newSelectedIdx);
                }
                break;
            }
            case "btnPickUp": {

                String[] chapters = new String[this.chapters.size()];
                for(int i = 0; i < chapters.length; i++) {
                    chapters[i] = this.chapters.get(i);
                }

                boolean[] compl = new boolean[3];
                if(checkBeginner.isSelected()) {
                    compl[0] = true;
                }
                if(checkMedium.isSelected()) {
                    compl[1] = true;
                }
                if(checkAdvanced.isSelected()) {
                    compl[2] = true;
                }


                if(Main.agent.startBehaviour(txtCourse.getText(), compl, Integer.parseInt(txtDuration.getText()), Double.parseDouble(txtPrice.getText()), chapters)){
                    resultController.setSource("");
                    resultController.setSource(Main.agent.recomendedCourses);
                    showResultDialog();
                }
                break;
            }
            case "btnView": {
                  if(Main.agent.startBehaviour()){
                      resultController.setSource("");
                      resultController.setSource(Main.agent.getResult());
                      showResultDialog();
                  }
                break;
            }
        }
    }

    private void showResultDialog() {
        if (resultStage ==null) {
            resultStage = new Stage();
            resultStage.setTitle("Result");
            resultStage.setResizable(false);
            resultStage.setScene(new Scene(fxmlResult));
            resultStage.initModality(Modality.WINDOW_MODAL);
            resultStage.initOwner(mainStage);
        }

        resultStage.showAndWait();
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;

        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

            }
        });
    }
}
