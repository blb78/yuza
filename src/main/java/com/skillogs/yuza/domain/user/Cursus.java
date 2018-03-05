package com.skillogs.yuza.domain.user;

public class Cursus {
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
