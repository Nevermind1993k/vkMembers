package com.rkovaliov.bu.services.interfaces;

import com.rkovaliov.bu.entities.User;
import com.rkovaliov.bu.resources.Sex;

import java.util.List;

public interface UserService {

    List<User> findTopUsers(Sex sex);
}
