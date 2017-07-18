package com.b1ngoos.tutor.controllers;

import com.b1ngoos.tutor.models.Chapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditChapterController implements Initializable {

    @FXML
    private TextField txtTitle;

    @FXML
    private TextField txtDuration;

    @FXML
    private TextField txtPrice;

    @FXML
    private ComboBox<String> cbComplexity;

    private Chapter chapter;

    public void actionClose(ActionEvent actionEvent){
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }

    public void setChapter(Chapter chapter) {

        if(chapter == null){
            return;
        }

        this.chapter = chapter;

        txtTitle.setText(chapter.getTitle());
        txtPrice.setText(chapter.getPrice());
        txtDuration.setText(chapter.getDuration());
        cbComplexity.setValue(chapter.getComplexity());
    }

    public Chapter getChapter(){
        return chapter;
    }

    public void actionSave(ActionEvent actionEvent) {
        chapter.setTitle(txtTitle.getText());
        chapter.setComplexity(cbComplexity.getSelectionModel().getSelectedItem());
        chapter.setPrice(txtPrice.getText());
        chapter.setDuration(txtDuration.getText());
        actionClose(actionEvent);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbComplexity.setItems(FXCollections.observableArrayList(
                "Beginner", "Medium", "Advanced"));
    }
}
