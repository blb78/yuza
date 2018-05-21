package com.skillogs.yuza.domain.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document
public class Cursus {
    @Id
    private String id;

    public Cursus(String id) {
        this.id = id;
    }

    public Cursus() {
        //  NOTE: for bean convention
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
        Cursus cursus = (Cursus) o;
        return Objects.equals(id, cursus.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }


    @Override
    public String toString() {
        return "Cursus{" +
                "id='" + id + '\'' +
                '}';
    }
}
