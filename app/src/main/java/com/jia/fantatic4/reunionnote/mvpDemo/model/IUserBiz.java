package com.jia.fantatic4.reunionnote.mvpDemo.model;

import com.jia.fantatic4.reunionnote.mvpDemo.listener.LoginListener;

/**
 * Created by jia on 2017/3/7.
 */

public interface IUserBiz {
     void login(String name, String password, LoginListener listener);
}
