package com.jia.fantatic4.reunionnote.mvpDemo.listener;

import com.jia.fantatic4.reunionnote.mvpDemo.model.UserBean;

/**
 * Created by jia on 2017/3/7.
 */

public interface LoginListener {
    void loginSuccess(UserBean user);

    void loginFailed();
}
