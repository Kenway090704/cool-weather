package com.aofei.coolweather.util;

import android.text.TextUtils;

import com.aofei.coolweather.db.CoolWeatherDB;
import com.aofei.coolweather.model.City;
import com.aofei.coolweather.model.County;
import com.aofei.coolweather.model.Province;

import org.w3c.dom.Text;

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
}
