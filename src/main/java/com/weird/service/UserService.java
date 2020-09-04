package com.weird.service;

import java.util.List;

public interface UserService {
    List<Integer> getNameById(String name);

    String getNameById(int id);

    boolean checkLogin(String name, String encryptedPassword);
}
