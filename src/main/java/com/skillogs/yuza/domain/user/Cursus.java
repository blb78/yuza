package com.skillogs.yuza.domain.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
}
