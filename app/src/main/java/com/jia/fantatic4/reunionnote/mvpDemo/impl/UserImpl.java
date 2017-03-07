package com.jia.fantatic4.reunionnote.mvpDemo.impl;

import com.jia.fantatic4.reunionnote.mvpDemo.listener.LoginListener;
import com.jia.fantatic4.reunionnote.mvpDemo.model.IUserBiz;
import com.jia.fantatic4.reunionnote.mvpDemo.model.UserBean;

/**
 * Created by as on 2017/3/7.
 * model业务层的具体处理
 */

public class UserImpl implements IUserBiz{
    @Override
    public void login(final String name, final String password, final LoginListener loginListener) {
        //模拟耗时操作
        new Thread(){

            @Override
            public void run() {
                //休眠两秒
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (name.equals("ajia")&&password.equals("686389")){
                    UserBean user =new UserBean();
                    user.setUserName(name);
                    user.setPassword(password);
                    loginListener.loginSuccess(user);
                }else {
                    loginListener.loginFailed();
                }
            }
        }.start();
    }
}
