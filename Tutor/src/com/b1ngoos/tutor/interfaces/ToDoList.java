package com.b1ngoos.tutor.interfaces;

import com.b1ngoos.tutor.models.Chapter;
import com.b1ngoos.tutor.models.CollectionChapter;

public interface ToDoList<T, R> {

    void add(T object);
    void update();
    void delete(T object);
    R load();
}
