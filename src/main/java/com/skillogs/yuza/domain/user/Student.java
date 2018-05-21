package com.skillogs.yuza.domain.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Student {
    @Id
    private String id;

    public Student() {
        // NOTE: for bean convention
    }

    public Student(String id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        return id.equals(student.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
