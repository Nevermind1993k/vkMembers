package com.rkovaliov.bu.services.impl;

import com.rkovaliov.bu.entities.User;
import com.rkovaliov.bu.repositories.UserDAO;
import com.rkovaliov.bu.resources.Sex;
import com.rkovaliov.bu.services.interfaces.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public List<User> findTopUsers(Sex sex) {
        return userDAO.findTop5BySexOrderByLikeCountDesc(sex);
    }
}
