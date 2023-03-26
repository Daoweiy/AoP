package com.daowei.service.impl;

import com.daowei.anno.Bean;
import com.daowei.anno.Di;
import com.daowei.dao.UserDao;
import com.daowei.service.UserService;
@Bean
public class UserServiceImpl implements UserService {
    @Di
    private UserDao userDao;

    @Override
    public void add() {
        System.out.println("service......");
        userDao.add();
    }
}
