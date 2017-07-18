package com.b1ngoos.tutor.models;

import javafx.beans.property.SimpleStringProperty;

import javax.xml.bind.annotation.XmlElement;

public class Course {

    private int courseId;
    private SimpleStringProperty title = new SimpleStringProperty("");

    public Course(int courseId, String title) {
        this.courseId = courseId;
        this.title = new SimpleStringProperty(title);
    }

    public Course(){ }

    public int getCourseId() {
        return courseId;
    }

    @XmlElement(name = "courseId")
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    @XmlElement(name = "title")
    public void setTitle(String title) { this.title.set(title); }

    public String getTitle() {
        return title.get();
    }

    public  SimpleStringProperty titleProperty(){ return title; }

    @Override
    public String toString() {
        return getTitle();
    }
}
