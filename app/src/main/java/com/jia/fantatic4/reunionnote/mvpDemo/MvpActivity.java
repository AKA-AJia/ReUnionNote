package com.jia.fantatic4.reunionnote.mvpDemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jia.fantatic4.reunionnote.R;
import com.jia.fantatic4.reunionnote.mvpDemo.impl.PresenterCompl;
import com.jia.fantatic4.reunionnote.mvpDemo.model.UserBean;
import com.jia.fantatic4.reunionnote.mvpDemo.view.ILoginView;

/**
 * Created by jia on 2017/3/7.
 */

public class MvpActivity extends AppCompatActivity implements ILoginView{

    private EditText mEt_name;
    private EditText mEt_password;
    private Button mBtn_login;
    private Button mBtn_clear;

    private PresenterCompl presenter = new PresenterCompl(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvp);
        initView();
        initListener();
    }



    private void initView() {
        mEt_name = (EditText) findViewById(R.id.ed_mvp_name);
        mEt_password = (EditText) findViewById(R.id.ed_mvp_password);
        mBtn_login = (Button) findViewById(R.id.btn_mvp_login);
        mBtn_clear = (Button) findViewById(R.id.btn_mvp_clear);
    }

    private void initListener() {
        mBtn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.login();
            }
        });

        mBtn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.clear();
            }
        });
    }

    @Override
    public String getUserName() {
        return mEt_name.getText().toString().trim();
    }

    @Override
    public String getPassword() {
        return mEt_password.getText().toString().trim();
    }

    @Override
    public void clearUserName() {
        mEt_name.setText("");
    }

    @Override
    public void clearPassword() {
        mEt_password.setText("");
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void toMainActivity(UserBean user) {
        Toast.makeText(this,"toMainActivity",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFailedError() {
        Toast.makeText(this,"login fail",Toast.LENGTH_SHORT).show();
    }
}
