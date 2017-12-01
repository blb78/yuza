package com.skillogs.yuza.domain.user;

import com.skillogs.yuza.domain.Course;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document
public class Classroom {

    @Id
    private String id;
    private String name;
    private Set<Teacher> teachers = new HashSet<>();
    private Set<Student> students = new HashSet<>();
    private Set<Course> courses = new HashSet<>();

    public Classroom(String id) {
        this.id = id;
    }

    public Classroom() {
        // NOTE: for bean convention
    }


    public void add(Student student) {
        this.students.add(student);
    }

    public void add(Course course) {
        this.courses.add(course);
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

        Classroom classroom = (Classroom) o;

        return id != null ? id.equals(classroom.id) : classroom.id == null;
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

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }


}
