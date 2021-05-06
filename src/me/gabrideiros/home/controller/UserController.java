package me.gabrideiros.home.controller;

import me.gabrideiros.home.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserController implements Controller{

    private final Map<String, User> map;

    public UserController() {
        this.map = new HashMap<>();
    }

    @Override
    public User get(String name) {
        return map.get(name);
    }

    @Override
    public Optional<User> getByName(String name) {
        return map.values().stream().filter(user -> user.getName().equals(name)).findAny();
    }

    @Override
    public void add(User user) {
        map.put(user.getName(), user);
    }

    @Override
    public void remove(User user) {
        map.remove(user.getName());
    }

    public Map<String, User> getMap() { return map; }
}
