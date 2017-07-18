package com.b1ngoos.tutor.models;

import javafx.beans.property.SimpleStringProperty;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

public class Chapter {

    private SimpleStringProperty title = new SimpleStringProperty("");
    private SimpleStringProperty duration = new SimpleStringProperty("");
    private SimpleStringProperty price = new SimpleStringProperty("");
    private SimpleStringProperty complexity = new SimpleStringProperty("");
    private int courseId;

    public Chapter(int courseId, String title, String duration, String price, String complexity) {
        this.courseId = courseId;
        this.title = new SimpleStringProperty(title);
        this.duration = new SimpleStringProperty(duration);
        this.price = new SimpleStringProperty(price);
        this.complexity = new SimpleStringProperty(complexity);
    }

    public Chapter(){}

    public int getCourseId() {
        return courseId;
    }

    @XmlAttribute(name = "courseId")
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    @XmlElement(name = "duration")
    public void setDuration(String duration) {
        this.duration.set(duration);
    }

    @XmlElement(name = "title")
    public void setTitle(String title) { this.title.set(title); }

    @XmlElement(name = "price")
    public void setPrice(String price) { this.price.set(price); }

    @XmlElement(name = "complexity")
    public void setComplexity(String complexity) { this.complexity.set(complexity); }

    public String getDuration() {
        return duration.get();
    }

    public String getTitle() {
        return title.get();
    }

    public String getPrice() {
        return price.get();
    }

    public String getComplexity() {
        return complexity.get();
    }

    public  SimpleStringProperty titleProperty(){ return title; }

    public  SimpleStringProperty durationProperty(){ return duration; }

    public  SimpleStringProperty priceProperty(){ return price; }

    public  SimpleStringProperty complexityProperty() {
        return complexity;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "duration=" + duration +
                ", title=" + title +
                '}';
    }
}
