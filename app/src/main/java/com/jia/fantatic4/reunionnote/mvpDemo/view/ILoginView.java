package com.jia.fantatic4.reunionnote.mvpDemo.view;

import com.jia.fantatic4.reunionnote.mvpDemo.model.UserBean;

/**
 * Created jia as on 2017/3/7.
 * view层业务接口
 */

public interface ILoginView {
    String getUserName();

    String getPassword();

    void clearUserName();

    void clearPassword();

    void showLoading();

    void hideLoading();

    void toMainActivity(UserBean user);

    void showFailedError();
}
