package com.skillogs.yuza.domain.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Teacher {

    @Id
    private String id;


    public Teacher(String id) {
        this.id = id;
    }

    public Teacher() {
        // NOTE: for bean convention
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Teacher teacher = (Teacher) o;

        return id.equals(teacher.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
