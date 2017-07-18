package com.b1ngoos.student.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.List;

public class ResultController {

    @FXML
    private TextArea txtSource;

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }

    public void setSource(String source) {
        txtSource.setText(source);
    }

    public void setSource(List<List<String>> source) {
        for (List<String> list : source) {
            for (String str : list) {
                txtSource.appendText(str + "\n");
            }
        }
    }
}
