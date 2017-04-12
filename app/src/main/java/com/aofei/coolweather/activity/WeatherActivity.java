package com.aofei.coolweather.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aofei.coolweather.R;

/**
 * Created by kenway on 17/4/12 14:38
 * Email : xiaokai090704@126.com
 */

public class WeatherActivity extends AppCompatActivity {
    private LinearLayout weatherInfoLayout;

    /**
     * 用于显示城市名
     */
   private TextView cityNameText;

    /**
     * 用于显示发布时间
     */
    private TextView publishText;

    /**
     * 用于显示天气描述信息
     */
    private TextView weatherDespText;
    /**
     * 用于显示气温1
     */
    private TextView temp1Text;
    /**
     * 用于显示气温2
     */
    private TextView temp2Text;

    /**
     * 用于显示当前日前
     */
    private TextView currentDateText;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.weather_layout);
    }
}
