package com.jia.fantatic4.reunionnote.mvpDemo.impl;

import android.os.Handler;

import com.jia.fantatic4.reunionnote.mvpDemo.listener.LoginListener;
import com.jia.fantatic4.reunionnote.mvpDemo.model.UserBean;
import com.jia.fantatic4.reunionnote.mvpDemo.presenter.ILoginPresenter;
import com.jia.fantatic4.reunionnote.mvpDemo.view.ILoginView;


/**
 * Created by jia on 2017/3/7.
 * presenter具体实现类
 */

public class PresenterCompl implements ILoginPresenter {

    private ILoginView loginView;
    private UserImpl userImpl;
    private Handler mHandler = new Handler();

    public PresenterCompl(ILoginView loginView) {
        this.loginView = loginView;
        this.userImpl = new UserImpl();
    }


    //对view层的具体处理
    @Override
    public void login() {
        loginView.showLoading();
        userImpl.login(loginView.getUserName(), loginView.getPassword(), new LoginListener() {
            @Override
            public void loginSuccess(final UserBean user) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        loginView.toMainActivity(user);
                        loginView.hideLoading();
                    }
                });
            }

            @Override
            public void loginFailed() {
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        loginView.showFailedError();
                        loginView.hideLoading();
                    }
                });
            }
        });
    }

    @Override
    public void clear() {
        loginView.clearUserName();
        loginView.clearPassword();
    }
}
