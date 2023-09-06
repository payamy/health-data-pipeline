package com.payamy.etl.entity;

import java.io.Serializable;

public class User implements Serializable {

    private Long id;
    private String username;
    private String name;
    private String eye_color;
    private String blood_type;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getEye_color() {
        return eye_color;
    }

    public void setEye_color( String eye_color ) {
        this.eye_color = eye_color;
    }

    public String getBlood_type() {
        return blood_type;
    }

    public void setBlood_type( String blood_type ) {
        this.blood_type = blood_type;
    }
}