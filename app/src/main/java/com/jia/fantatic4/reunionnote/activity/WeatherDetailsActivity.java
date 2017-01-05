package com.jia.fantatic4.reunionnote.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.fantatic4.reunionnote.R;
import com.jia.fantatic4.reunionnote.bean.Forecast;
import com.jia.fantatic4.reunionnote.bean.Weather;
import com.jia.fantatic4.reunionnote.constant.Constant;
import com.jia.fantatic4.reunionnote.utils.HttpUtil;
import com.jia.fantatic4.reunionnote.utils.Utility;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by jia on 2017/1/5.
 */

public class WeatherDetailsActivity extends AppCompatActivity {

    @BindView(R.id.tv_weather_title)
    TextView tvWeatherTitle;
    @BindView(R.id.tv_weather_current_time)
    TextView tvWeatherCurrentTime;
    @BindView(R.id.tv_now_tmp)
    TextView tvNowTmp;
    @BindView(R.id.tv_now_status)
    TextView tvNowStatus;
    @BindView(R.id.tv_forecast_time)
    TextView tvForecastTime;
    @BindView(R.id.tv_forecast_status)
    TextView tvForecastStatus;
    @BindView(R.id.tv_forecast_max_tmp)
    TextView tvForecastMaxTmp;
    @BindView(R.id.tv_forecast_min_tmp)
    TextView tvForecastMinTmp;
    @BindView(R.id.tv_aqi_date)
    TextView tvAqiDate;
    @BindView(R.id.tv_pm25_date)
    TextView tvPm25Date;
    @BindView(R.id.tv_suggestion_comfort)
    TextView tvSuggestionComfort;
    @BindView(R.id.tv_suggestion_carwash)
    TextView tvSuggestionCarwash;
    @BindView(R.id.tv_suggestion_sport)
    TextView tvSuggestionSport;
    @BindView(R.id.sc_weather)
    ScrollView scWeather;
    @BindView(R.id.ll_forecast)
    LinearLayout llForecast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather_detail);
        ButterKnife.bind(this);

        initSpData();
    }

    /**
     * 优先查询sp中是否有缓存
     */
    private void initSpData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherData = prefs.getString("weather", null);
        if (weatherData != null) {
            Weather weather = Utility.handleWeatherResponse(weatherData);
            showWeatherInfo(weather);
        } else {
            String weather_id = getIntent().getStringExtra("weather_id");
            scWeather.setVisibility(View.INVISIBLE);
            requestWeather(weather_id);
        }
    }

    private void requestWeather(String weather_id) {
        String weatherUrl = Constant.WEATHER_BASE_URL + weather_id + Constant.WEATHER_KEY;
        System.out.print("url==========="+weatherUrl);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherDetailsActivity.this,
                                "获取天气信息失败...", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String weatherData = response.body().toString();
                Weather weatherInfo = Utility.handleWeatherResponse(weatherData);
                if (weatherInfo != null && "ok".equals(weatherInfo.status)) {
                    SharedPreferences.Editor editor =
                            PreferenceManager.getDefaultSharedPreferences(WeatherDetailsActivity.this)
                                    .edit();
                    editor.putString("weather", weatherData);
                    editor.apply();
                    showWeatherInfo(weatherInfo);
                }
            }
        });
    }

    /**
     * 显示天气数据到界面
     *
     * @param weatherInfo 天气bean
     */
    private void showWeatherInfo(Weather weatherInfo) {
        String cityName = weatherInfo.basic.cityName;
        String updateTime = weatherInfo.basic.update.updateTime.split(" ")[1];
        String tmp = weatherInfo.now.temperature + "°C";
        String now_status = weatherInfo.now.more.info;
        tvWeatherTitle.setText(cityName);
        tvWeatherCurrentTime.setText(updateTime);
        tvNowTmp.setText(tmp);
        tvNowStatus.setText(now_status);

        llForecast.removeAllViews();
        for (Forecast forecast : weatherInfo.forecastList) {
            View view =LayoutInflater
                    .from(this).inflate(R.layout.forecast_weather,llForecast,false);
            TextView tv_date= (TextView) view.findViewById(R.id.tv_forecast_status);
            TextView tv_stutas= (TextView) view.findViewById(R.id.tv_forecast_time);
            TextView tv_max= (TextView) view.findViewById(R.id.tv_forecast_max_tmp);
            TextView tv_min= (TextView) view.findViewById(R.id.tv_forecast_min_tmp);

            tv_date.setText(forecast.date);
            tv_stutas.setText(forecast.more.info);
            tv_max.setText(forecast.temperature.max);
            tv_min.setText(forecast.temperature.min);

            llForecast.addView(view);
        }

        if (weatherInfo.aqi!=null){
            tvAqiDate.setText(weatherInfo.aqi.city.aqi);
            tvPm25Date.setText(weatherInfo.aqi.city.pm25);
        }

        String comfort="舒适度："+weatherInfo.suggestion.comfort.info;
        String carWash="洗车指数："+weatherInfo.suggestion.carWash.info;
        String sport="运动建议："+weatherInfo.suggestion.sport.info;

        tvSuggestionComfort.setText(comfort);
        tvSuggestionCarwash.setText(carWash);
        tvSuggestionSport.setText(sport);

        scWeather.setVisibility(View.VISIBLE);
    }
}
