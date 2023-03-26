package com.daowei;

import com.daowei.bean.AnnotationApplicationContext;
import com.daowei.bean.ApplicationContext;
import com.daowei.dao.UserDao;
import com.daowei.service.UserService;

public class TestUser {
    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationApplicationContext("com.daowei");
        UserService userService= (UserService) context.getBean(UserService.class);
        System.out.println(userService);
        userService.add();
    }
}
