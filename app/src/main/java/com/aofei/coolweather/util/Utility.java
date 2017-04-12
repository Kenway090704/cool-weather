package com.aofei.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.aofei.coolweather.db.CoolWeatherDB;
import com.aofei.coolweather.model.City;
import com.aofei.coolweather.model.County;
import com.aofei.coolweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Date;
import java.util.Locale;

/**
 * Created by kenway on 17/4/11 18:42
 * Email : xiaokai090704@126.com
 */

public class Utility {
    /**
     * 简析和处理服务器返回的省级数据
     *
     * @param coolWeatherDB
     * @param response
     * @return
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    coolWeatherDB.saveProvince(province);
                }
            }

            return true;

        }

        return false;
    }

    /**
     * 简析和处理服务器返回的市级数据
     *
     * @param coolWeatehrDB
     * @param response
     * @param provinceId
     * @return
     */
    public synchronized static boolean hanleCityResponse(CoolWeatherDB coolWeatehrDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCity = response.split(",");
            if (allCity != null && allCity.length > 0) {
                for (String c : allCity) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //将简析出来的数据存储到City类
                    coolWeatehrDB.saveCity(city);

                }
            }
            return true;
        }
        return false;
    }

    /**
     * 简析和处理服务器返回的县级数据
     *
     * @param coolWeatherDB
     * @param response
     * @param cityId
     * @return
     */
    public synchronized static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounty = response.split(",");
            if (allCounty != null && allCounty.length > 0) {
                for (String cou : allCounty) {
                    String[] array = cou.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
            }
            return false;
        }
        return false;
    }

    /**
     * 处理天气数据
     *
     * @param context
     * @param response
     */
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将获取的天气数据存储到sharedPreferences
     *
     * @param context
     * @param cityName
     * @param weatherCode
     * @param temp1
     * @param temp2
     * @param weatherDesp
     * @param publishTime
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }
}
