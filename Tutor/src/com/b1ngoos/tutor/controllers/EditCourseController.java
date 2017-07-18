package com.b1ngoos.tutor.controllers;

import com.b1ngoos.tutor.models.Chapter;
import com.b1ngoos.tutor.models.Course;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EditCourseController {

    @FXML
    private TextField txtTitle;

    private Course course;

    public void actionClose(ActionEvent actionEvent){
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }

    public void setCourse(Course course, int id) {

        if(course == null){
            return;
        }

        this.course = course;
        this.course.setCourseId(id);

        txtTitle.setText(course.getTitle());
    }

    public void setCourse(Course course) {

        this.course = course;

        txtTitle.setText(course.getTitle());
    }

    public Course getCourse(){
        return course;
    }

    public void actionSave(ActionEvent actionEvent) {
        course.setTitle(txtTitle.getText());

        actionClose(actionEvent);
    }
}
