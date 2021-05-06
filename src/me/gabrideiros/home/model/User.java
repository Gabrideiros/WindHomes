package me.gabrideiros.home.model;

import me.gabrideiros.home.enums.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User {

    private String name;
    private Map<String, Home> homes;

    public User(String name, Map<String, Home> homes) {
        this.name = name;
        this.homes = homes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Home> getHomes() {
        return homes;
    }

    public void setHomes(Map<String, Home> homes) {
        this.homes = homes;
    }

    public List<String> getPublic() {

        List<String> publics = new ArrayList<>();

        for (Map.Entry<String, Home> entry : homes.entrySet()) {

            if (entry.getValue().getType().equals(Type.PUBLIC)) publics.add(entry.getKey());

        }

        return publics;

    }

    public List<String> getPrivate() {

        List<String> privates = new ArrayList<>();

        for (Map.Entry<String, Home> entry : homes.entrySet()) {

            if (entry.getValue().getType().equals(Type.PRIVATE)) privates.add(entry.getKey());

        }

        return privates;
    }
}
