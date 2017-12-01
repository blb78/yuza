package com.skillogs.yuza.repository;

import com.skillogs.yuza.domain.Course;

public interface CourseRepository {
    Course findOne(String id);
    Course create(Course course);
}
