package com.skillogs.yuza.domain.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document
public class Promotion {

    @Id
    private String id;
    private String name;
    private Set<Teacher> teachers = new HashSet<>();
    private Set<Student> students = new HashSet<>();
    private Cursus cursus;

    public Promotion(String id) {
        this.id = id;
    }

    public Promotion() {
        // NOTE: for bean convention
    }


    public void add(Student student) {
        this.students.add(student);
    }

    public void add(Teacher teacher) {
        this.teachers.add(teacher);
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

        Promotion promotion = (Promotion) o;

        return id != null ? id.equals(promotion.id) : promotion.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(Set<Teacher> teachers) {
        this.teachers = teachers;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    public void remove(Teacher teacher) {
        this.teachers.remove(teacher);
    }

    public void remove(Student student) {
        this.students.remove(student);
    }

    public Cursus getCursus() {
        return cursus;
    }

    public void setCursus(Cursus cursus) {
        this.cursus = cursus;
    }
}
