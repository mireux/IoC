package com.lhj;

import com.lhj.Annotation.Autowired;
import com.lhj.entity.User;

public class TestAutowired {

    @Autowired
    private static User user;

    public static void main(String[] args) {
        new MyAnnotationConfigApplicationContext("com.lhj");
        System.out.println(user);
    }
}
