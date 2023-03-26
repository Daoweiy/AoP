package com.daowei.dao.impl;

import com.daowei.anno.Bean;

import com.daowei.dao.UserDao;

@Bean
public class UserDaoImpl implements UserDao {


    @Override
    public void add() {
        System.out.println("dao ......");
    }
}
