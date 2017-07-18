package com.b1ngoos.tutor.models;

import com.b1ngoos.tutor.interfaces.ToDoList;
import com.b1ngoos.tutor.models.parser.JaxbParser;
import com.b1ngoos.tutor.models.parser.Parser;
import com.b1ngoos.tutor.start.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;

@XmlRootElement(name = "courses")
@XmlAccessorType(XmlAccessType.NONE)
public class CollectionCourse implements ToDoList<Course, CollectionCourse> {

    public static String FILE_NAME = Main.fileName + "_course.xml";
    private static CollectionCourse instance = new CollectionCourse();

    public static synchronized CollectionCourse getInstance() {
        if(instance == null) {
            instance = new CollectionCourse();
        }

        return instance;
    }

    @XmlElement(name = "course")
    private ObservableList<Course> courseList = FXCollections.observableArrayList();

    private CollectionCourse() {
    }

    @Override
    public void add(Course object) {
        courseList.add(object);
    }

    @Override
    public void update() {
        try {

            new JaxbParser().saveObject(new File(FILE_NAME), this);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Course object) {
        courseList.remove(object);
    }

    @Override
    public CollectionCourse load() {
        Parser parser = new JaxbParser();
        CollectionCourse courses = CollectionCourse.getInstance();
        try {

            courses = (CollectionCourse) parser.getObject(new File(FILE_NAME), CollectionCourse.class);

        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return courses;
    }

    public ObservableList<Course> getCourseList() {
        return courseList;
    }

    public int getMaxIndex() {

        int max = 0;
        for(Course c: getCourseList()){
            if(c.getCourseId() > max)
                max = c.getCourseId();
        }
        return max + 1;
    }
}
