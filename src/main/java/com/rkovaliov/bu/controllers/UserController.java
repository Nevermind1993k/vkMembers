package com.rkovaliov.bu.controllers;

import com.rkovaliov.bu.entities.User;
import com.rkovaliov.bu.resources.Sex;
import com.rkovaliov.bu.services.interfaces.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private static final String GET_TOP5_USERS_URL = "/getTopUsers";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = GET_TOP5_USERS_URL)
    public ResponseEntity<Object> showInstructions(@RequestParam("sex") Sex sex) {
        List<User> topUsers = userService.findTopUsers(sex);
        return new ResponseEntity<>(topUsers, HttpStatus.OK);
    }
}
