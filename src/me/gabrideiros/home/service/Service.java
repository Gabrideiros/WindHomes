package me.gabrideiros.home.service;

import me.gabrideiros.home.model.User;

public interface Service {

    void load();

    void insert(User user);

    void update(User user);

    void delete(User user);
}
