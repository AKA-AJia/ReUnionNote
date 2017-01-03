package com.jia.fantatic4.reunionnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jia.fantatic4.reunionnote.activity.WeatherActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_material_design)
    Button btnMaterialDesign;
    @BindView(R.id.btn_weather)
    Button btnWeather;
    @BindView(R.id.btn_im)
    Button btnIm;
    @BindView(R.id.btn_lbs)
    Button btnLbs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_material_design, R.id.btn_weather, R.id.btn_im, R.id.btn_lbs})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_material_design:
                Toast.makeText(this,"material design",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_weather:
                Intent intent=new Intent(this, WeatherActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_im:
                Toast.makeText(this,"IM",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_lbs:
                Toast.makeText(this,"LBS",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
