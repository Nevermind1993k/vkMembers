package com.rkovaliov.bu.entities;

import com.rkovaliov.bu.resources.Sex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
public class User {

    @Id
    private long id;

    private long likeCount;

    private String name;

    private String lastName;

    private Sex sex;

    public User(long id) {
        this.id = id;
    }

    public User(String name, String lastName, Sex sex) {
        this.name = name;
        this.lastName = lastName;
        this.sex = sex;
    }

    public User(long id, String name, String lastName, Sex sex) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.sex = sex;
    }
}
