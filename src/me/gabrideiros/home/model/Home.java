package me.gabrideiros.home.model;


import me.gabrideiros.home.enums.Type;

public class Home {

    private String name;
    private String location;
    private Type type;

    public Home(String name, String location, Type type) {
        this.name = name;
        this.location = location;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
