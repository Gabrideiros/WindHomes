package me.gabrideiros.home.controller;

import me.gabrideiros.home.model.User;

import java.util.Optional;

public interface Controller {

    User get(String name);
    Optional<User> getByName(String name);

    void add(User user);

    void remove(User user);
}
