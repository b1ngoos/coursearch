package com.b1ngoos.tutor.controllers;

import com.b1ngoos.tutor.models.Chapter;
import com.b1ngoos.tutor.models.CollectionChapter;
import com.b1ngoos.tutor.models.CollectionCourse;
import com.b1ngoos.tutor.models.Course;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class MainController {

    @FXML
    private TableView tableTasks;

    @FXML
    private TableColumn<Chapter, String> columnTitle;

    @FXML
    private TableColumn<Chapter, String> columnDuration;

    @FXML
    private TableColumn<Chapter, String> columnPrice;

    @FXML
    private TableColumn<Chapter, String> columnComplexity;

    @FXML
    private TableView tableCourses;

    @FXML
    private TableColumn<Course, String> columnCourse;

    @FXML
    public TextField txtTutorName;

    private Parent fxmlEditChapter;
    private Parent fxmlEditCourse;
    private FXMLLoader fxmlLoader = new FXMLLoader();
    private FXMLLoader fxmlLoader2 = new FXMLLoader();
    private EditChapterController editChapterController;
    private EditCourseController editCourseController;
    private Stage editChapterStage;
    private Stage editCourseStage;
    private int currentCourse;

    private CollectionChapter chapters = CollectionChapter.getInstance();
    private CollectionCourse courses = CollectionCourse.getInstance();
    private Stage mainStage;

    public MainController() {
        courses = courses.load();
        chapters = chapters.load();
    }

    @FXML
    private void initialize(){
        tableCourses.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        columnCourse.setCellValueFactory(new PropertyValueFactory<Course, String>("title"));

        tableTasks.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        columnTitle.setCellValueFactory(new PropertyValueFactory<Chapter, String>("title"));
        columnDuration.setCellValueFactory(new PropertyValueFactory<Chapter, String>("duration"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<Chapter, String>("price"));
        columnComplexity.setCellValueFactory(new PropertyValueFactory<Chapter, String>("complexity"));

        initListener();
        initLoad();
        tableCourses.setItems(courses.getCourseList());
        tableTasks.setItems(chapters.getChapterList(-1));
    }

    private void initListener() {
        tableTasks.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    editChapterController.setChapter((Chapter) tableTasks.getSelectionModel().getSelectedItem());
                    showChapterDialog("Edit chapter");
                }
            }
        });
        tableCourses.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1) {
                    System.out.println();
                    Course t = (Course) tableCourses.getSelectionModel().getSelectedItem();
                    currentCourse = t.getCourseId();
                    tableTasks.setItems(chapters.getChapterList(currentCourse));
                }
            }
        });
    }

    private void initLoad() {
        try {

            fxmlLoader.setLocation(getClass().getResource("../view/dialogChapter.fxml"));
            fxmlEditChapter = fxmlLoader.load();
            editChapterController = fxmlLoader.getController();

            fxmlLoader2.setLocation(getClass().getResource("../view/dialogCourse.fxml"));
            fxmlEditCourse = fxmlLoader2.load();
            editCourseController = fxmlLoader2.getController();

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
            case "btnAddCourse": {
                editCourseController.setCourse(new Course(), courses.getMaxIndex());
                showCourseDialog("Add course");
                courses.add(editCourseController.getCourse());
                break;
            }
            case "btnAddChapter": {
                editChapterController.setChapter(new Chapter());
                showChapterDialog("Add chapter");
                chapters.add(editChapterController.getChapter());
                break;
            }
            case "btnEditCourse": {
                Course selectedCourse = (Course) tableCourses.getSelectionModel().getSelectedItem();
                editCourseController.setCourse(selectedCourse);
                showCourseDialog("Edit course");
                break;
            }
            case "btnEditChapter": {
                Chapter selectedChapter = (Chapter) tableTasks.getSelectionModel().getSelectedItem();
                editChapterController.setChapter(selectedChapter);
                showChapterDialog("Edit chapter");
                break;
            }
            case "btnDeleteCourse":
                courses.delete((Course)tableCourses.getSelectionModel().getSelectedItem());
                break;
            case "btnDeleteChapter":
                chapters.delete((Chapter) tableTasks.getSelectionModel().getSelectedItem());
                break;
            case "btnLogin": {
                jade.core.Runtime rt = jade.core.Runtime.instance();
                Profile p = new ProfileImpl();
                p.setParameter(Profile.MAIN_HOST, "localhost");
                p.setParameter(Profile.MAIN_PORT, "1099");
                p.setParameter(Profile.CONTAINER_NAME, txtTutorName.getText() + "Container");

                ContainerController cc = null;

                cc = rt.createAgentContainer(p);

                Object[] args = { courses, chapters};

                try {
                    cc.createNewAgent(txtTutorName.getText(),
                            "com.b1ngoos.tutor.agent.TutorAgent", args).start();
                } catch (StaleProxyException e) {
                    e.printStackTrace();
                }
            }
        }
        //tableTasks.refresh();
    }

    private void showCourseDialog(String title) {

        if (editCourseStage == null) {
            editCourseStage = new Stage();
            editCourseStage.setResizable(false);
            editCourseStage.setScene(new Scene(fxmlEditCourse));
            editCourseStage.initModality(Modality.WINDOW_MODAL);
            editCourseStage.initOwner(mainStage);
        }
        editCourseStage.setTitle(title);
        editCourseStage.showAndWait();
    }

    private void showChapterDialog(String title) {

        if (editChapterStage ==null) {
            editChapterStage = new Stage();
            editChapterStage.setResizable(false);
            editChapterStage.setScene(new Scene(fxmlEditChapter));
            editChapterStage.initModality(Modality.WINDOW_MODAL);
            editChapterStage.initOwner(mainStage);
        }
        editChapterStage.setTitle(title);

        editChapterStage.showAndWait();
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;

        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                chapters.update();
                courses.update();
            }
        });
    }
}
