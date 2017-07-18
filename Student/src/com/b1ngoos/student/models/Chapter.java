package com.b1ngoos.student.models;

public class Chapter {

    private String title;
    private int duration;
    private double price;
    private String complexity;
    private int courseId;
    private String tutorName;

    public Chapter(String title, int duration, double price, String complexity, int courseId, String tutorName) {
        this.title = title;
        this.duration = duration;
        this.price = price;
        this.complexity = complexity;
        this.courseId = courseId;
        this.tutorName = tutorName;
    }

    public Chapter(Chapter chapter) {
        this.title = chapter.title;
        this.duration = chapter.duration;
        this.price = chapter.price;
        this.complexity = chapter.complexity;
        this.courseId = chapter.courseId;
        this.tutorName = chapter.tutorName;
    }

    public Chapter(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getComplexity() {
        return complexity;
    }

    public void setComplexity(String complexity) {
        this.complexity = complexity;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "duration=" + duration +
                ", title=" + title +
                '}';
    }
}