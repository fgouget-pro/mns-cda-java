package com.mns.todo.database;

import com.mns.todo.model.Model;

import java.util.ArrayList;
import java.util.List;

public class ModelDatabaseAccess<T extends Model> {

    private final List<T> elements = new ArrayList<>();

    public void addElement(T element) {
        elements.add(element);
    }

    public List<T> getElements() {return elements;}

    public T getElementByID(long id) throws ElementNotFoundException {
        for (T element : elements) {
            if (element.getId() == id){
                return element;
            }
        }
        throw new ElementNotFoundException("The element with id <" + id + " could not be found.");
    }

}
