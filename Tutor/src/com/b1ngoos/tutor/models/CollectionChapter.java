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
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@XmlRootElement(name = "chapters")
@XmlAccessorType(XmlAccessType.NONE)
public class CollectionChapter implements ToDoList<Chapter, CollectionChapter> {

    public static String FILE_NAME = Main.fileName + "_chapter.xml";
    private static CollectionChapter instance = null;

    public static synchronized CollectionChapter getInstance() {
        if(instance == null) {
            instance = new CollectionChapter();
        }

        return instance;
    }

    @XmlElement(name = "chapter")
    private ObservableList<Chapter> chapterList = FXCollections.observableArrayList();

    private CollectionChapter() {
    }

    @Override
    public CollectionChapter load() {

        Parser parser = new JaxbParser();
        CollectionChapter chapters = CollectionChapter.getInstance();
        try {

            chapters = (CollectionChapter) parser.getObject(new File(FILE_NAME), CollectionChapter.class);

        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return chapters;
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
    public void add(Chapter chapter) {
        chapterList.add(chapter);
    }

    @Override
    public void delete(Chapter chapter) {
        chapterList.remove(chapter);
    }

    public ObservableList<Chapter> getChapterList(int id) {

        if (id != -1) {
            Predicate<Chapter> selectChapterFilter
                    = p -> p.getCourseId() == id;

            List<Chapter> selectedChapters = chapterList.stream()
                    .filter(selectChapterFilter)
                    .collect(Collectors.toList());
            //System.out.println("WWWWW " + selectedChapters.size());
            return FXCollections.observableArrayList(selectedChapters);
        } else {
            return chapterList;
        }
    }
}