package com.b1ngoos.tutor.models.parser;

import javax.xml.bind.JAXBException;
import java.io.File;import java.lang.Class;import java.lang.Object;

public interface Parser {
    Object getObject(File file, Class c) throws JAXBException;
    void saveObject(File file, Object o) throws JAXBException;
}
